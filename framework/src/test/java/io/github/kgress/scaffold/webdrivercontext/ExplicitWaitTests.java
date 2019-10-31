package io.github.kgress.scaffold.webdrivercontext;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.webdriver.TestContext;
import org.junit.Before;
import org.junit.Test;

import static io.github.kgress.scaffold.webdriver.interfaces.TestContextSetting.WAIT_FOR_DISPLAY_ENABLED;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExplicitWaitTests extends BaseUnitTest {

    @Before
    public void enableExplicitWaits() {
        TestContext.baseContext().addSetting(WAIT_FOR_DISPLAY_ENABLED, true);
    }

    @Test
    public void testEnableExplicitWaits() {
        var explicitWaitEnabled = TestContext.baseContext().getSetting(Boolean.class, WAIT_FOR_DISPLAY_ENABLED);
        assertTrue(explicitWaitEnabled);
    }
}
