package org.scaffold.webdriverwrapper;

import org.scaffold.BaseUnitTest;
import org.scaffold.webelements.DivWebElement;
import org.scaffold.webelements.LinkWebElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebDriverWrapperTests extends BaseUnitTest {

    @Test
    public void testFindElement() {
        mockWebDriver.setElementToFind(mockElement1);
        assertEquals(TEXT_NAME_1, webDriverWrapper.findElement(By.cssSelector("element1")).getText());
    }

    @Test
    public void testFindElements() {
        List<WebElement> elementList = new ArrayList<>();
        elementList.add(mockElement1);
        elementList.add(mockElement2);
        mockWebDriver.setElementsToFind(elementList);

        assertEquals(TEXT_NAME_1, webDriverWrapper.findElements(By.cssSelector("element1")).get(0).getText());
        assertEquals(TEXT_NAME_2, webDriverWrapper.findElements(By.cssSelector("element2")).get(1).getText());
    }

    @Test
    public void testFindElementsStrongType() {
        List<WebElement> elementList = new ArrayList<>();
        elementList.add(mockElement1);
        elementList.add(mockElement2);
        mockWebDriver.setElementsToFind(elementList);

        assertEquals(TEXT_NAME_1, webDriverWrapper.findElements(DivWebElement.class, By.cssSelector("element1")).get(0).getText());
        assertEquals(TEXT_NAME_2, webDriverWrapper.findElements(LinkWebElement.class, By.cssSelector("element2")).get(1).getText());
    }

    @Test
    public void testFindElementDoesntExist() {
        assertThrows(NoSuchElementException.class, () -> mockWebDriver.findElement(By.id("elementDoesNotExist")));
    }
}
