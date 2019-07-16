package org.scaffold.webelement;

import org.scaffold.BaseUnitTest;
import org.scaffold.webelements.InputWebElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputWebElementTests extends BaseUnitTest {

    private static final String VALUE_TEXT = "test text 1234";
    private InputWebElement testInputWebElement;

    @BeforeEach
    private void setupInputWebElementData() {
        mockElement1.setAttribute("value", VALUE_TEXT);
        testInputWebElement = new InputWebElement(By.cssSelector("test"));
        mockWebDriver.setElementToFind(mockElement1);
    }

    @Test
    public void testTextMethods() {
        var newValueText = "text after sendKeys()";

        assertEquals(VALUE_TEXT, testInputWebElement.getValue());

        testInputWebElement.sendKeys(newValueText);
        assertEquals(newValueText, mockElement1.getText(),
                "The underlying element's text should be the new value now");

        testInputWebElement.clear();
        assertTrue("The underlying element's text should now be empty", mockElement1.getText().isEmpty());
    }

    @Test
    public void testClearAndSendKeys() {
        var newValueText = "text after clearAndSendKeys()";

        assertEquals(VALUE_TEXT, testInputWebElement.getValue(),
                "The base element's text should be correct");

        testInputWebElement.clearAndSendKeys(newValueText);
        assertEquals(newValueText, mockElement1.getText(),
                "The underlying element's text should be the new value now");

        testInputWebElement.clearAndSendKeys("");
        assertTrue("The underlying element's text should now be empty", mockElement1.getText().isEmpty());
    }

    @Test
    public void testGetWebValue() {
        var newValue = "New value";

        testInputWebElement.clearAndSendKeys(newValue);
        assertEquals(newValue, testInputWebElement.getText(),
                "The underlying element's text should be the new value now");
        assertEquals(newValue,  testInputWebElement.getValue());
    }
}
