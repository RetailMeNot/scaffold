package org.scaffold.webelement;

import org.scaffold.BaseUnitTest;
import org.scaffold.models.unittests.MockWebElement;
import org.scaffold.webelements.DropDownWebElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropDownWebElementTests extends BaseUnitTest {

    @Test
    public void testWebElementConstructor() {
        var dropDown = new DropDownWebElement(mockElement1);
        assertEquals(TEXT_NAME_1, dropDown.getText());
    }

    @Test
    public void testByAndParentByConstructor() {
        var dropDown = new DropDownWebElement(By.id("self"), By.id("parent"));
        assertEquals(By.id("self").toString(), dropDown.getBy().toString());
        assertEquals(By.id("parent").toString(), dropDown.getParentBy().toString());
    }

    @Test
    public void testByConstructor() {
        mockWebDriver.setElementToFind(mockElement1);
        var dropDown = new DropDownWebElement(By.id(""));
        assertEquals(TEXT_NAME_1, dropDown.getText());
    }

    @Test
    public void testByAndParentElementConstructor() {
        var parent = new MockWebElement();
        parent.setElementToFind(mockElement1);

        var dropDown = new DropDownWebElement(By.id(""), parent);
        mockWebDriver.setElementToFind(mockElement1);
        assertEquals(TEXT_NAME_1, dropDown.getText());
    }
}
