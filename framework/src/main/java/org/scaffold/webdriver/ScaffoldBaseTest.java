package org.scaffold.webdriver;

import org.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import org.scaffold.util.AutomationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The base test file to be used for all projects. This class includes the setup and tear down of the web driver.
 * <p>
 * For your project, create a BaseTest file extending off of this. E.G., "CharonBaseTest".
 * <p>
 * The project specific BaseTest file should include the following annotations at the class level:
 *
 * {@literal @Execution(ExecutionMode.CONCURRENT)}
 * {@literal @ExtendWith(SpringExtension.class)}
 * {@literal @SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.NONE,}
 * {@literal classes = { YourProjectConfiguration.class, ScaffoldConfig.class })}
 * <p>
 * Execution is required for parallel testing.
 * ExtendWith is required for running the testing with Junit5
 * SpringBootTest is required for initializing the Application Context
 */
@Slf4j
@Component
public class ScaffoldBaseTest {

    @Autowired
    private DesiredCapabilitiesConfigurationProperties desiredCapabilities;

    @Autowired
    private RestTemplate seleniumGridRestTemplate;

    /**
     * Starts a {@link WebDriver} instance by checking for the desiredCapabilities bean, configuring a new {@link WebDriverContext}
     * for the thread, and launching a new browser view.
     * <p>
     * This start up will occur before every test method.
     *
     * @param testInfo the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    @BeforeEach
    public void setup(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        baseSetup(testName);
        setupWebdriver(testName);
        startWebBrowser(testName);
    }

    /**
     * Tears down a {@link WebDriver} instance by closing the driver and then removing the webdrivercontext
     * from the thread. Removing the webdrivercontext is important to ensure that there is no bleed over of a {@link WebDriver}
     * from one thread to another.
     * <p>
     * This tear down will occur after every test method.
     *
     * @param testInfo the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        var testName = testInfo.getDisplayName();
        log.debug(String.format("WebDriver teardown executing for test: %s", testName));
        try {
            getWebDriverContext().getWebDriverManager().closeDriver();
            getTestContext().removeContext();
        } catch (Exception e) {
            throw new RuntimeException("Could not stop the Web Driver", e);
        }
    }

    /**
     * This is intended to be a protected method for obtaining the web driver facade on the current thread. Currently,
     * the intended use case for this is for navigating to pages and for getting the current url. Only the BaseTest file
     * that is extending off of this file has access to this.
     * <p>
     *
     * @return the {@link WebDriverWrapper}
     */
    protected WebDriverWrapper getWebDriverWrapper() {
        return getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
    }

    /**
     * Grabs the webdrivercontext for the current thread. This allows us to get the {@link WebDriverWrapper} for that specific thread.
     *
     * @return the {@link TestContext}
     */
    private TestContext getTestContext() {
        return TestContext.baseContext();
    }

    private WebDriverContext getWebDriverContext() {
        return TestContext.baseContext().getWebDriverContext();
    }

    /**
     * Helper method for {@link #setup(TestInfo)}.
     * <p>
     * This checks to ensure that the desiredCapabilities bean is not null. If so, return an error.
     *
     * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    private void baseSetup(String testName) {
        try {
            if (desiredCapabilities == null) {
                var msg = String.format("Spring webdrivercontext has not been initialized for test [%s]. Please ensure desiredCapabilities bean is configured.", testName);
                log.error(msg);
                var exception = new RuntimeException(msg);
                // Associate the exception with the test so we can report on it later
                getTestContext().addExceptionForTest(testName, exception);
                throw exception;
            }
        } catch (Throwable t) {
            // The exception was associated with this test in the underlying try/catch block so we don't need to re-associate it here
            log.error(String.format("Error with baseSetup for Test [%s]: %s", testName, AutomationUtils.getStackTrace(t)));
            throw new RuntimeException(t);
        }
    }

    /**
     * Helper method for {@link #setup(TestInfo)}.
     * <p>
     * This will create a new {@link WebDriverContext} by creating a new instance of {@link org.scaffold.webdriver.WebDriverManager} and creating
     * a singleton with the webdrivercontext and the test name. This will make it easier to refer to this webdrivercontext at a later time.
     *
     * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    private void setupWebdriver(String testName) {
        try {
            log.debug(String.format("WebDriver setup executing for test %s", testName));
            var webDriverManager = new WebDriverManager(desiredCapabilities, seleniumGridRestTemplate);
            getTestContext().setContext(webDriverManager, testName);
        } catch (Throwable e) {
            // If a test fails to start, we want to make sure and remove the driver from this thread
            getTestContext().removeContext();

            // Associate the exception with the test so we can report on it later
            getTestContext().addExceptionForTest(testName, e);
            log.error(String.format("Error with webDriverSetup for Test [%s]: %s", testName, AutomationUtils.getStackTrace(e)));
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method for {@link #setup(TestInfo)}.
     * <p>
     * This will launch the new browser using the configured {@link WebDriverContext} that was setup during {@link #setupWebdriver(String)}.
     *
     * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    private void startWebBrowser(String testName) {
        try {
            log.debug(String.format("Starting browser for test: %s", testName));
            getWebDriverContext().getWebDriverManager().initDriver(testName);
        } catch (Throwable e) {
            // If a test fails to start, we want to make sure and remove the driver from this thread
            getTestContext().removeContext();

            // Associate the exception with the test so we can report on it later
            getTestContext().addExceptionForTest(testName, e);
            log.error(String.format("Error with startBrowser for Test [%s]: %s", testName, AutomationUtils.getStackTrace(e)));
            throw new RuntimeException(e);
        }
    }
}
