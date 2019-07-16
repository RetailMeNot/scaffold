package org.scaffold.webelements.interfaces;

import org.scaffold.webelements.AbstractWebElement;
import org.scaffold.webelements.ButtonWebElement;
import org.scaffold.webelements.CheckBoxWebElement;
import org.scaffold.webelements.DateWebElement;
import org.scaffold.webelements.DivWebElement;
import org.scaffold.webelements.DropDownWebElement;
import org.scaffold.webelements.ImageWebElement;
import org.scaffold.webelements.InputWebElement;
import org.scaffold.webelements.LinkWebElement;
import org.scaffold.webelements.StaticTextWebElement;
import org.scaffold.webelements.WebElementWait;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This interface acts as a buffer between us and Selenium to help guard against drastic changes to their API and functionality
 * in the {@link WebElement}. It handles the wrapping of the {@link WebElement} to our strongly typed elements:
 *
 * {@link ButtonWebElement}
 * {@link CheckBoxWebElement}
 * {@link DateWebElement}
 * {@link DivWebElement}
 * {@link DropDownWebElement}
 * {@link ImageWebElement}
 * {@link InputWebElement}
 * {@link LinkWebElement}
 * {@link StaticTextWebElement}
 */
public interface BaseWebElement {

    /**
     * Returns true if the element is enabled, false otherwise.
     *
     * @return the result as {@link boolean}.
     */
    boolean isEnabled();

    /**
     * Returns the text of the element.
     *
     * @see WebElement#getText()
     *
     * @return the text as {@link String}.
     */
    String getText();

    /**
     * Finds an element of the given class, using the current webelement as the "anchor" point. Similar to the Selenium webelement.findElement() method,
     * this allows you to ask for a LinkWebElement or ButtonWebElement based on the same criteria
     *
     * Note that this method has the advantage of only calling Selenium's findElements() method ONE time, even if there's a parent element--normally you'd call parent.findElement(), then
     * child.findElement(), resulting in multiple calls to WebDriver. This will consolidate all locators into one
     *
     * @param elementClass the class of the element that is being found
     * @param by the mechanism of searching for the element
     * @param <T> The type reference that extends off of {@link AbstractWebElement}
     * @return the element as the specified Type Reference {@link AbstractWebElement}
     */
    <T extends AbstractWebElement> T findElement(Class<T> elementClass, By by);

    /**
     * Finds a list of elements of the given class, using the current webelement as the "anchor" point. Similar to the Selenium webelement.findElements() method,
     * this allows you to ask for a {@literal List<LinkWebElement>} or {@literal List<ButtonWebElement>} based on the same criteria.
     *
     * Note that this method has the advantage of only calling Selenium's findElements() method ONE time, even if there's a parent element--normally you'd call parent.findElement(), then
     * child.findElement(), resulting in multiple calls to WebDriver. This will consolidate all locators into one
     *
     * @param elementClass the class of the element that is being found
     * @param by the mechanism of searching for the element
     * @param <T> the type reference that extends {@link AbstractWebElement}
     * @return the list of elements as the specified Type Reference {@link AbstractWebElement}
     */
    <T extends AbstractWebElement> List<T> findElements(Class<T> elementClass, By by);

    /**
     * Indicates whether or not the element is displayed.
     *
     * @see WebElement#isDisplayed()
     * @return the result as {@link boolean}.
     */
    boolean isDisplayed();

    /**
     * Returns the specified attribute of the element.
     *
     * @see WebElement#getAttribute(String)
     *
     * @param attribute the attribute to search for.
     * @return the attribute as {@link String}.
     */
    String getAttribute(String attribute);

    /**
     * Returns the Dimension object for this element.
     *
     * @see WebElement#getSize()
     *
     * @return the size as {@link Dimension}.
     */
    Dimension getSize();

    /**
     * Allows for specific wait conditions for this element.
     *
     * @return the wait as {@link WebElementWait}.
     */
    WebElementWait getWait();

    /**
     * Gets the location mechanism.
     *
     * @return the location mechanism as {@link By}.
     */
    By getBy();

    /**
     * Retrieves the backing element instance.
     *
     * @return The backing element.
     */
    WebElement getBackingElement();

    /**
     * You can set this, but you'll never get it back with an accessor. This gives you the ability to base a Rich webelement on an existing webelement, but you get no
     * locator information, no parent element, etc., and this could lead to trouble. It's intended for when you can't get that other information, you only
     * have a webelement and you want to give it a particular type (LinkWebElement, etc)
     *
     * @param baseElement the base web element to set
     */
    void setBaseElement(WebElement baseElement);

    /**
     * Returns true if the element exists in the DOM, false otherwise (irrespective of whether or not it's displayed).
     *
     * @return the result as {@link boolean}.
     */
    boolean exists();

    /**
     * Returns the underlying webelement.  If an raw underlying webelement exists rather than a By locator,
     * that is what will be returned instead of a "fresh" lookup.
     *
     * @return the {@link WebElement}.
     */
    WebElement getWebElement();

    /**
     * Forces WebDriver to scroll the webelement into view so that it can be clicked without
     * running into a ElementNotVisibleException.
     */
    void scrollIntoView();

    By getParentBy();

    /**
     * Set the By locator used to identify the parent element.
     *
     * @param parentBy the parent represented with a {@link By} locator.
     */
    void setParentBy(By parentBy);

    /**
     * Set the parent element.
     *
     * @param parentElement the parent element being set
     */
    void setParentElement(WebElement parentElement);

    /**
     * Get the tag name of an element.
     *
     * @return the tag name as {@link String}.
     * @see WebElement#getTagName()
     */
    String getTagName();

    /**
     * Get the location of an element.
     *
     * @return A {@link Point} object
     * @see WebElement#getLocation()
     */
    Point getLocation();

    /**
     * Get the CSS value of an element
     *
     * @param propertyName The name of the CSS property you need to look up.
     * @return The CSS Value for the indicated property.
     * @see WebElement#getCssValue(String)
     */
    String getCssValue(String propertyName);
}
