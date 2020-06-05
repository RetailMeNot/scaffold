package io.github.kgress.scaffold.webdriver;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.environment.config.SeleniumGridServiceConfiguration;
import io.github.kgress.scaffold.exception.WebDriverContextException;
import io.github.kgress.scaffold.models.GridSessionRequest;
import io.github.kgress.scaffold.models.GridSessionResponse;
import io.github.kgress.scaffold.models.enums.BrowserType;
import io.github.kgress.scaffold.models.enums.RunType;
import io.github.kgress.scaffold.models.enums.ScreenResolution;
import io.github.kgress.scaffold.models.unittests.MockWebDriver;
import io.github.kgress.scaffold.webdriver.interfaces.TestContextSetting;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import static io.github.kgress.scaffold.models.enums.RunType.*;
import static io.github.kgress.scaffold.models.enums.ScreenResolution.ScreenResolutionType.SAUCELABS;
import static io.github.kgress.scaffold.models.enums.ScreenResolution.ScreenResolutionType.SELENIUM;
import static io.github.kgress.scaffold.util.AutomationUtils.getStackTrace;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * This class manages {@link WebDriverWrapper} instances on a per thread basis. This is useful for multi-threaded tests to be
 * able to seamlessly access their associated WebDriver instance statically.
 * <p>
 * The startLock and closeLock properties:
 * We want locks to open *and* close WebDriver instances when we are using Selenium Grid as we do not want a large amount of
 * open/close requests flooding the hub at once.  This way, they will be processed serially.
 */
@Slf4j
@Service
public class WebDriverManager {

    private static final String GRID_TEST_SESSION_URI = "/grid/api/testsession";
    private static final Long TEN_SECONDS = 10L;
    private static final String SCREEN_RESOLUTION_CAPABILITY = "screenResolution"; //conforms to selenium capability standard

    private final RestTemplate seleniumGridRestTemplate;
    private final DesiredCapabilitiesConfigurationProperties desiredCapabilities;
    private WebDriverWrapper webDriverWrapper;

    private final Object startLock = new Object();
    private final Object closeLock = new Object();
    private Set<Cookie> cookieJar = new TreeSet<>();
    private RunType runType;
    private BrowserType browserType;
    private ScreenResolution screenResolution;
    private String sessionId;
    private String remoteUrl;


    @Autowired
    public WebDriverManager(DesiredCapabilitiesConfigurationProperties desiredCapabilities,
                            RestTemplate seleniumGridRestTemplate) {
        this.desiredCapabilities = desiredCapabilities;
        this.seleniumGridRestTemplate = seleniumGridRestTemplate;
        this.runType = desiredCapabilities.getRunType();
        this.browserType = desiredCapabilities.getBrowserType();
        this.screenResolution = desiredCapabilities.getScreenResolution();
        this.remoteUrl = desiredCapabilities.getRemoteUrl();
    }

    /**
     * Creates a new instance of a {@link WebDriver}, wraps it into a {@link WebDriverWrapper}, and sets an implicit timeout.
     * <p>
     * 1. Check if the {@link WebDriverWrapper} is null from the {@link WebDriverContext}. If it's null, the driver already exists and
     * we've encountered a threading issue.
     * 2. Configure the new browser driver.
     * 3. Create the new {@link WebDriverWrapper} with the browser driver.
     * 4. Setup implicit waits on the driver to make it easier to interact with elements.
     *
     * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    public void initDriver(String testName) {
        if (webDriverWrapper != null) {
            throw new WebDriverContextException("Driver already exists. Try closing/quitting it before trying to initialize a new one");
        }
        var browserDriver = configureBrowserDriver(testName);
        webDriverWrapper = new WebDriverWrapper(browserDriver);

        // Configure the browser to implicitly wait anytime a user attempts to locate an element
        webDriverWrapper.manage().timeouts().implicitlyWait(TEN_SECONDS, SECONDS);
    }

    /**
     * Closes the current {@link WebDriver}.
     * <p>
     * 1. Check if the {@link WebDriverWrapper} is not null. If it's not null, proceed with the closing of the driver.
     * 2. Quit the driver on the thread.
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
     * Gets the {@link WebDriverWrapper} on the current thread.
     *
     * @return the {@link WebDriverWrapper}
     */
    public WebDriverWrapper getWebDriverWrapper() {
        return webDriverWrapper;
    }

