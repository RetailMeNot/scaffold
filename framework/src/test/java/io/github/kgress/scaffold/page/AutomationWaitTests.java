package io.github.kgress.scaffold.page;

import io.github.kgress.scaffold.AutomationWait;
import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.SharedTestVariables;
import io.github.kgress.scaffold.WebDriverWrapper;
import io.github.kgress.scaffold.webelements.DivWebElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.openqa.selenium.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AutomationWaitTests extends BaseUnitTest {

    private final static String TEST_CLASS_NAME2 = "KHAAAAAAAANNNN!!!";
    private final static String DOM_READY_STATE_SCRIPT = "return document.readyState";
    private final static String DOM_EXPECTED_READY_STATE = "complete";
    private final static Long BASE_TIMEOUT = 1L;
    private final static Long TEMP_TIMEOUT = 2L;
    private TestAutomationWait testAutomationWait;

    @Mock
    private DivWebElement mockDivWebElement;

    @BeforeEach
    public void setup() {
        testAutomationWait = createTestAutomationWait();
    }

    @Test
    public void testWaitForTextToContain_success() {
        when(mockDivWebElement.getText()).thenReturn(SharedTestVariables.TEXT_1);
        var textIsThere = testAutomationWait
                .waitForTextToContain(mockDivWebElement, SharedTestVariables.TEXT_1);
        assertTrue(textIsThere);
    }

    @Test
    public void testWaitForTextToContain_fail() {
        when(mockDivWebElement.getText()).thenReturn(SharedTestVariables.TEXT_2);
        assertThrows(TimeoutException.class, () ->
                testAutomationWait.waitForTextToContain(mockDivWebElement, SharedTestVariables.TEXT_1));
    }

    @Test
    public void testWaitUntilElementIsEnabled_success() {
        when(mockDivWebElement.isEnabled()).thenReturn(true);
        var elementIsEnabled = testAutomationWait.waitUntilElementIsEnabled(mockDivWebElement);
        assertTrue(elementIsEnabled);
    }

    @Test
    public void testWaitUntilElementIsEnabled_fail() {
        when(mockDivWebElement.isEnabled()).thenReturn(false);
        assertThrows(TimeoutException.class, () -> testAutomationWait.waitUntilElementIsEnabled(mockDivWebElement));
    }

    @Test
    public void testWaitForElementToHaveClass_success() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class"))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        var elementHasClass = testAutomationWait
                .waitForElementToHaveClass(mockDivWebElement, SharedTestVariables.CLASS_NAME);
        assertTrue(elementHasClass);
    }

    @Test
    public void testWaitForElementToHaveClass_fail() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class")).thenReturn(TEST_CLASS_NAME2);
        assertThrows(TimeoutException.class, () ->
                testAutomationWait.waitForElementToHaveClass(mockDivWebElement,SharedTestVariables.CLASS_NAME));
    }

    @Test
    public void testWaitForElementToNotHaveClass_success() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class"))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        var elementDoesNotHaveClass = testAutomationWait
                .waitForElementToNotHaveClass(mockDivWebElement, TEST_CLASS_NAME2);
        assertTrue(elementDoesNotHaveClass);
    }

    @Test
    public void testWaitForElementToNotHaveClass_fail() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class"))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertThrows(TimeoutException.class, () -> testAutomationWait
                .waitForElementToNotHaveClass(mockDivWebElement, SharedTestVariables.CLASS_NAME));
    }

    @Test
    public void testWaitUntilPageIsLoaded_success() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when(mockWebDriverWrapper.getJavascriptExecutor().executeScript(DOM_READY_STATE_SCRIPT))
                .thenReturn(DOM_EXPECTED_READY_STATE);
        var pageIsLoaded = testAutomationWait.waitUntilPageIsLoaded();
        assertTrue(pageIsLoaded);
    }

    @Test
    public void testWaitUntilPageIsLoaded_fail() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when(mockWebDriverWrapper.getJavascriptExecutor().executeScript(DOM_READY_STATE_SCRIPT))
                .thenReturn("neener, it's not complete");
        assertThrows(TimeoutException.class, () -> testAutomationWait.waitUntilPageIsLoaded());
    }

    @Test
    public void testWaitForTextToContain_tempTimeout_success() {
        when(mockDivWebElement.getText()).thenReturn(SharedTestVariables.TEXT_1);
        var textIsThere = testAutomationWait
                .waitForTextToContain(mockDivWebElement, SharedTestVariables.TEXT_1, TEMP_TIMEOUT);
        assertTrue(textIsThere);
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitForTextToContain_tempTimeout_fail() {
        when(mockDivWebElement.getText()).thenReturn(SharedTestVariables.TEXT_2);
        assertThrows(TimeoutException.class, () -> testAutomationWait
                .waitForTextToContain(mockDivWebElement, SharedTestVariables.TEXT_1, TEMP_TIMEOUT));
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitUntilElementIsEnabled_tempTimeout_success() {
        when(mockDivWebElement.isEnabled()).thenReturn(true);
        var elementIsEnabled = testAutomationWait.waitUntilElementIsEnabled(mockDivWebElement, TEMP_TIMEOUT);
        assertTrue(elementIsEnabled);
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitUntilElementIsEnabled_tempTimeout_fail() {
        when(mockDivWebElement.isEnabled()).thenReturn(false);
        assertThrows(TimeoutException.class, () -> testAutomationWait
                .waitUntilElementIsEnabled(mockDivWebElement, TEMP_TIMEOUT));
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitForElementToHaveClass_tempTimeout_success() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class"))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        var elementHasClass = testAutomationWait
                .waitForElementToHaveClass(mockDivWebElement, SharedTestVariables.CLASS_NAME, TEMP_TIMEOUT);
        assertTrue(elementHasClass);
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitForElementToHaveClass_tempTimeout_fail() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class")).thenReturn(TEST_CLASS_NAME2);
        assertThrows(TimeoutException.class, () -> testAutomationWait
                .waitForElementToHaveClass(mockDivWebElement,SharedTestVariables.CLASS_NAME, TEMP_TIMEOUT));
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitForElementToNotHaveClass_tempTimeout_success() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class"))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        var elementDoesNotHaveClass = testAutomationWait
                .waitForElementToNotHaveClass(mockDivWebElement, TEST_CLASS_NAME2, TEMP_TIMEOUT);
        assertTrue(elementDoesNotHaveClass);
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitForElementToNotHaveClass_tempTimeout_fail() {
        when(mockDivWebElement.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockDivWebElement.getRawWebElement().getAttribute("class"))
                .thenReturn(SharedTestVariables.CLASS_NAME);
        assertThrows(TimeoutException.class, () -> testAutomationWait
                .waitForElementToNotHaveClass(mockDivWebElement, SharedTestVariables.CLASS_NAME, TEMP_TIMEOUT));
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitUntilPageIsLoaded_tempTimeout_success() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when(mockWebDriverWrapper.getJavascriptExecutor().executeScript(DOM_READY_STATE_SCRIPT))
                .thenReturn(DOM_EXPECTED_READY_STATE);
        var pageIsLoaded = testAutomationWait.waitUntilPageIsLoaded(TEMP_TIMEOUT);
        assertTrue(pageIsLoaded);
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    @Test
    public void testWaitUntilPageIsLoaded_tempTimeout_fail() {
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when(mockWebDriverWrapper.getJavascriptExecutor().executeScript(DOM_READY_STATE_SCRIPT))
                .thenReturn("neener, it's not complete");
        assertThrows(TimeoutException.class, () -> testAutomationWait.waitUntilPageIsLoaded(TEMP_TIMEOUT));
        assertEquals(BASE_TIMEOUT, testAutomationWait.getTimeoutInSeconds());
    }

    private static class TestAutomationWait extends AutomationWait {
        public TestAutomationWait(WebDriverWrapper webDriverWrapper, Long timeoutInSeconds) {
            super(webDriverWrapper, timeoutInSeconds);
        }
    }

    private TestAutomationWait createTestAutomationWait() {
        var testAutomationWait = new TestAutomationWait(mockWebDriverWrapper, BASE_TIMEOUT);
        when(mockWebDriverWrapper.getBaseWebDriver()).thenReturn(mockBaseWebDriver);
        return testAutomationWait;
    }
}
