package io.github.kgress.scaffold.webelement;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.SharedTestVariables;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BaseClickableAndTypableTests extends BaseUnitTest {

    private final TestBaseClickableAndTypableWebElement elementByCssSelector =
            new TestBaseClickableAndTypableWebElement(SharedTestVariables.CSS_SELECTOR1);
    private final TestBaseClickableAndTypableWebElement elementByClassName =
            new TestBaseClickableAndTypableWebElement(By.className(SharedTestVariables.CLASS_NAME));

    @Test
    public void testBaseClickableAndTypable_byCss_sendKeys() {
        setBaseWhen(elementByCssSelector);
        elementByCssSelector.sendKeys(SharedTestVariables.SEND_KEYS_TEXT);
        verify(mockRawWebElement, times(1)).sendKeys(SharedTestVariables.SEND_KEYS_TEXT);
    }

    @Test
    public void testBaseClickableAndTypable_byCss_getValue() {
        setBaseWhen(elementByCssSelector);
        when(mockRawWebElement.getAttribute(SharedTestVariables.VALUE_ATTRIBUTE))
                .thenReturn(SharedTestVariables.ELEMENT_VALUE);

        var elementValue = elementByCssSelector.getValue();
        assertEquals(SharedTestVariables.ELEMENT_VALUE, elementValue);
    }

    @Test
    public void testBaseClickableAndTypable_byCss_clear() {
        setBaseWhen(elementByCssSelector);
        elementByCssSelector.clear();
        verify(mockRawWebElement, times(1)).clear();
    }

    @Test
    public void testBaseClickableAndTypable_byCss_clearAndSendKey() {
        setBaseWhen(elementByCssSelector);
        elementByCssSelector.clearAndSendKeys(SharedTestVariables.SEND_KEYS_TEXT);
        verify(mockRawWebElement, times(1)).clear();
        verify(mockRawWebElement, times(1)).sendKeys(SharedTestVariables.SEND_KEYS_TEXT);
    }

    @Test
    public void testBaseClickableAndTypable_byClass_sendKeys() {
        setBaseWhen(elementByClassName);
        elementByClassName.sendKeys(SharedTestVariables.SEND_KEYS_TEXT);
        verify(mockRawWebElement, times(1)).sendKeys(SharedTestVariables.SEND_KEYS_TEXT);
    }

    @Test
    public void testBaseClickableAndTypable_byClass_getValue() {
        setBaseWhen(elementByClassName);
        when(mockRawWebElement.getAttribute(SharedTestVariables.VALUE_ATTRIBUTE))
                .thenReturn(SharedTestVariables.ELEMENT_VALUE);

        var elementValue = elementByClassName.getValue();
        assertEquals(SharedTestVariables.ELEMENT_VALUE, elementValue);
    }

    @Test
    public void testBaseClickableAndTypable_byClass_clear() {
        setBaseWhen(elementByClassName);
        elementByClassName.clear();
        verify(mockRawWebElement, times(1)).clear();
    }

    @Test
    public void testBaseClickableAndTypable_byClass_clearAndSendKey() {
        setBaseWhen(elementByClassName);
        elementByClassName.clearAndSendKeys(SharedTestVariables.SEND_KEYS_TEXT);
        verify(mockRawWebElement, times(1)).clear();
        verify(mockRawWebElement, times(1)).sendKeys(SharedTestVariables.SEND_KEYS_TEXT);
    }
}
