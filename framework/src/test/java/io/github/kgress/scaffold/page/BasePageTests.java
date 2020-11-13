package io.github.kgress.scaffold.page;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.util.AutomationWait;
import io.github.kgress.scaffold.webdriver.BasePage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class BasePageTests extends BaseUnitTest {

    private BasePage testBasePage = new BasicBasePage();
    private AutomationWait mockAutomationWait = Mockito.mock(AutomationWait.class);

    @Test
    public void isOnPage_noVarArgsTest() {
        assertThrows(RuntimeException.class, testBasePage::verifyIsOnPage);
    }

    @Test
    public void isOnPage_elementDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        var testElement = new TestableAbstractWebElement(mockElement1);
        var isOnPage = testBasePage.verifyIsOnPage(testElement);
        assertTrue(isOnPage);
    }

    @Test
    public void isOnPage_elementsDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        var testElement = new TestableAbstractWebElement(mockElement1);
        var testElement2 = new TestableAbstractWebElement(mockElement2);
        var isOnPage = testBasePage.verifyIsOnPage(testElement, testElement2);
        assertTrue(isOnPage);
    }

    @Test
    public void isOnPage_elementNotDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        var testElement = new TestableAbstractWebElement(mockElement1);
        mockElement1.setIsDisplayed(false);
        assertThrows(NoSuchElementException.class, () -> testBasePage.verifyIsOnPage(testElement));
    }

    @Test
    public void isOnPage_elementsNotDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        var testElement = new TestableAbstractWebElement(mockElement1);
        var testElement2 = new TestableAbstractWebElement(mockElement2);
        mockElement1.setIsDisplayed(false);
        mockElement2.setIsDisplayed(false);
        assertThrows(NoSuchElementException.class, () -> testBasePage.verifyIsOnPage(testElement, testElement2));
    }
    
    /**
     * This nested class is only intended for unit testing purposes. It should never be used for production code. We
     * required a mocked automation wait in order to properly set the {@link AutomationWait#waitUntilPageIsLoaded()}
     * condition.
     */
    private class BasicBasePage extends BasePage {

        @Override
        public boolean isOnPage() {
            return false;
        }

        @Override
        public AutomationWait getAutomationWait() {
            return mockAutomationWait;
        }
    }
}
