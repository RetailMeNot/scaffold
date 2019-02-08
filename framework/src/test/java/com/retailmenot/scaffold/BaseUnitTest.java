package com.retailmenot.scaffold;

import com.retailmenot.scaffold.environment.config.ScaffoldConfiguration;
import com.retailmenot.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import com.retailmenot.scaffold.models.unittests.MockWebDriver;
import com.retailmenot.scaffold.models.unittests.MockWebElement;
import com.retailmenot.scaffold.webdriver.TestContext;
import com.retailmenot.scaffold.webdriver.WebDriverManager;
import com.retailmenot.scaffold.webdriver.WebDriverWrapper;
import com.retailmenot.scaffold.webelements.AbstractWebElement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.retailmenot.scaffold.util.AutomationUtils.getUniqueString;

@Slf4j
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { ScaffoldConfiguration.class }
)
public abstract class BaseUnitTest {

    protected static final String TAG_NAME_1 = "test element 1";
    protected static final String TAG_NAME_2 = "test element 2";
    protected static final String TEXT_NAME_1 = "element 1";
    protected static final String TEXT_NAME_2 = "element 2";
    private static String MOCK_UNIT_TEST;

    protected WebDriverManager webDriverContextImpl;
    protected WebDriverWrapper webDriverWrapper;
    protected MockWebDriver mockWebDriver;
    protected TestableAbstractWebElement testAbstractWebElement;
    protected MockWebElement mockElement1;
    protected MockWebElement mockElement2;

    @Autowired
    protected RestTemplate seleniumGridRestTemplate;

    @Autowired
    protected DesiredCapabilitiesConfigurationProperties desiredCapabilities;

    /**
     * Setup the mock web driver and test data before every method
     */
    @BeforeEach
    protected void setupTestWebDriver() {
        // Create a new unique ID for the test name
        MOCK_UNIT_TEST = "Mock Unit Test " + getUniqueString();

        // Create a new WebDriverContext and create a TestContext singleton with the WebDriverContext and test name
        webDriverContextImpl = new WebDriverManager(desiredCapabilities, seleniumGridRestTemplate);
        TestContext.baseContext().setContext(webDriverContextImpl, MOCK_UNIT_TEST);

        // Hit initDriver to create the mock driver and auto assign the webDriverWrapper to the WebDriverContext
        // with reflection magic
        webDriverContextImpl.initDriver(MOCK_UNIT_TEST);

        // Set the webdriverwrapper to the one created and set to the WebDriverContext.
        webDriverWrapper = webDriverContextImpl.getWebDriverWrapper();

        // Set the mockWebDriver to the one created and set to the webdriverwrapper from the WebDriverContext.
        mockWebDriver = (MockWebDriver) webDriverWrapper.getBaseWebDriver();

        setupTestData();
    }

    /**
     * Get rid of the web driver context after the test
     */
    @AfterEach
    protected void tearDownTestWebDriver() {
        TestContext.baseContext().removeContext();
    }

    /**
     * A helper method for setting up shared test data
     */
    private void setupTestData() {
        testAbstractWebElement = new TestableAbstractWebElement();
        mockElement1 = new MockWebElement()
                .text(TEXT_NAME_1)
                .tagName(TAG_NAME_1);
        mockElement2 = new MockWebElement()
                .text(TEXT_NAME_2)
                .tagName(TAG_NAME_2);
    }

    /**
     * A nested class for testing against abstract elements
     */
    public class TestableAbstractWebElement extends AbstractWebElement {
        public TestableAbstractWebElement() {
            this(null, (By) null); //have to cast the second null arg to disambiguate the parentBy from the parentElement
        }

        public TestableAbstractWebElement(By by, By parentBy) {
            super(by, parentBy);
        }

        public TestableAbstractWebElement(By by, WebElement parentElement) {
            super(by, parentElement);
        }

        public TestableAbstractWebElement(By by) {
            super( by );
        }

        public TestableAbstractWebElement(WebElement element) {
            super(element);
        }

        public String getText() {
            return getWebElement().getText();
        }
    }
}
