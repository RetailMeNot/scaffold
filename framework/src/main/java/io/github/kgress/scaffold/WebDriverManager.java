package io.github.kgress.scaffold;

import static io.github.kgress.scaffold.models.enums.desktop.RunType.SAUCE;
import static io.github.kgress.scaffold.models.enums.desktop.ScreenResolution.ScreenResolutionType.SAUCELABS;
import static io.github.kgress.scaffold.models.enums.desktop.ScreenResolution.ScreenResolutionType.SELENIUM;
import static io.github.kgress.scaffold.util.AutomationUtils.getStackTrace;
import static io.github.kgress.scaffold.util.WebDriverValidationUtil.validateAwsLambdaDesiredCapabilities;
import static io.github.kgress.scaffold.util.WebDriverValidationUtil.validateRequiredDesktopBrowserCapabilities;
import static io.github.kgress.scaffold.util.WebDriverValidationUtil.validateRequiredMobileEmulatorCapabilities;
import static io.github.kgress.scaffold.util.WebDriverValidationUtil.validateRequiredSauceAuth;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.environment.config.SeleniumGridServiceConfiguration;
import io.github.kgress.scaffold.exception.WebDriverContextException;
import io.github.kgress.scaffold.exception.WebDriverManagerException;
import io.github.kgress.scaffold.models.GridSessionRequest;
import io.github.kgress.scaffold.models.GridSessionResponse;
import io.github.kgress.scaffold.models.enums.desktop.RunType;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import lombok.AccessLevel;
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
 * user based setting. It might be prudent to allow a timeout field as a desired capability. Or even, perhaps,
 * the implicit waits timeout as set during {@link #initDriver(String)}.
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
  private static final String SCREEN_RESOLUTION_CAPABILITY = "screenResolution";

  @Getter(AccessLevel.PRIVATE)
  private final Object startLock = new Object();

  @Getter(AccessLevel.PRIVATE)
  private final Object closeLock = new Object();

  @Getter(AccessLevel.PRIVATE)
  private final DesiredCapabilitiesConfigurationProperties desiredCapabilities;

  @Getter(AccessLevel.PACKAGE)
  private WebDriverWrapper webDriverWrapper;

  @Getter(AccessLevel.PRIVATE)
  private final RestTemplate seleniumGridRestTemplate;

  @Autowired
  public WebDriverManager(DesiredCapabilitiesConfigurationProperties desiredCapabilities,
      RestTemplate seleniumGridRestTemplate) {
    this.desiredCapabilities = desiredCapabilities;
    this.seleniumGridRestTemplate = seleniumGridRestTemplate;
  }

  /**
   * TODO Consider changing the method signature to {@link WebDriver} to allow us some form of unit
   * testing of this class. We just need to make sure doing so will not allow end users to modify
   * the web driver directly after it has been instantiated.
   * <p>
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
  protected void initDriver(String testName) {
    if (getWebDriverWrapper() != null) {
      throw new WebDriverContextException(
          "Driver already exists. Try closing/quitting it before trying to initialize a new one");
    }
    var webDriver = configureWebDriver(testName);
    webDriverWrapper = new WebDriverWrapper(webDriver, getDesiredCapabilities().getWaitTimeoutInSeconds());
  }

  /**
   * Closes the current {@link WebDriver}.
   * <p>
   * 1. Check if the {@link WebDriverWrapper} is not null. If it's not null, proceed with the
   * closing of the driver. 2. Quit the driver on the thread.
   */
  void closeDriver() {
    if (getWebDriverWrapper() != null) {
      try {
        synchronized (getCloseLock()) {
          getWebDriverWrapper().quit();
        }
      } catch (Exception e) {
        log.error("Error quitting browser: " + getStackTrace(e));
      }
      webDriverWrapper = null;
    }
  }

  /**
   * Checks the run type from {@link #getDesiredCapabilities()} and configures browser options.
   * Afterwards, creates the {@link WebDriver} based on the browser options.
   *
   * @param testName the name of the test being executed
   * @return as {@link WebDriver}
   */
  private WebDriver configureWebDriver(String testName) {
    MutableCapabilities browserOptions;
    WebDriver webDriver;
    var runType = getDesiredCapabilities().getRunType(); // already null checked via lombok
    var screenResolution = getDesiredCapabilities().getScreenResolution(); // already has default

    log.debug(String.format("Starting driver for test: %s", testName));
    switch (runType) {
      case UNIT:
        log.debug("Configuring mock browser for Scaffold unit testing.");
        webDriver = null;
        break;
      case LOCAL:
        browserOptions = configureLocalBrowserOptions();
        webDriver = checkForRemoteUrl(browserOptions, runType);
        break;
      case HEADLESS:
        browserOptions = configureHeadlessChromeOptions();
        webDriver = checkForRemoteUrl(browserOptions, runType);
        break;
      case GRID:
        log.debug("Configuring remote browser for Grid.");
        browserOptions = configureGridBrowserOptions();
        browserOptions.setCapability(SCREEN_RESOLUTION_CAPABILITY,
            screenResolution.getScreenShotResolutionAsString(SAUCELABS));
        webDriver = createGridRemoteDriver(browserOptions);
        break;
      case SAUCE:
        log.debug("Configuring remote browser for Sauce.");
        browserOptions = configureSauceBrowserOptions();
        webDriver = configureSauceRemoteBrowser(browserOptions, testName);
        break;
      case SAUCE_MOBILE_EMULATOR:
        log.debug("Configuring remote browser for Sauce's Mobile Emulation");
        browserOptions = configureMobileEmulatorOptions();
        webDriver = configureSauceRemoteBrowser(browserOptions, testName);
        break;
      case AWS_LAMBDA_LOCAL:
        log.debug("Configuring local browser for AWS Lambda");
        browserOptions = configureAWSLambdaChromeOptions();
        webDriver = configureLocalDriver(browserOptions);
        break;
      case AWS_LAMBDA_REMOTE:
        log.debug("Configuring remote browser for AWS Lambda");
        browserOptions = configureAWSLambdaChromeOptions();
        webDriver = configureRemoteDriver(browserOptions);
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
   * Checks to see if a {@link RunType#LOCAL} or {@link RunType#HEADLESS} test configuration
   * includes a {@link DesiredCapabilitiesConfigurationProperties#getRemoteUrl()}. If it exists,
   * return a new {@link RemoteWebDriver}. Otherwise, return a {@link WebDriver} for a local run.
   *
   * @param browserOptions the browser options represented as {@link MutableCapabilities}
   * @param runType        the {@link RunType}. Used for logging purposes.
   * @return as {@link WebDriver}
   */
  private WebDriver checkForRemoteUrl(MutableCapabilities browserOptions, RunType runType) {
    WebDriver webDriver;
    var remoteUrl = Optional.ofNullable(getDesiredCapabilities().getRemoteUrl());
    if (remoteUrl.isPresent()) {
      log.debug(String
          .format("Configuring remote browser for a %s docker execution.", runType.getRunType()));
      webDriver = createRemoteWebDriver(browserOptions);
    } else {
      log.debug(String.format("Configuring local browser for %s execution.", runType.getRunType()));
      webDriver = configureLocalDriver(browserOptions);
    }
    return webDriver;
  }

  /**
   * Configures browser options for a {@link RunType#LOCAL} test execution.
   *
   * @return as {@link MutableCapabilities}
   */
  private MutableCapabilities configureLocalBrowserOptions() {
    validateRequiredDesktopBrowserCapabilities(getDesiredCapabilities());
    var browserOptions = configureDesktopBrowserOptions();
    browserOptions.setCapability("platform", getDesiredCapabilities().getRunPlatform());
    Optional.ofNullable(getDesiredCapabilities().getBrowserVersion())
        .ifPresent(version -> browserOptions.setCapability("version", version));
    return browserOptions;
  }

  /**
   * Configures browser options for a {@link RunType#HEADLESS} test execution.
   *
   * @return as {@link MutableCapabilities}
   */
  private MutableCapabilities configureHeadlessChromeOptions() {
    validateRequiredDesktopBrowserCapabilities(getDesiredCapabilities());
    System.setProperty("webdriver.chrome.silentOutput", "true");
    var chromeOptions = new ChromeOptions().setAcceptInsecureCerts(true).setHeadless(true)
        .addArguments("--window-size=1440x5000")
        .addArguments("--whitelisted-ips")
        .addArguments("--no-sandbox");
    chromeOptions.setCapability("platform", getDesiredCapabilities().getRunPlatform());
    Optional.ofNullable(getDesiredCapabilities().getBrowserVersion())
        .ifPresent(version -> chromeOptions.setCapability("version", version));
    return chromeOptions;
  }

  /**
   * Configures browser options for a {@link RunType#GRID} test execution.
   *
   * @return as {@link MutableCapabilities}
   */
  private MutableCapabilities configureGridBrowserOptions() {
    validateRequiredDesktopBrowserCapabilities(getDesiredCapabilities());
    var browserOptions = configureDesktopBrowserOptions();
    var uuid = TestContext.baseContext()
        .getSetting(String.class, TestContextSetting.TEST_RUN_ID);
    browserOptions.setCapability("uuid", uuid);
    return browserOptions;
  }

  /**
   * Configures browser options for a {@link RunType#SAUCE} test execution.
   *
   * @return as {@link MutableCapabilities}
   */
  private MutableCapabilities configureSauceBrowserOptions() {
    validateRequiredDesktopBrowserCapabilities(getDesiredCapabilities());
    var browserOptions = configureDesktopBrowserOptions();
    browserOptions.setCapability("browserName",
        getDesiredCapabilities().getBrowserType().getBrowserName()); // already null checked
    browserOptions.setCapability("platformName",
        getDesiredCapabilities().getRunPlatform().getPlatform()); // already null checked
    Optional.ofNullable(getDesiredCapabilities().getBrowserVersion())
        .ifPresent(version -> browserOptions.setCapability("browserVersion", version));
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
   * Mobile testing options through sauce can be found at this link:
   * https://wiki.saucelabs.com/display/DOCS/Test+Configuration+Options#TestConfigurationOptions-MobileTestingOptions
   *
   * @return as {@link DesiredCapabilities}
   */
  private MutableCapabilities configureMobileEmulatorOptions() {
    validateRequiredMobileEmulatorCapabilities(getDesiredCapabilities());
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
   * Configures browser options for a {@link RunType#AWS_LAMBDA_REMOTE} test execution.
   *
   * @return as {@link MutableCapabilities}
   */
  private MutableCapabilities configureAWSLambdaChromeOptions() {
    validateRequiredDesktopBrowserCapabilities(getDesiredCapabilities());
    validateAwsLambdaDesiredCapabilities(getDesiredCapabilities());
    var chromeOptions = new ChromeOptions()
        .setBinary(getDesiredCapabilities().getAwsLambda().getBrowserBinaryPath())
        .setHeadless(true)
        .setAcceptInsecureCerts(true)
        .addArguments("--no-sandbox")
        .addArguments("--single-process")
        .addArguments("--disable-dev-shm-usage")
        .addArguments("--window-size=1440x5000")
        .addArguments("--disable-gpu")
        .addArguments("--disable-dev-tools")
        .addArguments("--no-zygote")
        .addArguments("--disable-extensions")
        .addArguments("--disable-application-cache");
    Optional.ofNullable(getDesiredCapabilities().getAwsLambda().getDataPath())
        .ifPresent(dataPath -> chromeOptions.addArguments("--data-path=" + dataPath));
    Optional.ofNullable(getDesiredCapabilities().getAwsLambda().getDiskCacheDir())
        .ifPresent(diskCacheDir -> chromeOptions.addArguments("--disk-cache-dir=" + diskCacheDir));
    Optional.ofNullable(getDesiredCapabilities().getAwsLambda().getHomeDir())
        .ifPresent(homeDir -> chromeOptions.addArguments("--homedir=" + homeDir));
    Optional.ofNullable(getDesiredCapabilities().getAwsLambda().getUserDataDir())
        .ifPresent(userDataDir -> chromeOptions.addArguments("--user-data-dir=" + userDataDir));
    return chromeOptions;
  }

  /**
   * Helper method that creates {@link MutableCapabilities} based on the browser type.
   *
   * @return as {@link MutableCapabilities}
   */
  private MutableCapabilities configureDesktopBrowserOptions() {
    MutableCapabilities browserOptions;
    var screenResolution = getDesiredCapabilities().getScreenResolution(); // Has a default set
    var browserType = getDesiredCapabilities().getBrowserType(); // already null checked

    switch (browserType) {
      case CHROME:
        browserOptions = new ChromeOptions().setAcceptInsecureCerts(true)
            .addArguments(
                "--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
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
    return browserOptions;
  }

  /**
   * This helper method configures a {@link RemoteWebDriver}. A {@link RemoteWebDriver} is used for
   * testing against Selenium Grid SauceLabs, AWS Lambda, or Docker.
   * <p>
   * Serves the purpose of configuring a remote driver for both desktop and mobile emulation. Since
   * mobile emulation uses {@link RunType#SAUCE_MOBILE_EMULATOR}, we don't need to worry about any
   * desktop specific settings being applied to the execution.
   *
   * @param browserOptions the browser configuration to be used with the new {@link
   *                       RemoteWebDriver}
   * @return the new {@link RemoteWebDriver} as a {@link WebDriver}
   */
  private WebDriver configureRemoteDriver(MutableCapabilities browserOptions) {
    RemoteWebDriver remoteWebDriver;
    remoteWebDriver = createRemoteWebDriver(browserOptions);
    String sessionId = remoteWebDriver.getSessionId().toString();
    TestContext.baseContext().addSetting("SESSION_ID", sessionId);
    return remoteWebDriver;
  }

  /**
   * This helper method configures a {@link WebDriver} for local use. We are now using the updated
   * W3C standardized browser drivers for creating the new {@link WebDriver}. In this case:
   * <p>
   * {@link ChromeDriver} {@link SafariDriver} {@link FirefoxDriver} {@link InternetExplorerDriver}
   * {@link OperaDriver}
   * <p>
   * A local web driver execution requires the browser driver to be installed on your machine.
   * Selenium checks for the following system property: webdriver.chrome.driver=path/to/file. By
   * default, when installing a browser driver on your machine, the path to file is typically added
   * to your systems' $PATH env variable. If it isn't, you can manually add it so you can skip
   * having to specify the path as a system property during test execution.
   * <p>
   *
   * @param browserOptions the browser configuration to be used with the new {@link
   *                       RemoteWebDriver}
   * @return the new {@link WebDriver}
   */
  private WebDriver configureLocalDriver(MutableCapabilities browserOptions) {
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
   * Pulls the session id and sends a new grid request using the {@link RestTemplate} set up from
   * {@link SeleniumGridServiceConfiguration}.
   *
   * @param browserOptions the browser options represented as {@link MutableCapabilities}
   * @return as {@link RemoteWebDriver}
   */
  private RemoteWebDriver createGridRemoteDriver(MutableCapabilities browserOptions) {
    var remoteWebDriver = createRemoteWebDriver(browserOptions);
    var sessionId = remoteWebDriver.getSessionId().toString();
    try {
      // Pull the session id and add it to the Grid request
      var gridSessionRequest = new GridSessionRequest();
      gridSessionRequest.setSession(sessionId);
      var request = new HttpEntity<>(gridSessionRequest);

      var fullPath = GRID_TEST_SESSION_URI + "?session=" + gridSessionRequest.getSession();
      getSeleniumGridRestTemplate().getForObject(fullPath, GridSessionResponse.class, request);
    } catch (Exception ex) {
      log.error("Unable to call the Selenium Grid", ex);
    }
    return remoteWebDriver;
  }

  /**
   * Creates a new {@link RemoteWebDriver} based on {@link MutableCapabilities}.
   *
   * @param browserOptions the browser options represented as {@link MutableCapabilities}
   * @return as {@link RemoteWebDriver}
   */
  private RemoteWebDriver createRemoteWebDriver(MutableCapabilities browserOptions) {
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
    validateRequiredSauceAuth(getDesiredCapabilities());
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
    var extendedDebugging = Optional.ofNullable(sauce.getExtendedDebugging());
    var capturePerformance = Optional.ofNullable(sauce.getCapturePerformance());

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
      extendedDebugging.ifPresent(setting -> sauceCaps.setCapability("extendedDebugging", setting));
      capturePerformance.ifPresent(setting -> sauceCaps.setCapability("capturePerformance", setting));

      browserOptions.setCapability("sauce:options", sauceCaps);
      return startScreenshotRemoteDriver(sauceConfigUrl, browserOptions);
    } catch (Exception e) {
      throw new WebDriverManagerException(
          "Unable to start new remote session against SauceLabs. Check the caused by in the stacktrace below.",
          e);
    }
  }

  /**
   * Starts a new {@link ScreenshotRemoteDriver} for the remote session.
   * <p>
   * This method will throw a {@link MalformedURLException}. Only two methods should be using this
   * helper method: {@link #configureSauceRemoteBrowser(MutableCapabilities, String)} and {@link
   * #createRemoteWebDriver(MutableCapabilities)}. Those methods should be responsible for throwing
   * their own custom error message since they both have varying reasons that could cause a failure
   * during the initialization of a new remote driver.
   *
   * @param remoteUrl      the remote URL to be used
   * @param browserOptions the mutable capabilities of the browser
   * @return the driver as a {@link RemoteWebDriver}
   */
  private synchronized RemoteWebDriver startScreenshotRemoteDriver(String remoteUrl,
      MutableCapabilities browserOptions)
      throws MalformedURLException {

    synchronized (getStartLock()) {
      return new ScreenshotRemoteDriver(new URL(remoteUrl), browserOptions);
    }
  }
}
