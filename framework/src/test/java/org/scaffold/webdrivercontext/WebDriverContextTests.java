package io.github.kgress.scaffold.webdrivercontext;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.exception.WebDriverContextException;
import io.github.kgress.scaffold.util.AutomationUtils;
import io.github.kgress.scaffold.webdriver.TestContext;
import io.github.kgress.scaffold.webdriver.WebDriverManager;
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
