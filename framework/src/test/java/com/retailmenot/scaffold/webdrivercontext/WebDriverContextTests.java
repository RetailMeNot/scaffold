package com.retailmenot.scaffold.webdrivercontext;

import com.retailmenot.scaffold.BaseUnitTest;
import com.retailmenot.scaffold.exception.WebDriverContextException;
import com.retailmenot.scaffold.util.AutomationUtils;
import com.retailmenot.scaffold.webdriver.TestContext;
import com.retailmenot.scaffold.webdriver.WebDriverManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WebDriverContextTests extends BaseUnitTest {

    @Test
    public void testContextGet() {
        var actualWebDriverContext = TestContext.baseContext().getWebDriverContext();
        assertEquals(webDriverContextImpl, actualWebDriverContext.getWebDriverManager());
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
        String testName = "test" + AutomationUtils.getUniqueString(5);
        TestContext.baseContext().addExceptionForTest(testName, t);
        var returnedException = TestContext.baseContext().getExceptionForTest(testName);
        assertEquals(t, returnedException);
    }
}
