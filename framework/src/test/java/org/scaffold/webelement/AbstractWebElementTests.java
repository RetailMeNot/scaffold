package org.scaffold.webelement;

import org.scaffold.BaseUnitTest;
import org.scaffold.models.unittests.MockLogs;
import org.scaffold.models.unittests.MockWebDriver;
import org.scaffold.models.unittests.MockWebElement;
import org.scaffold.util.AutomationUtils;
import org.scaffold.webelements.DivWebElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

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

        assertTrue("The element should be enabled by default", testElement.isEnabled());
        mockElement1.setEnabled(false);
        assertFalse("The element should no longer be enabled", testElement.isEnabled());
    }

    @Test
    public void testIsDisplayed() {
        var testElement = new TestableAbstractWebElement(mockElement1);

        assertTrue("The element should be displayed by default", testElement.isDisplayed());
        mockElement1.setIsDisplayed(false);
        assertFalse("The element should no longer be displayed", testElement.isDisplayed());
    }

    @Test
    public void testGetSize() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertNull("getSize() should return null", testAbstractWebElement.getSize());
    }

    @Test
    public void testGetLocation() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertNull("getLocation() should return null", testAbstractWebElement.getLocation());
    }

    @Test
    public void testGetCssValue() {
        testAbstractWebElement.setBaseElement(mockElement1);
        assertNull("getCssValue() should return null", testAbstractWebElement.getCssValue("something"));
    }

    @Test
    public void testExists() {
        var testElement = new TestableAbstractWebElement(mockElement1);
        assertTrue("The element should exist", testElement.exists());
    }

    @Test
    public void testExistsFalse() {
        assertFalse("The element should not exist", testAbstractWebElement.exists());
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
        var byElementByParent = new TestableAbstractWebElement(By.id( "baz"), By.xpath("//ew/xpath"));
        assertEquals("Parent By: [By.xpath: //ew/xpath], By: [By.id: baz]", byElementByParent.toString(),
                "The element's toString should be correct: 'By, parentBy' constructor");
    }

    @Test
    public void testToStringByAndWebElementConstructor() {
        //The constructor that takes a By locator for the main element, and a webelement for the parent
        var byElementWebElementParent = new TestableAbstractWebElement( By.id( "baz"), new MockWebElement() );
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
}
