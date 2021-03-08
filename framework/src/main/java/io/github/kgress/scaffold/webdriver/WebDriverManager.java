package io.github.kgress.scaffold.webdriver;

import static io.github.kgress.scaffold.models.enums.desktop.RunType.GRID;
import static io.github.kgress.scaffold.models.enums.desktop.RunType.HEADLESS;
import static io.github.kgress.scaffold.models.enums.desktop.RunType.SAUCE;
import static io.github.kgress.scaffold.models.enums.desktop.RunType.SAUCE_MOBILE_EMULATOR;
import static io.github.kgress.scaffold.models.enums.desktop.RunType.UNIT;
import static io.github.kgress.scaffold.models.enums.desktop.ScreenResolution.ScreenResolutionType.SAUCELABS;
import static io.github.kgress.scaffold.models.enums.desktop.ScreenResolution.ScreenResolutionType.SELENIUM;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName.ANDROID;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName.CHROME;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobileBrowserName.SAFARI;
import static io.github.kgress.scaffold.models.enums.mobileemulator.MobilePlatform.IOS;
import static io.github.kgress.scaffold.util.AutomationUtils.getStackTrace;
import static java.util.concurrent.TimeUnit.SECONDS;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties.MobileEmulator;
import io.github.kgress.scaffold.environment.config.SeleniumGridServiceConfiguration;
import io.github.kgress.scaffold.exception.WebDriverContextException;
import io.github.kgress.scaffold.exception.WebDriverManagerException;
import io.github.kgress.scaffold.models.GridSessionRequest;
import io.github.kgress.scaffold.models.GridSessionResponse;
import io.github.kgress.scaffold.models.enums.desktop.RunType;
import io.github.kgress.scaffold.models.enums.mobileemulator.MobilePlatform;
import io.github.kgress.scaffold.models.unittests.MockWebDriver;
import io.github.kgress.scaffold.webdriver.interfaces.TestContextSetting;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This class manages {@link WebDriverWrapper} instances on a per thread basis. This is useful for
 * multi-threaded tests to be able to seamlessly access their associated WebDriver instance
 * statically.
 * <p>
 * The startLock and closeLock properties: We want locks to open *and* close WebDriver instances
 * when we are using Selenium Grid as we do not want a large amount of open/close requests flooding
 * the hub at once.  This way, they will be processed serially.
 * <p>
 * As of version 2.11.0, I've added some experimental sauce mobile emulator functionality. Sauce
 * uses Appium as the driver and, after some initial testing, it appears some devices (mostly older
 * devices by the look of it) do not translate clicks to taps. Furthermore, the implicit timeout
 * must be higher due to the slower speeds from Appium. 30 seconds has provided more than ample time
 * to allow for Appium's slower responses. However, this increases the time of failure for a test.
 * We'll monitor the performance and make improvements along the way. Perhaps we could allow for a
 * user based setting, much like the {@link WebDriverWrapper#getSeleniumObjectTimeout()}. It might
 * be prudent to allow a timeout field as a desired capability. Or even, perhaps, the implicit waits
 * timeout as set during {@link #initDriver(String)}.
 * <p>
 * We have an opportunity to also explore desktop mobile view. For example, it's entirely possible
 * to set an experimental option on chrome to change its view to a mobile device, using the
 * mobileEmulation capability. We can set this to, say, Nexus 5 and it will automatically load the
 * proper screen resolution for us. It doesn't require Appium and is much quicker.
 * <p>
 * Additional feedback from end users is highly appreciated as we continue to experiment with mobile
 * emulation.
 */
@Slf4j
@Service
public class WebDriverManager {

  private static final String GRID_TEST_SESSION_URI = "/grid/api/testsession";
  private static final Long TEN_SECONDS = 10L;
  private static final Long THIRTY_SECONDS = 30L;
  private static final String SCREEN_RESOLUTION_CAPABILITY = "screenResolution"; //conforms to selenium capability standard

  @Getter
  private final RestTemplate seleniumGridRestTemplate;

  @Getter
  private final DesiredCapabilitiesConfigurationProperties desiredCapabilities;

  @Getter
  private WebDriverWrapper webDriverWrapper;

  private final Object startLock = new Object();
  private final Object closeLock = new Object();

  @Autowired
  public WebDriverManager(DesiredCapabilitiesConfigurationProperties desiredCapabilities,
      RestTemplate seleniumGridRestTemplate) {
    this.desiredCapabilities = desiredCapabilities;
    this.seleniumGridRestTemplate = seleniumGridRestTemplate;
  }

  /**
   * Creates a new instance of a {@link WebDriver}, wraps it into a {@link WebDriverWrapper}, and
   * sets an implicit timeout.
   * <p>
   * 1. Check if the {@link WebDriverWrapper} is null from the {@link WebDriverContext}. If it's
   * null, the driver already exists and we've encountered a threading issue. 2. Configure the new
   * browser driver. 3. Create the new {@link WebDriverWrapper} with the browser driver. 4. Setup
   * implicit waits on the driver to make it easier to interact with elements.
   *
   * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter
   *                 annotations.
   */
  public void initDriver(String testName) {
    if (webDriverWrapper != null) {
      throw new WebDriverContextException(
          "Driver already exists. Try closing/quitting it before trying to initialize a new one");
    }
    var browserDriver = configureBrowserDriver(testName);
    webDriverWrapper = new WebDriverWrapper(browserDriver);

    // Configure the browser to implicitly wait anytime a user attempts to locate an element. If
    // using mobile emulator, we need to kick this timeout up quite a bit since it's powered by
    // appium. Otherwise, a 10 second timeout on desktop has proven to be good.
    if (getDesiredCapabilities().getRunType().equals(SAUCE_MOBILE_EMULATOR)) {
      webDriverWrapper.manage().timeouts().implicitlyWait(THIRTY_SECONDS, SECONDS);
    } else {
      webDriverWrapper.manage().timeouts().implicitlyWait(TEN_SECONDS, SECONDS);
    }
  }

  /**
   * Closes the current {@link WebDriver}.
   * <p>
   * 1. Check if the {@link WebDriverWrapper} is not null. If it's not null, proceed with the
   * closing of the driver. 2. Quit the driver on the thread.
   */
  public void closeDriver() {
    if (webDriverWrapper != null) {
      try {
        synchronized (closeLock) {
          webDriverWrapper.quit();
        }
      } catch (Exception e) {
        log.error("Error quitting browser: " + getStackTrace(e));
      }
      webDriverWrapper = null;
    }
  }

  /**
   * Configures a new instance of a browser driver. First checks the {@link
   * DesiredCapabilitiesConfigurationProperties} to detect a desktop or mobile test execution.
   * Afterwards, creates {@link MutableCapabilities} and configures either a remote browser or local
   * browser based on the {@link DesiredCapabilitiesConfigurationProperties#getRunType()}.
   *
   * @return as {@link WebDriver}
   */
  private WebDriver configureBrowserDriver(String testName) {
    var browserOptions = configureBrowserOptions();
    return configureWebDriver(browserOptions, testName);
  }

  /**
   * Checks to determine if the desired capabilities set by the spring profile confer with a desktop
   * execution or a mobile emulator execution. Once determined, returns a set of {@link
   * MutableCapabilities} for either a desktop configuration or mobile configuration.
   * <p>
   * To check, pull in the {@link DesiredCapabilitiesConfigurationProperties#getRunPlatform()} and
   * {@link MobileEmulator#getRunPlatform()}. Check to ensure the following conditions:
   * <p>
   * 1. Both runPlatform and platformName should not be provided. Only one or the other should be
   * provided. 2. If runPlatform exists and platformName doesn't, configure a desktop browser. 3. If
   * platformName exists and runPlatform doesn't, configure a mobile emulator browser. 4. If neither
   * exists, throw an error asking to provide at least one or the other.
   *
   * @return depending on the resolution of the logic above, this method returns {@link
   * MutableCapabilities} from either {@link #configureDesktopBrowserOptions()} or {@link
   * #configureMobileEmulatorCapabilities()}.
   */
  private MutableCapabilities configureBrowserOptions() {
    MutableCapabilities mutableCapabilities;
    var runPlatform = Optional.ofNullable(getDesiredCapabilities().getRunPlatform());
    var mobilePlatform = Optional
        .ofNullable(getDesiredCapabilities().getMobile().getPlatformName());

    if (runPlatform.isPresent() && mobilePlatform.isEmpty()) {
      log.debug("Desktop configuration detected.");
      validateRequiredDesktopBrowserCapabilities();
      mutableCapabilities = configureDesktopBrowserOptions();
    } else if (runPlatform.isEmpty() && mobilePlatform.isPresent()) {
      log.debug("Mobile configuration detected.");
      validateRequiredMobileEmulatorCapabilities();
      mutableCapabilities = configureMobileEmulatorCapabilities();
    } else if (runPlatform.isPresent() && mobilePlatform.isPresent()) {
      throw new WebDriverManagerException(String.format(
          "Desktop and mobile emulation configuration detected! "
              + "runPlatform: %s. mobilePlatform: %s. Please check your configuration and try again.",
          runPlatform.get().getPlatform(), mobilePlatform.get().getPlatform()
      ));
    } else {
      throw new WebDriverManagerException(
          "A desktop or mobile platform could not be found. "
              + "Please check your configuration and try again.");
    }
    return mutableCapabilities;
  }

  /**
   * Checks the required values from the {@link DesiredCapabilitiesConfigurationProperties} to
   * ensure they are not null.
   */
  private void validateRequiredDesktopBrowserCapabilities() {
    var runType = Optional
        .ofNullable(getDesiredCapabilities().getRunType());
    var runPlatform = Optional
        .ofNullable(getDesiredCapabilities().getRunPlatform());
    var browserType = Optional
        .ofNullable(getDesiredCapabilities().getBrowserType());
    var screenResolution = Optional
        .ofNullable(getDesiredCapabilities().getScreenResolution());

    if (runPlatform.isEmpty()) {
      throw new WebDriverManagerException(
          "Run Platform must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
    if (browserType.isEmpty()) {
      throw new WebDriverManagerException(
          "Browser type must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
    // In case screen resolution ever is not set as default, this will catch the change in the future.
    if (screenResolution.isEmpty()) {
      throw new WebDriverManagerException(
          "Screen Resolution must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
    // In case run type ever is not checked by lombok, this will catch the change in the future.
    if (runType.isEmpty()) {
      throw new WebDriverManagerException(
          "Run Type must be defined when initiating a desktop web driver configuration. "
              + "Please check your configuration and try again.");
    }
  }

  /**
   * Checks the required values from the {@link MobileEmulator} to ensure they are not null. Also
   * checks to ensure a mismatch is not present between the platform and browser. We are opting not
   * to perform error checks against the device name, os, and browser name since there an exorbitant
   * amount of combinations. Instead, we will surface the sauce error to the user.
   */
  private void validateRequiredMobileEmulatorCapabilities() {
    var deviceName = Optional
        .ofNullable(getDesiredCapabilities().getMobile().getSauceDeviceName());
    var browserName = Optional
        .ofNullable(getDesiredCapabilities().getMobile().getBrowserName());
    var platformName = Optional
        .ofNullable(getDesiredCapabilities().getMobile().getPlatformName());

    var mismatchedBrowserAndPlatformException =
        new WebDriverManagerException(String.format(
            "Operating system and browser mismatch: platformName = %s, browserName = %s. "
                + "Please check your configuration and try again.",
            platformName, browserName));

    if (deviceName.isEmpty()) {
      throw new WebDriverManagerException(
          "Device Name must be defined when initiating a mobile emulator web driver configuration. "
              + "Please check your configuration and try again.");
    }
    if (browserName.isEmpty()) {
      throw new WebDriverManagerException(
          "Browser Name must be defined when initiating a mobile emulator web driver configuration. "
              + "Please check your configuration and try again.");
    }
    if (platformName.isEmpty()) {
      throw new WebDriverManagerException(
          "Platform Name must be defined when initiating a mobile emulator web driver configuration. "
              + "Please check your configuration and try again.");
    }

    // Some high level validation so we can fail faster
    if ((platformName.get() == MobilePlatform.ANDROID) && (browserName.get() == SAFARI)) {
      throw mismatchedBrowserAndPlatformException;
    }

    if ((platformName.get() == IOS) && (browserName.get() == CHROME)) {
      throw mismatchedBrowserAndPlatformException;
    }

    if ((platformName.get() == IOS) && (browserName.get() == ANDROID)) {
      throw mismatchedBrowserAndPlatformException;
    }
  }

  /**
   * Configures a new {@link WebDriver} with {@link MutableCapabilities} from either {@link
   * #configureDesktopBrowserOptions()} or {@link #configureMobileEmulatorCapabilities()}.
   *
   * @param browserOptions the {@link MutableCapabilities} to be used when configuring the web
   *                       driver
   * @param testName       the name of the test being executed
   * @return as {@link WebDriver}
   */
  private WebDriver configureWebDriver(MutableCapabilities browserOptions, String testName) {
    WebDriver webDriver;
    var runType = getDesiredCapabilities().getRunType(); // already null checked
    var screenResolution = getDesiredCapabilities().getScreenResolution(); //already null checked
    var remoteUrl = Optional.ofNullable(getDesiredCapabilities().getRemoteUrl());

    log.debug(String.format("Starting driver for test: %s", testName));
    switch (runType) {
      case UNIT:
        log.debug("Configuring local browser for Scaffold unit testing.");
        webDriver = new MockWebDriver();
        break;
      case LOCAL:
      case HEADLESS:
        // If the remote url isn't null, we should be configuring a remote browser. Even though the
        // run type is still local centric, it's possible to use remote url with "local" or
        // "headless" runs through docker.
        if (remoteUrl.isPresent()) {
          log.debug("Configuring remote browser for Headless.");
          webDriver = configureRemoteBrowser(browserOptions, testName);
          // Otherwise, if the remote url is not in the configuration, configure a default local
          // browser.
        } else {
          log.debug("Configuring local browser for Headless.");
          webDriver = configureLocalBrowser(browserOptions);
        }
        break;
      case GRID:
        log.debug("Configuring remote browser for Grid.");
        browserOptions.setCapability(SCREEN_RESOLUTION_CAPABILITY,
            screenResolution.getScreenShotResolutionAsString(SAUCELABS));
        webDriver = configureRemoteBrowser(browserOptions, testName);
        break;
      case SAUCE:
        log.debug("Configuring remote browser for Sauce.");
        webDriver = configureRemoteBrowser(browserOptions, testName);
        break;
      case SAUCE_MOBILE_EMULATOR:
        log.debug("Configuring remote browser for Sauce's Mobile Emulation");
        webDriver = configureRemoteBrowser(browserOptions, testName);
        break;
      default:
        throw new WebDriverManagerException(String
            .format("Unknown run type: %s. Please check your configuration.",
                runType.getRunType()));
    }
    log.debug(String.format("Driver started for test: %s", testName));
    return webDriver;
  }

  /**
   * Gets the desired capabilities via browser options. Browser options are browser dependent, thus
   * they have their own object. However, they all extend off of {@link MutableCapabilities}. Set up
   * the browser options for each browser specific case and return those browser options.
   * <p>
   * This is used during the {@link #configureBrowserDriver(String)} to obtain the capabilities when
   * creating the new Driver.
   *
   * @return as a browser options object like {@link ChromeOptions}. It must be an object that
   * extends off of {@link MutableCapabilities}
   */
  private MutableCapabilities configureDesktopBrowserOptions() {
    MutableCapabilities browserOptions;
    var runType = getDesiredCapabilities()
        .getRunType(); // runType already has null check via lombok
    var screenResolution = getDesiredCapabilities().getScreenResolution(); // Has a default set
    var browserType = getDesiredCapabilities().getBrowserType(); // already null checked

    // Boolean condition for a headless run
    boolean headless = false;
    if (runType == HEADLESS) {
      headless = true;
    }

    // If the run type isn't unit, configure the options based on the browser type
    if (runType != UNIT) {
      switch (browserType) {
        case CHROME:
          if (headless) {
            log.debug("Setting up headless browser with maximized screen.");
            System.setProperty("webdriver.chrome.silentOutput", "true");
            browserOptions = new ChromeOptions().setAcceptInsecureCerts(true).setHeadless(true)
                .addArguments("--window-size=1440x5000")
                .addArguments("--whitelisted-ips")
                .addArguments("--no-sandbox")
                .addArguments("--disable-extensions");
          } else {
            browserOptions = new ChromeOptions().setAcceptInsecureCerts(true)
                .addArguments(
                    "--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
          }
          break;
        case SAFARI:
          browserOptions = new SafariOptions();
          break;
        case FIREFOX:
          browserOptions = new FirefoxOptions().setAcceptInsecureCerts(true)
              .addArguments(
                  "--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
          break;
        case INTERNET_EXPLORER:
          browserOptions = new InternetExplorerOptions();
          break;
        case EDGE:
          browserOptions = new EdgeOptions();
          break;
        case OPERA:
          browserOptions = new OperaOptions()
              .addArguments(
                  "--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
          break;
        default:
          throw new WebDriverManagerException(
              String.format("Unknown browser type %s", browserType.toString()));
      }

      // If the runType is set to GRID, we should set a "uuid" capability for tracking
      if (runType == GRID) {
        var uuid = TestContext.baseContext()
            .getSetting(String.class, TestContextSetting.TEST_RUN_ID);
        browserOptions.setCapability("uuid", uuid);
      }

      // Otherwise if the run type is set to UNIT, create a default mutable capabilities object
      // for the mock driver
    } else {
      browserOptions = new MutableCapabilities();
    }
    return browserOptions;
  }

  /**
   * Sets up {@link DesiredCapabilities} for a mobile emulator based on the values passed in by
   * {@link DesiredCapabilitiesConfigurationProperties.MobileEmulator}. The configuration set up
   * here pertains to a sauce execution, due to the hard coded sauce device name. However, in the
   * future we can generalize this by creating a switch to set the device name based on the type of
   * device name required.
   * <p>
   * For example, if sauceDeviceName.isPresent() && amazonDeviceName.isEmpty() -> setSauceDeviceName
   * if amazonDeviceName.isPresent() && sauceDeviceName.isEmpty() -> setAmazonDeviceName etc.
   * <p>
   * Mobile emulation through sauce uses the older {@link DesiredCapabilities} but it does extend
   * off of {@link MutableCapabilities} so existing code will work here. Mobile testing options
   * through sauce can be found at this link: https://wiki.saucelabs.com/display/DOCS/Test+Configuration+Options#TestConfigurationOptions-MobileTestingOptions
   *
   * @return as {@link DesiredCapabilities}
   */
  private MutableCapabilities configureMobileEmulatorCapabilities() {
    var caps = new MutableCapabilities();

    // Setting required capabilities
    var deviceName = getDesiredCapabilities().getMobile()
        .getSauceDeviceName(); // already null checked
    var browserName = getDesiredCapabilities().getMobile().getBrowserName(); // already null checked
    var platformName = getDesiredCapabilities().getMobile()
        .getPlatformName(); // already null checked

    caps.setCapability("platformName", platformName.getPlatform());
    switch (browserName) {
      case ANDROID:
        // If you're running a test on an Android emulator, you'll need to specify "Browser"
        // (the Android stock browser for older Android versions) and "Chrome"
        // (for newer Android versions).
        caps.setCapability("browserName", "Browser");
        break;
      case CHROME:
      case SAFARI:
        caps.setCapability("browserName", browserName.getBrowserName());
        break;
      default:
        throw new WebDriverManagerException(
            String.format("Unknown browser name %s. Please check your configuration and try again.",
                browserName.getBrowserName()));
    }
    caps.setCapability("appium:deviceName", deviceName.getDeviceName());

    // Setting optional capabilities
    // blank version sets to default
    var platformVersion = Optional
        .ofNullable(getDesiredCapabilities().getMobile().getPlatformVersion()).orElse("");
    var deviceType = Optional.ofNullable(getDesiredCapabilities().getMobile().getDeviceType());
    var deviceOrientation = Optional
        .ofNullable(getDesiredCapabilities().getMobile().getDeviceOrientation());
    caps.setCapability("appium:platformVersion", platformVersion);
    deviceOrientation.ifPresent(
        orientation -> caps.setCapability("deviceOrientation", orientation.getDeviceOrientation()));
    deviceType.ifPresent(type -> caps.setCapability("deviceType", type.getDeviceType()));

    return caps;
  }

  /**
   * This helper method configures a {@link RemoteWebDriver}. A {@link RemoteWebDriver} is used for
   * testing against Selenium Grid or SauceLabs.
   * <p>
   * Serves the purpose of configuring a remote browser for both desktop and mobile emulation. Since
   * mobile emulation uses {@link RunType#SAUCE_MOBILE_EMULATOR}, we don't need to worry about any
   * desktop specific settings being applied to the execution.
   *
   * @param browserOptions the browser configuration to be used with the new {@link
   *                       RemoteWebDriver}
   * @return the new {@link RemoteWebDriver} as a {@link WebDriver}
   */
  private WebDriver configureRemoteBrowser(MutableCapabilities browserOptions,
      String testName) {
    RemoteWebDriver remoteWebDriver;
    var runType = getDesiredCapabilities().getRunType(); // already null checked through lombok
    // These values are already null checked previously. However, using optionals for their lambdas
    // cut down on code and it looks prettier. So let's do that!
    var desktopRunPlatform = Optional.ofNullable(getDesiredCapabilities().getRunPlatform());
    var desktopBrowserType = Optional.ofNullable(getDesiredCapabilities().getBrowserType());
    var desktopBrowserVersion = Optional.ofNullable(getDesiredCapabilities().getBrowserVersion());

    // Sauce capability keys are different and must be set accordingly/.
    // Only set if not sauce mobile emulator
    if (runType != SAUCE_MOBILE_EMULATOR) {
      if (runType == SAUCE) {
        desktopBrowserVersion
            .ifPresent(version -> browserOptions.setCapability("browserVersion", version));
        desktopBrowserType.ifPresent(
            browser -> browserOptions.setCapability("browserName", browser.getBrowserName()));
        desktopRunPlatform.ifPresent(
            platform -> browserOptions.setCapability("platformName", platform.getPlatform()));
      } else {
        desktopBrowserVersion
            .ifPresent(version -> browserOptions.setCapability("version", version));
        desktopRunPlatform.ifPresent(
            platform -> browserOptions.setCapability("platform", platform.getPlatform()));
      }
    }
    // Create the remote web driver and set the session id, adding it to the test context
    remoteWebDriver = createRemoteWebDriver(browserOptions, testName);
    String sessionId = remoteWebDriver.getSessionId().toString();
    TestContext.baseContext().addSetting("SESSION_ID", sessionId);

    // Check if the run type is GRID. If it is, send the test info through to grid
    checkIfGridAndSendGridRequest(remoteWebDriver);
    return remoteWebDriver;
  }

  /**
   * This helper method configures a {@link WebDriver} for local use. We are now using the updated
   * methods for creating the new {@link WebDriver}. In this case:
   * <p>
   * {@link ChromeDriver} {@link SafariDriver} {@link FirefoxDriver} {@link InternetExplorerDriver}
   * {@link OperaDriver}
   * <p>
   * I've noticed that the drivers require properties to be set to indicate where a particular web
   * driver exists on the machine. These properties are set as a system property like:
   * webdriver.chrome.driver=path/to/file.
   * <p>
   *
   * @param browserOptions the browser configuration to be used with the new {@link
   *                       RemoteWebDriver}
   * @return the new {@link WebDriver}
   */
  private WebDriver configureLocalBrowser(MutableCapabilities browserOptions) {
    WebDriver localWebDriver;
    var browserType = getDesiredCapabilities().getBrowserType();

    log.debug("Tests will be executed locally.");
    switch (browserType) {
      case CHROME:
        log.debug("Chrome chosen as browser type.");
        localWebDriver = new ChromeDriver((ChromeOptions) browserOptions);
        break;
      case SAFARI:
        log.debug("Safari chosen as browser type.");
        localWebDriver = new SafariDriver((SafariOptions) browserOptions);
        break;
      case FIREFOX:
        log.debug("Firefox chosen as browser type.");
        localWebDriver = new FirefoxDriver((FirefoxOptions) browserOptions);
        break;
      case INTERNET_EXPLORER:
        log.debug("Internet Explorer chosen as browser type.");
        localWebDriver = new InternetExplorerDriver((InternetExplorerOptions) browserOptions);
        break;
      case EDGE:
        log.debug("Edge chosen as browser type.");
        localWebDriver = new EdgeDriver((EdgeOptions) browserOptions);
        break;
      case OPERA:
        log.debug("Opera chosen as browser type.");
        localWebDriver = new OperaDriver((OperaOptions) browserOptions);
        break;
      default:
        throw new WebDriverContextException(
            "No browser or invalid browser type called for: " + browserType.toString());
    }
    return localWebDriver;
  }

  /**
   * Helper method for {@link #configureRemoteBrowser(MutableCapabilities, String)}.
   * <p>
   * Checks if the run type from the desiredCapabilities bean is GRID. If it is, it'll pull the
   * session id and send a new grid request using the {@link RestTemplate} set up from {@link
   * SeleniumGridServiceConfiguration}.
   * <p>
   *
   * @param remoteWebDriver the {@link RemoteWebDriver} that was setup from the configuration
   *                        method.
   */
  private void checkIfGridAndSendGridRequest(RemoteWebDriver remoteWebDriver) {
    var sessionId = remoteWebDriver.getSessionId().toString();
    var runType = getDesiredCapabilities().getRunType(); // already null checked

    if (runType == GRID) {
      try {
        // Pull the session id and add it to the Grid request
        var gridSessionRequest = new GridSessionRequest();
        gridSessionRequest.setSession(sessionId);
        var request = new HttpEntity<>(gridSessionRequest);

        var fullPath = GRID_TEST_SESSION_URI + "?session=" + gridSessionRequest.getSession();
        seleniumGridRestTemplate.getForObject(fullPath, GridSessionResponse.class, request);
      } catch (Exception ex) {
        log.error("Unable to call the Selenium Grid", ex);
      }
    }
  }

  /**
   * Helper method for {@link #configureRemoteBrowser(MutableCapabilities, String)}.
   * <p>
   * Checks the run type from the desiredCapabilities bean and creates a new {@link
   * RemoteWebDriver}.
   *
   * @param browserOptions the desired capabilites for the browser
   * @param testName       the information on the test that is being ran. This plugs in with Junit
   *                       Jupiter annotations.
   * @return the new {@link RemoteWebDriver}
   */
  private RemoteWebDriver createRemoteWebDriver(MutableCapabilities browserOptions,
      String testName) {
    RemoteWebDriver remoteWebDriver;
    var runType = getDesiredCapabilities().getRunType(); // already null checked
    var remoteUrl = Optional.ofNullable(getDesiredCapabilities().getRemoteUrl());

    log.debug("Test executing against a Remote Host");
    switch (runType) {
      case GRID:
        remoteWebDriver = configureGenericRemoteBrowser(browserOptions);
        break;
      case SAUCE:
      case SAUCE_MOBILE_EMULATOR:
        remoteWebDriver = configureSauceRemoteBrowser(browserOptions, testName);
        break;
      case LOCAL:
      case HEADLESS:
        if (remoteUrl.isPresent()) {
          remoteWebDriver = configureGenericRemoteBrowser(browserOptions);
        } else {
          throw new WebDriverException(
              "Unable to start a Remote Web Driver for a run type of HEADLESS or LOCAL" +
                  "with a null remoteUrl. Please check your configuration and try again.");
        }
        break;
      default:
        throw new WebDriverException(
            String.format("Unable to start a Remote Web Driver for runType: %s. " +
                "Please check your configuration and try again.", runType.getRunType()));
    }
    return remoteWebDriver;
  }

  /**
   * Helper method for {@link #createRemoteWebDriver(MutableCapabilities, String)}.
   * <p>
   * If the {@link RunType} is GRID, this sets up the remote driver session for Selenium Grid.
   * <p>
   * If any issue is discovered during the starting of this browser, we will throw a {@link
   * WebDriverException} with a custom message.
   *
   * @param browserOptions the desired capabilities we're adding on to
   * @return the driver as {@link RemoteWebDriver}
   */
  private RemoteWebDriver configureGenericRemoteBrowser(MutableCapabilities browserOptions) {
    var runType = getDesiredCapabilities().getRunType(); // already null checked
    var remoteUrl = getDesiredCapabilities().getRemoteUrl(); // already null checked

    try {
      return startScreenshotRemoteDriver(remoteUrl, browserOptions);
    } catch (Exception e) {
      throw new WebDriverContextException(
          "Error initializing remote session against " + runType.getRunType() +
              ". Check to ensure your remote url is configured correctly prior to running your tests",
          e);
    }
  }

  /**
   * Helper method for {@link #createRemoteWebDriver(MutableCapabilities, String)}
   * <p>
   * Sets up the desired capabilities for Sauce and returns the fully configured remote url.
   * <p>
   * If the {@link RunType} is SAUCE, set the user credentials and add the sauce connect tunnel id
   * to capabilities. Also the remote URL is a combination of the username, access key, and sauce
   * url. Additional information on this can be found here: https://wiki.saucelabs.com/display/DOCS/Getting+Started+with+Selenium+for+Automated+Website+Testing
   * <p>
   * If any issue is discovered during the starting of this browser, we will throw a {@link
   * WebDriverException} with a custom message.
   * <p>
   * TODO We also need to update report test pass/fail to sauce so it shows up as pass/fail on the
   * sauce UI
   *
   * @param browserOptions the desired capabilities we're adding on to
   * @param testName       the information on the test that is being ran. This plugs in with Junit
   *                       Jupiter annotations.
   * @return the driver as {@link RemoteWebDriver}
   */
  private RemoteWebDriver configureSauceRemoteBrowser(MutableCapabilities browserOptions,
      String testName) {
    validateRequiredSauceAuth();
    var sauceCaps = new MutableCapabilities();
    var sauce = getDesiredCapabilities().getSauce();
    var screenResolution = getDesiredCapabilities().getScreenResolution(); // already null checked

    // In order to build the URi correctly, pull the username and access key from the desired
    // capabilities bean.
    final var defaultSauceUrl = "@ondemand.saucelabs.com/wd/hub";
    var username = sauce.getUserName(); // already null checked
    var accessKey = sauce.getAccessKey(); // already null checked
    var tunnelIdentifier = Optional.ofNullable(sauce.getTunnelIdentifier());
    var parentTunnel = Optional.ofNullable(sauce.getParentTunnel());
    var timeZone = Optional.ofNullable(sauce.getTimeZone());
    var sauceConfigUrl = Optional.ofNullable(sauce.getUrl())
        .orElse(URI.create("https://" + username + ":" + accessKey + defaultSauceUrl)
            .toString());

    try {
      // Required
      sauceCaps.setCapability("username", username);
      sauceCaps.setCapability("accessKey", accessKey);
      sauceCaps.setCapability("name", testName);

      // Since we've added mobile emulation, we need to make sure screen resolution is not set
      // for a mobile emulation sauce configuration. Otherwise, set the appium version capability
      if (getDesiredCapabilities().getRunType() == SAUCE) {
        sauceCaps.setCapability("screenResolution",
            screenResolution.getScreenShotResolutionAsString(SAUCELABS));
      } else {
        // Default appium version left as blank string
        var appiumVersion = Optional
            .ofNullable(getDesiredCapabilities().getMobile().getAppiumVersion()).orElse("");
        sauceCaps.setCapability("appiumVersion", appiumVersion);
      }

      // Optionals with no default
      tunnelIdentifier.ifPresent(tunnelId -> sauceCaps.setCapability("tunnelIdentifier", tunnelId));
      parentTunnel
          .ifPresent(parentTunnelId -> sauceCaps.setCapability("parentTunnel", parentTunnelId));
      timeZone.ifPresent(tz -> sauceCaps.setCapability("timeZone", tz));

      browserOptions.setCapability("sauce:options", sauceCaps);
      return startScreenshotRemoteDriver(sauceConfigUrl, browserOptions);
    } catch (Exception e) {
      throw new WebDriverManagerException(
          "Unable to start new remote session against SauceLabs. Check the caused by in the stacktrace below.",
          e);
    }
  }

  private void validateRequiredSauceAuth() {
    var username = Optional.ofNullable(getDesiredCapabilities().getSauce().getUserName());
    var accessKey = Optional.ofNullable(getDesiredCapabilities().getSauce().getAccessKey());

    if (username.isEmpty()) {
      throw new WebDriverManagerException(
          "Username must be defined when initiating a Sauce based browser configuration. "
              + "Please check your configuration and try again.");
    }
    if (accessKey.isEmpty()) {
      throw new WebDriverManagerException(
          "Access Key must be defined when initiating a Sauce based browser configuration. "
              + "Please check your configuration and try again.");
    }
  }

  /**
   * Starts a new {@link ScreenshotRemoteDriver} for the remote session.
   * <p>
   * This method will throw a {@link MalformedURLException}. Only two methods should be using this
   * helper method: {@link #configureSauceRemoteBrowser(MutableCapabilities, String)} and {@link
   * #configureGenericRemoteBrowser(MutableCapabilities)}. Those methods should be responsible for
   * throwing their own custom error message since they both have varying reasons that could cause a
   * failure during the initialization of a new remote driver.
   *
   * @param remoteUrl      the remote URL to be used
   * @param browserOptions the mutable capabilities of the browser
   * @return the driver as a {@link RemoteWebDriver}
   */
  private synchronized RemoteWebDriver startScreenshotRemoteDriver(String remoteUrl,
      MutableCapabilities browserOptions)
      throws MalformedURLException {

    synchronized (startLock) {
      return new ScreenshotRemoteDriver(new URL(remoteUrl), browserOptions);
    }
  }
}