    public String getSessionId() {
        return sessionId;
    }

    /**
     * Set up any cookies the user passed in, so they will be active before the first url is loaded
     *
     * @param webDriver the instance of the web driver
     * @param cookieJar the cookies
     */
    private void initializeCookies(WebDriverWrapper webDriver, Set<Cookie> cookieJar) {
        if (!cookieJar.isEmpty()) {
            for (var cookie : cookieJar) {
                webDriver.addCookie(cookie);
            }
            //If any new cookies were added, refresh the page to make sure they "take".
            // Setting them before we get to the correct domain will throw a "same origin" security exception
            webDriver.navigate().refresh();
        }
    }

    /**
     * Opens the URL.
     * <p>
     * TODO We should add cookie jar init during the start of the web driver.
     *
     * @param webDriver the instance of the webdriver to be used
     * @param url       the url to be navigated to
     */
    private void openUrl(WebDriverWrapper webDriver, String url) {
        if (url != null && !url.isEmpty()) {
            webDriver.get(url);

            //Initialize any cookies that should be in place (authentication, traffic slicing, etc)
            // before the first navigation is done, then refresh the page
            initializeCookies(webDriver, cookieJar);

            log.debug(String.format("Opening new WebDriver instance at '%s'", url));
        } else {
            log.debug("No starting URL specified, so no navigation will be performed at this time");
        }
    }

