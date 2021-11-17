package io.github.kgress.scaffold.webelement;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.SharedTestVariables;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.*;

public class BaseClickableTests extends BaseUnitTest {

    @Test
    public void testBaseClickable_byCss_click() {
        var element = new TestBaseClickableWebElement(SharedTestVariables.CSS_SELECTOR1);
        clickAndVerifyIsClicked(element);
    }

    @Test
    public void testBaseClickable_byClass_click() {
        var element = new TestBaseClickableWebElement(By.className(SharedTestVariables.CLASS_NAME));
        clickAndVerifyIsClicked(element);
    }

    private void clickAndVerifyIsClicked(TestBaseClickableWebElement element) {
        when(mockWebElementWait.waitUntilDisplayed()).thenReturn(mockRawWebElement);
        when(element.getRawWebElement()).thenReturn(mockRawWebElement);
        when(mockWebDriverWrapper.getJavascriptExecutor()).thenReturn(mockJavascriptExecutor);
        when((WebElement) mockWebDriverWrapper
                .getJavascriptExecutor()
                .executeScript(SharedTestVariables.SCROLL_INTO_VIEW_SCRIPT, mockRawWebElement))
                .thenReturn(mockRawWebElement);
        element.click();
        verify(mockRawWebElement, times(1)).click();
    }
}
