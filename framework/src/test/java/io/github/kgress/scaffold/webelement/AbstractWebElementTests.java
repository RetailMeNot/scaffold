package io.github.kgress.scaffold.webelement;

import io.github.kgress.scaffold.BaseUnitTest;
import io.github.kgress.scaffold.models.unittests.MockLogs;
import io.github.kgress.scaffold.models.unittests.MockWebDriver;
import io.github.kgress.scaffold.models.unittests.MockWebElement;
import io.github.kgress.scaffold.util.AutomationUtils;
import io.github.kgress.scaffold.webelements.AbstractWebElement;
import io.github.kgress.scaffold.webelements.DivWebElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractWebElementTests extends BaseUnitTest {

    @Test
    public void testSetBaseWebElement() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertEquals(TAG_NAME_1, testAbstractWebElement.getWebElement().getTagName());
    }

    @Test
    public void testSetBaseWebElementWithLocator() {
        var testElement = new TestableAbstractWebElement(By.id(""), (By) null);

        assertThrows(NoSuchElementException.class, testElement::getWebElement);

        testElement.setBaseElement(mockElement1);
        assertEquals(TAG_NAME_1, testElement.getWebElement().getTagName());
    }

    @Test
    public void testGetChildrenWithoutParentEmpty() {
        assertThrows(NoSuchElementException.class, () -> testAbstractWebElement.findElement(DivWebElement.class, By.id("nothing")));
    }

    @Test
    public void testGetChildrenWithParentEmpty() {
        var testElement = new TestableAbstractWebElement(By.id("parent"), mockElement1);
        testElement.setBaseElement(mockElement1);

        assertThrows(NoSuchElementException.class, () -> testElement.findElement(DivWebElement.class, By.cssSelector("child")));
    }

    @Test
    public void testGetChildrenWithoutParentNotEmpty() {
        List<WebElement> list = new ArrayList<>();
        list.add(mockElement1);
        list.add(mockElement2);

        //Assign the mocks to the SeleniumBase object we're interested in
        var parentElement = new MockWebElement();
        parentElement.setElementsToFind(list);
        mockWebDriver.setElementToFind(parentElement);

        assertEquals("element 1", testAbstractWebElement.findElements(DivWebElement.class, By.id("")).get(0).getWebElement().getText());
    }

    @Test
    public void testGetChildrenWithParentNotEmpty() {
        var parentWebElement = new MockWebElement().text("parent element");
        var childWebElement1 = new MockWebElement().text("element 1");
        var childWebElement2 = new MockWebElement().text("element 2");

        List<WebElement> list = new ArrayList<>();
        list.add(childWebElement1);
        list.add(childWebElement2);

        var baseElement = new MockWebElement();
        baseElement.setElementsToFind(list);
        parentWebElement.setElementToFind(baseElement);
        testAbstractWebElement.setParentElement(parentWebElement);

        assertEquals(testAbstractWebElement.findElements(DivWebElement.class, By.id("")).get(0).getWebElement().getText(), "element 1");
        assertEquals(testAbstractWebElement.findElements(DivWebElement.class, By.id("")).get(1).getWebElement().getText(), "element 2");
    }

    @Test
    public void testGetTagName() {
        var tagName = AutomationUtils.getUniqueString();
        mockElement1.setTagName(tagName);
        var testElement = new TestableAbstractWebElement(mockElement1);

        assertEquals(tagName, testElement.getTagName());
    }

    @Test
    public void testIsEnabled() {
        var testElement = new TestableAbstractWebElement(mockElement1);

        assertTrue(testElement.isEnabled(), "The element should be enabled by default");
        mockElement1.setEnabled(false);
        assertFalse(testElement.isEnabled(), "The element should no longer be enabled");
    }

    @Test
    public void testIsDisplayed() {
        var testElement = new TestableAbstractWebElement(mockElement1);

        assertTrue(testElement.isDisplayed(), "The element should be displayed by default");
        mockElement1.setIsDisplayed(false);
        assertFalse(testElement.isDisplayed(), "The element should no longer be displayed");
    }

    @Test
    public void testGetSize() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertNull(testAbstractWebElement.getSize(), "getSize() should return null");
    }

    @Test
    public void testGetLocation() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertNull(testAbstractWebElement.getLocation(), "getLocation() should return null");
    }

    @Test
    public void testGetCssValue() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertNull(testAbstractWebElement.getCssValue("something"), "getCssValue() should return null");
    }

    @Test
    public void testExists() {
        var testElement = new TestableAbstractWebElement(mockElement1);
        assertTrue(testElement.exists(), "The element should exist");
    }

    @Test
    public void testExistsFalse() {
        assertFalse(testAbstractWebElement.exists(), "The element should not exist");
    }

    /**
     * Test the mock log error created from {@link MockLogs}
     *
     * This test does not utilized the retrieval of logs from the {@link MockWebDriver} and instead tests only the creation
     * and usage of an independent {@link MockLogs} object.
     */
    @Test
    public void testGetErrorLog() {
        // Get the entries and ensure they are correct
        var mockBrowserLogs = mockLogs.get(LogType.BROWSER);
        for (LogEntry entry : mockBrowserLogs) {
            assertEquals(LOG_LEVEL, entry.getLevel());
            assertEquals(TIME_STAMP, entry.getTimestamp());
            assertEquals(MESSAGE, entry.getMessage());
        }
    }

    @Test
    public void testToStringByConstructor() {
        //The constructor that takes only a By locator for the primary element
        var byElement = new TestableAbstractWebElement(By.cssSelector(".foobar"));
        assertEquals("By: [By.cssSelector: .foobar]", byElement.toString(),
                "The element's toString should be correct: 'By' constructor");
    }

    @Test
    public void testToStringByAndByConstructor() {
        //The constructor that takes By locators for both the primary element and the parent element
        var byElementByParent = new TestableAbstractWebElement(By.id("baz"), By.xpath("//ew/xpath"));
        assertEquals("Parent By: [By.xpath: //ew/xpath], By: [By.id: baz]", byElementByParent.toString(),
                "The element's toString should be correct: 'By, parentBy' constructor");
    }

    @Test
    public void testToStringByAndWebElementConstructor() {
        //The constructor that takes a By locator for the main element, and a webelement for the parent
        var byElementWebElementParent = new TestableAbstractWebElement(By.id("baz"), new MockWebElement());
        assertEquals("Parent webelement: [MockWebElement: I Mock Thee!], By: [By.id: baz]", byElementWebElementParent.toString(),
                "The element's toString should be correct: 'By, parent element' constructor");
    }

    @Test
    public void testToStringWebElementConstructor() {
        //Test the webelement-only constructor
        var webElementElement = new TestableAbstractWebElement(new MockWebElement());
        assertEquals("webelement: [MockWebElement: I Mock Thee!]", webElementElement.toString(),
                "The element's toString should be correct: 'webelement' constructor");
    }

    @Test
    public void testToStringNullConstructor() {
        //Test the null, null constructor (should never happen in real life, but you never know...)
        assertEquals("This element was not properly initialized with a By locator or a base element. Please check your code", testAbstractWebElement.toString(),
                "The element's toString should be correct: 'null, null' constructor");
    }

    /**
     * Test to ensure that we can find elements with the {@link AbstractWebElement#findElement(Class, String)}
     * method. This method specifically finds an element with a CSS Selector.
     */
    @Test
    public void testFindElementWithCSS() {
        var sampleText = "Sample Text";
        var abstractWebElement = new TestableAbstractWebElement("testSelector");
        var testWebElement = new MockWebElement();
        testWebElement.setText(sampleText);
        mockWebDriver.setElementToFind(testWebElement);

        var testDivWebElement = abstractWebElement.findElement(DivWebElement.class, ".testSelector");
        assertTrue(testDivWebElement.getText().contains(sampleText));
    }

    @Test
    public void testFindElementsWithCSS() {
        var abstractWebElement = new TestableAbstractWebElement("testSelector");
        var testElement1 = new MockWebElement().text(TEXT_NAME_1);
        var testElement2 = new MockWebElement().text(TEXT_NAME_2);

        var elementList = new ArrayList<WebElement>();
        elementList.add(testElement1);
        elementList.add(testElement2);
        mockWebDriver.setElementsToFind(elementList);

        var testDivWebElements = abstractWebElement.findElements(DivWebElement.class, ".testSelector");
        assertEquals(2, testDivWebElements.size());
        assertTrue(testDivWebElements.get(0).getText().contains(TEXT_NAME_1));
        assertTrue(testDivWebElements.get(1).getText().contains(TEXT_NAME_2));
    }
}