    /**
     * Gets the desired capabilities via browser options. Browser options are browser dependent, thus they have their own object.
     * However, they all extend off of {@link MutableCapabilities}. Set up the browser options for each browser specific case and
     * return those browser options.
     * <p>
     * This is used during the {@link #configureBrowserDriver(String)} to obtain the capabilities when creating the new Driver.
     *
     * @return as a browser options object like {@link ChromeOptions}. It must be an object that extends off of {@link MutableCapabilities}
     */
    private MutableCapabilities configureBrowserOptions() {
        MutableCapabilities browserOptions;

        // Boolean condition for a headless run
        boolean headless = false;
        if (runType == HEADLESS) {
            headless = true;
        }

        // If the run type isn't unit, configure the options based on the browser type
        if (runType != UNIT) {
            switch (browserType) {
                case Chrome:
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
                                .addArguments("--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
                    }
                    break;
                case Safari:
                    browserOptions = new SafariOptions();
                    break;
                case Firefox:
                    browserOptions = new FirefoxOptions().setAcceptInsecureCerts(true)
                            .addArguments("--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
                    break;
                case InternetExplorer:
                    browserOptions = new InternetExplorerOptions();
                    break;
                case Edge:
                    browserOptions = new EdgeOptions();
                    break;
                case Opera:
                    browserOptions = new OperaOptions()
                            .addArguments("--window-size=" + screenResolution.getScreenShotResolutionAsString(SELENIUM));
                    break;
                default:
                    throw new WebDriverContextException("No browser or invalid browser type called for: " + browserType.toString());
            }

            // If the runType is set to GRID, we should set a "uuid" capability for tracking
            if (runType == GRID) {
                var uuid = TestContext.baseContext().getSetting(String.class, TestContextSetting.TEST_RUN_ID);
                browserOptions.setCapability("uuid", uuid);
            }

            // Otherwise if the run type is set to UNIT, create a default mutable capabilities object for the mock driver
        } else {
            browserOptions = new MutableCapabilities();
        }
        return browserOptions;
    }

    /**
     * Configures the WebDriver with the desired capabilities of Chrome, Safari, Firefox, Opera, or Internet Explorer.
     *
     * @return as {@link WebDriver}
     */
    private WebDriver configureBrowserDriver(String testName) {
        WebDriver webDriver;

        // Create the MutableCapabilities based on the type of run that's being executed
        var browserOptions = configureBrowserOptions();

        log.debug("Starting driver for test: " + testName);
        // Scaffold has a run type of unit to be used with the unit testing in this repo. If it's unit, create a mock driver
        if (runType == UNIT) {
            webDriver = new MockWebDriver();

            // If the run type is local or headless, configure a new browser intended for local use
        } else if (runType == LOCAL || runType == HEADLESS) {

            // If the remote url isn't null, we should be configuring a remote browser. Even though the run type is still
            // local centric, it's possible to use remote url with "local" or "headless" runs through docker.
            if (remoteUrl != null) {
                webDriver = configureRemoteBrowser(browserOptions, testName);

                // Otherwise, if the remote url is not in the configuration, configure a default local browser.
            } else {
                webDriver = configureLocalBrowser(browserOptions);
            }

            // If the run type is sauce of grid, configure a remote browser
        } else if (runType == GRID) {
            browserOptions.setCapability(SCREEN_RESOLUTION_CAPABILITY, screenResolution.getScreenShotResolutionAsString(SAUCELABS));
            webDriver = configureRemoteBrowser(browserOptions, testName);
            // If an unknown run type has been passed in, throw an error
        } else if (runType == SAUCE) {
            webDriver = configureRemoteBrowser(browserOptions, testName);
        } else {
            throw new WebDriverContextException(String
                    .format("Unknown run type: %s. Please check your configuration.", runType.getRunType()));
        }
        log.debug("Driver started for test: " + testName);
        return webDriver;
    }

    /**
     * This helper method configures a {@link WebDriver} for local use. We are now using the updated methods for creating
     * the new {@link WebDriver}. In this case:
     * <p>
     * {@link ChromeDriver}
     * {@link SafariDriver}
     * {@link FirefoxDriver}
     * {@link InternetExplorerDriver}
     * {@link OperaDriver}
     * <p>
     * I've noticed that the drivers require properties to be set to indicate where a particular web driver exists on
     * the machine. These properties are set as a system property like: webdriver.chrome.driver=path/to/file.
     * <p>
     *
     * @param browserOptions the browser configuration to be used with the new {@link RemoteWebDriver}
     * @return the new {@link WebDriver}
     */
    private WebDriver configureLocalBrowser(MutableCapabilities browserOptions) {
        WebDriver localWebDriver;

        log.debug("Tests will be executed locally.");
        switch (browserType) {
            case Chrome:
                log.debug("Chrome chosen as browser type.");
                localWebDriver = new ChromeDriver((ChromeOptions) browserOptions);
                break;
            case Safari:
                log.debug("Safari chosen as browser type.");
                localWebDriver = new SafariDriver((SafariOptions) browserOptions);
                break;
            case Firefox:
                log.debug("Firefox chosen as browser type.");
                localWebDriver = new FirefoxDriver((FirefoxOptions) browserOptions);
                break;
            case InternetExplorer:
                log.debug("Internet Explorer chosen as browser type.");
                localWebDriver = new InternetExplorerDriver((InternetExplorerOptions) browserOptions);
                break;
            case Edge:
                log.debug("Edge chosen as browser type.");
                localWebDriver = new EdgeDriver((EdgeOptions) browserOptions);
                break;
            case Opera:
                log.debug("Opera chosen as browser type.");
                localWebDriver = new OperaDriver((OperaOptions) browserOptions);
                break;
            default:
                throw new WebDriverContextException("No browser or invalid browser type called for: " + browserType.toString());
        }
        return localWebDriver;
    }

    /**
     * This helper method configures a {@link RemoteWebDriver}. A {@link RemoteWebDriver} is used for testing against
     * Selenium Grid or SauceLabs.
     *
     * @param browserOptions the browser configuration to be used with the new {@link RemoteWebDriver}
     * @return the new {@link RemoteWebDriver} as a {@link WebDriver}
     */
    private WebDriver configureRemoteBrowser(MutableCapabilities browserOptions, String testName) {
        var browserVersion = desiredCapabilities.getBrowserVersion();
        var runPlatform = desiredCapabilities.getRunPlatform();
		var browserType = desiredCapabilities.getBrowserType();
        RemoteWebDriver remoteWebDriver;

        if (runType == SAUCE) {
            // If the browser version isn't null, set the version capability to what the user wants
            if (browserVersion != null) {
                browserOptions.setCapability("browserVersion", browserVersion);
            }

			// If the browser type isn't null, set the type capability to what the user wants
			if (browserType != null) {
				browserOptions.setCapability("browserName", browserType);
			}
			
            // If the run platform value isn't null, set the platform to what the user wants
            if (runPlatform != null) {
                browserOptions.setCapability("platformName", runPlatform);
            }
        } else {
            // If the browser version isn't null, set the version capability to what the user wants
            if (browserVersion != null) {
                browserOptions.setCapability("version", browserVersion);
            }

            // If the run platform value isn't null, set the platform to what the user wants
            if (runPlatform != null) {
                browserOptions.setCapability("platform", runPlatform);
            }
        }
        // Create the remote web driver and set the session id, adding it to the test context
        remoteWebDriver = createRemoteWebDriver(browserOptions, testName);
        sessionId = remoteWebDriver.getSessionId().toString();
        TestContext.baseContext().addSetting("SESSION_ID", sessionId);

        // Check if the run type is GRID. If it is, send the test info through to grid
        checkIfGridAndSendGridRequest(remoteWebDriver);
        return remoteWebDriver;
    }

    /**
     * Helper method for {@link #configureRemoteBrowser(MutableCapabilities, String)}.
     * <p>
     * Checks if the run type from the desiredCapabilities bean is GRID. If it is, it'll pull the session id and send
     * a new grid request using the {@link RestTemplate} set up from {@link SeleniumGridServiceConfiguration}.
     * <p>
     *
     * @param remoteWebDriver the {@link RemoteWebDriver} that was setup from the configuration method.
     */
    private void checkIfGridAndSendGridRequest(RemoteWebDriver remoteWebDriver) {
        var sessionId = remoteWebDriver.getSessionId().toString();

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
     * Checks the run type from the desiredCapabilities bean and creates a new {@link RemoteWebDriver}.
     *
     * @param browserOptions the desired capabilites for the browser
     * @param testName       the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     * @return the new {@link RemoteWebDriver}
     */
    private RemoteWebDriver createRemoteWebDriver(MutableCapabilities browserOptions, String testName) {
        RemoteWebDriver remoteWebDriver;

        log.debug("Tests will be executed against a Remote Host");
        // If the run type is grid, create a generic remote browser
        if (runType == GRID) {
            remoteWebDriver = configureGenericRemoteBrowser(browserOptions);

            // If the run type is sauce, create a configuration that is specific to sauce lab
        } else if (runType == SAUCE) {
            remoteWebDriver = configureSauceRemoteBrowser(browserOptions, testName);

            // If the run type is local or headless, and the remote url is not null, we should create a remote browser with
            // the provided remote url. Otherwise, throw an exception with instructions on how checking configuration.
        } else if (runType == LOCAL || runType == HEADLESS) {
            if (remoteUrl != null) {
                remoteWebDriver = configureGenericRemoteBrowser(browserOptions);
            } else {
                throw new WebDriverException("Unable to start a Remote Web Driver for a run type of HEADLESS or LOCAL" +
                        "with a null remoteUrl. Please check your configuration and try again.");
            }
        }

        // If a non run type is passed in, throw an error.
        else {
            throw new WebDriverContextException(String
                    .format("Unable to start a Remote Web Driver for runType=%s. " +
                            "Please check your configuration and try again.", runType.getRunType()));
        }
        return remoteWebDriver;
    }

    /**
     * Helper method for {@link #createRemoteWebDriver(MutableCapabilities, String)}.
     * <p>
     * If the {@link RunType} is GRID, this sets up the remote driver session for Selenium Grid.
     * <p>
     * If any issue is discovered during the starting of this browser, we will throw a {@link WebDriverException} with a
     * custom message.
     *
     * @param browserOptions the desired capabilities we're adding on to
     * @return the driver as {@link RemoteWebDriver}
     */
    private RemoteWebDriver configureGenericRemoteBrowser(MutableCapabilities browserOptions) {
        try {
            return startScreenshotRemoteDriver(remoteUrl, browserOptions);
        } catch (Exception e) {
            throw new WebDriverContextException("Error initializing remote session against " + runType.getRunType() +
                    ". Check to ensure your remote url is configured correctly prior to running your tests", e);
        }
    }

    /**
     * Helper method for {@link #createRemoteWebDriver(MutableCapabilities, String)}
     * <p>
     * Sets up the desired capabilities for Sauce and returns the fully configured remote url.
     * <p>
     * If the {@link RunType} is SAUCE, set the user credentials and add the sauce connect tunnel id to capabilities. Also
     * the remote URL is a combination of the username, access key, and sauce url. Additional information on this
     * can be found here: https://wiki.saucelabs.com/display/DOCS/Getting+Started+with+Selenium+for+Automated+Website+Testing
     * <p>
     * If any issue is discovered during the starting of this browser, we will throw a {@link WebDriverException} with a
     * custom message.
     * <p>
     * TODO We also need to update report test pass/fail to sauce so it shows up as pass/fail on the sauce UI
     *
     * @param browserOptions the desired capabilities we're adding on to
     * @param testName       the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     * @return the driver as {@link RemoteWebDriver}
     */
    private RemoteWebDriver configureSauceRemoteBrowser(MutableCapabilities browserOptions, String testName) {
        var sauce = desiredCapabilities.getSauce();

        // In order to build the URi correctly, pull the username and access key from the desired capabilities bean.
        var username = sauce.getUserName();
        var accessKey = sauce.getAccessKey();
        var defaultSauceUrl = "@ondemand.saucelabs.com/wd/hub";
        var tunnelIdentifier = sauce.getTunnelIdentifier();
        var parentTunnel = sauce.getParentTunnel();
        var sauceCaps = new MutableCapabilities();
        var sauceConfigUrl = sauce.getUrl();

        try {
            if (sauce.getTunnelIdentifier() != null) {
                sauceCaps.setCapability("tunnelIdentifier", tunnelIdentifier);
            }
            if (sauce.getParentTunnel() != null) {
                sauceCaps.setCapability("parentTunnel", parentTunnel);
            }
            if (sauceConfigUrl == null) {
                sauceConfigUrl = URI.create("https://" + username + ":" + accessKey + defaultSauceUrl).toString();
            }

            sauceCaps.setCapability("username", username);
            sauceCaps.setCapability("accessKey", accessKey);
            sauceCaps.setCapability("name", testName);
            sauceCaps.setCapability("screenResolution", screenResolution.getScreenShotResolutionAsString(SAUCELABS));

            browserOptions.setCapability("sauce:options", sauceCaps);
            return startScreenshotRemoteDriver(sauceConfigUrl, browserOptions);
        } catch (Exception e) {
            throw new WebDriverContextException("Unable to start new remote session against saucelabs. Please check your " +
                    "configuration.", e);
        }
    }

    /**
     * Starts a new {@link ScreenshotRemoteDriver} for the remote session.
     * <p>
     * This method will throw a {@link MalformedURLException}. Only two methods should be using this helper method:
     * {@link #configureSauceRemoteBrowser(MutableCapabilities, String)} and
     * {@link #configureGenericRemoteBrowser(MutableCapabilities)}. Those methods should be responsible for throwing their
     * own custom error message since they both have varying reasons that could cause a failure during the initialization
     * of a new remote driver.
     *
     * @param remoteUrl      the remote URL to be used
     * @param browserOptions the mutable capabilities of the browser
     * @return the driver as a {@link RemoteWebDriver}
     */
    private synchronized RemoteWebDriver startScreenshotRemoteDriver(String remoteUrl, MutableCapabilities browserOptions)
            throws MalformedURLException {

        synchronized (startLock) {
            return new ScreenshotRemoteDriver(new URL(remoteUrl), browserOptions);
        }
    }
}
