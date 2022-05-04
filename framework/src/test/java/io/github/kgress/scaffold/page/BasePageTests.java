package io.github.kgress.scaffold.page;

import io.github.kgress.scaffold.AutomationWait;
import io.github.kgress.scaffold.BasePage;
import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.BaseWebElement;
import io.github.kgress.scaffold.webelements.DivWebElement;
import io.github.kgress.scaffold.webelements.InputWebElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BasePageTests extends BaseUnitTest {

    private final static String EXPECTED_FAILED_TEXT = "Page verification failed";
    private TestPage testBasePage;

    @Mock
    private AutomationWait mockAutomationWait;

    @Mock
    private DivWebElement mockDivWebElement;

    @Mock
    private InputWebElement mockInputWebElement;

    @BeforeEach
    public void setup() {
        testBasePage = new TestPage();
    }

    @Test
    public void verifyIsOnPage_noVarArgsTest() {
        var exception = assertThrows(
                RuntimeException.class, testBasePage::verifyIsOnPage_callProtectedMethod);
        assertTrue(exception.getMessage().contains("No elements to search for when verifying"));
    }

    @Test
    public void verifyIsOnPage_elementDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        when(mockDivWebElement.isDisplayed()).thenReturn(true);

        var isOnPage = testBasePage.verifyIsOnPage_callProtectedMethod(mockDivWebElement);
        assertTrue(isOnPage);
    }

    @Test
    public void verifyIsOnPage_elementsDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        when(mockDivWebElement.isDisplayed()).thenReturn(true);
        when(mockInputWebElement.isDisplayed()).thenReturn(true);

        var isOnPage = testBasePage
                .verifyIsOnPage_callProtectedMethod(mockDivWebElement, mockInputWebElement);
        assertTrue(isOnPage);
    }

    @Test
    public void verifyIsOnPage_elementNotDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        when(mockDivWebElement.isDisplayed()).thenReturn(false);
        var exception = assertThrows(TimeoutException.class, () ->
                testBasePage.verifyIsOnPage_callProtectedMethod(mockDivWebElement));
        assertTrue(exception.getMessage().contains(EXPECTED_FAILED_TEXT));
    }

    @Test
    public void verifyIsOnPage_elementsNotDisplayed() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(true);
        when(mockDivWebElement.isDisplayed()).thenReturn(true);
        when(mockInputWebElement.isDisplayed()).thenReturn(false);
        var exception = assertThrows(TimeoutException.class, () ->
                testBasePage.verifyIsOnPage_callProtectedMethod(mockDivWebElement, mockInputWebElement));
        assertTrue(exception.getMessage().contains(EXPECTED_FAILED_TEXT));
    }

    @Test
    public void verifyIsOnPage_pageNotLoaded() {
        when(mockAutomationWait.waitUntilPageIsLoaded()).thenReturn(false);
        var exception = assertThrows(TimeoutException.class, () ->
                testBasePage.verifyIsOnPage_callProtectedMethod(mockDivWebElement));
        assertTrue(exception.getMessage().contains("The intended page failed to load"));
    }

    @Test
    public void waitUntilPageIsLoaded_targetFrameDetachedThrowsAfterFiveTries() {
        var spyAutomationWait = spy(new AutomationWait(mockWebDriverWrapper, 1L));
        doThrow(new WebDriverException("target frame detached"))
                .when(spyAutomationWait)
                .waitForCustomCondition(any(), any());
        assertThrows(WebDriverException.class, spyAutomationWait::waitUntilPageIsLoaded);
    }

    @Test
    public void waitUntilPageIsLoaded_targetFrameDetachedDoesNotThrowUnderFiveTries() {
        var spyAutomationWait = spy(new AutomationWait(mockWebDriverWrapper, 1L));
        doThrow(new WebDriverException("target frame detached"))
                .doThrow(new WebDriverException("target frame detached"))
                .doThrow(new WebDriverException("target frame detached"))
                .doThrow(new WebDriverException("target frame detached"))
                .doReturn(true)
                .when(spyAutomationWait)
                .waitForCustomCondition(any(), any());
        assertTrue(spyAutomationWait.waitUntilPageIsLoaded());
    }

    /**
     * This nested class is only intended for unit testing purposes. It should never be used for production code. We
     * required a mocked automation wait in order to properly set the
     * {@link AutomationWait#waitUntilPageIsLoaded(Long)} condition.
     */
    class TestPage extends BasePage {

        @Override
        public AutomationWait getAutomationWait() {
            return mockAutomationWait;
        }

        Boolean verifyIsOnPage_callProtectedMethod(BaseWebElement... element) {
            return verifyIsOnPage(element);
        }
    }
}
