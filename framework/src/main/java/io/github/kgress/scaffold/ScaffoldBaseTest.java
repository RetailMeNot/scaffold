package io.github.kgress.scaffold;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
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
     * Helper method for {@link #setup(TestInfo)}.
     * <p>
     * This will create a new {@link WebDriverContext} by creating a new instance of {@link WebDriverManager} and creating
     * a singleton with the webdrivercontext and the test name. This will make it easier to refer to this webdrivercontext at a later time.
     *
     * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    private void setupWebdriver(String testName) {
        log.debug(String.format("WebDriver setup executing for test %s", testName));
        var webDriverManager = new WebDriverManager(desiredCapabilities, seleniumGridRestTemplate);
        getTestContext().setContext(webDriverManager, testName);
    }

    /**
     * Helper method for {@link #setup(TestInfo)}.
     * <p>
     * This will launch the new browser using the configured {@link WebDriverContext} that was setup during {@link #setupWebdriver(String)}.
     *
     * @param testName the information on the test that is being ran. This plugs in with Junit Jupiter annotations.
     */
    private void startWebBrowser(String testName) {
        log.debug(String.format("Starting browser for test: %s", testName));
        getWebDriverContext().getWebDriverManager().initDriver(testName);
    }

    /**
     * Grabs the webdrivercontext for the current thread. This allows us to get the {@link WebDriverWrapper} for that specific thread.
     *
     * @return the {@link TestContext}
     */
    private TestContext getTestContext() {
        return TestContext.baseContext();
    }

    /**
     * Gets the {@link WebDriverContext} for the current thread.
     *
     * @return the {@link WebDriverContext}
     */
    private WebDriverContext getWebDriverContext() {
        return getTestContext().getWebDriverContext();
    }
}
