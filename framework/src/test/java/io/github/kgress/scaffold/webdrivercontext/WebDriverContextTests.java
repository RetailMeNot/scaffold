package io.github.kgress.scaffold.webdrivercontext;

import io.github.kgress.scaffold.*;
import io.github.kgress.scaffold.exception.WebDriverContextException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static io.github.kgress.scaffold.util.AutomationUtils.getUniqueString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebDriverContextTests extends BaseUnitTest {

    /**
     * Setup the mock web driver and test data before every method
     */
    @BeforeEach
    protected void setupTestWebDriver() {
        // Create a new unique ID for the test name
        var mockUnitTest = "Mock Unit Test " + getUniqueString();

        // Create a new WebDriverContext and create a TestContext singleton with the WebDriverContext and test name
        TestWebDriverManager testWebDriverManager = new TestWebDriverManager(desiredCapabilities, seleniumGridRestTemplate);
        TestContext.baseContext().setContext(testWebDriverManager, mockUnitTest);

        // Hit initDriver to create the mock driver and auto assign the webDriverWrapper to the WebDriverContext
        // with reflection magic
        testWebDriverManager.initDriver_fromParent(mockUnitTest);

        // Set the webdriverwrapper to the one created and set to the WebDriverContext.
        WebDriverWrapper testWebDriverWrapper = testWebDriverManager.getWebDriverWrapper_fromParent();

        // Set the mockWebDriver to the one created and set to the webdriverwrapper from the WebDriverContext.
        WebDriver testWebDriver = mockWebDriverWrapper.getBaseWebDriver();
    }

     /**
     * Get rid of the web driver context after the test
     */
    @AfterEach
    protected void tearDownTestWebDriver() {
        TestContext.baseContext().removeContext();
    }


    @Test
    public void testSetContextWithoutRemove() {
        var newWebDriverContext = new WebDriverManager(desiredCapabilities, seleniumGridRestTemplate);
        assertThrows(WebDriverContextException.class, () ->
                TestContext.baseContext().setContext(newWebDriverContext, "New Context Without Remove"));
    }

    @Test
    public void testSetContextWithRemove() {
        //First, remove the default webdrivercontext set up by the @BeforeEach from BaseUnitTest
        TestContext.baseContext().removeContext();

        var newWebDriverManager = new WebDriverManager(desiredCapabilities, seleniumGridRestTemplate);
        TestContext.baseContext().setContext(newWebDriverManager, "New Context With Remove");

        var actualWebDriverContext = TestContext.baseContext().getWebDriverContext();
        assertEquals(newWebDriverManager, actualWebDriverContext.getWebDriverManager());
    }

    @Test
    public void testExceptionGet() {
        var t = new Throwable("Exception");
        String testName = "test" + getUniqueString(5);
        TestContext.baseContext().addExceptionForTest(testName, t);
        var returnedException = TestContext.baseContext().getExceptionForTest(testName);
        assertEquals(t, returnedException);
    }
}
