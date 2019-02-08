package com.retailmenot.scaffold.webelements;

import com.retailmenot.scaffold.webdriver.TestContext;
import com.retailmenot.scaffold.webdriver.WebDriverWrapper;
import com.retailmenot.scaffold.webdriver.interfaces.TestContextSetting;
import com.retailmenot.scaffold.webelements.interfaces.BaseWebElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Point;
import org.openqa.selenium.NoSuchElementException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This will serve as the "base" web element for everything you want to do on a page.
 */
@Slf4j
public abstract class AbstractWebElement implements BaseWebElement {

    protected By by;
    protected WebElement backingElement;
    private WebElement parentElement;
    private By parentBy;

    /**
     *  TODO Defining this might lead to a StaleElementException--it's better to use locators whenever the webelement is
     *   required, rather than "caching" it in a member variable, but sometimes you only have a webelement
     *   (i.e. when calling findElements() on the WebDriverWrapper)
     */
    protected WebElement baseElement;
    private WebElementWait webElementWait;

    /**
     * Create a new element, using the supplied By locator. This does not call or invoke WebDriver in any way--it merely stores the locator for later use
     *
     * @param by The locator to be used by this element
     */
    public AbstractWebElement(By by) {
        this.setBy(by);
        initWait();
    }

    /**
     * Defines a Selenium webelement as a child of the given baseElement--all locators will take place relative to the parent. For example,
     * a modal dialog box is a webelement with the locator By.css( ".modal" ), you would define it and its children like so:
     * <p>
     * DivWebElement parentModal           = new DivWebElement( By.cssSelector( ".modal" );
     * InputWebElement childInputElement   = new InputWebElement( By.cssSelector( ".userName, parentModal );
     * ButtonWebElement childButtonElement = new ButtonWebElement( By.cssSelector( ".submit", parentModal );
     * <p>
     * Setting things up in this way will allow you to set the parent element to be top level of the DOM used to locate the child object. The
     * locator for the child will always be found by invoking parent.findElement() - which prunes the DOM down to the most localized elements necessary
     * to find the one you're interested in, and which also reduces the chance of an ambiguous element
     *
     * @param by            The locator to be used by this element
     * @param parentElement The parent element--has the effect of calling this element's by locator relative to the parent
     */
    public AbstractWebElement(By by, WebElement parentElement) {
        this.setBy(by);
        this.setParentElement(parentElement);
        initWait();
    }

    /**
     * Defines a Selenium webelement as a child of the given baseElement--all locators will take place relative to the parent. For example,
     * a modal dialog box is a webelement with the locator By.css( ".modal" ), you would define it and its children like so:
     * <p>
     * InputWebElement childInputElement   = new InputWebElement( By.cssSelector( ".userName, By.cssSelector( ".modal" ) );
     * ButtonWebElement childButtonElement = new ButtonWebElement( By.cssSelector( ".submit", By.cssSelector( ".modal" ) );
     * <p>
     * Setting things up in this way will allow you to set the parent element to be top level of the DOM used to locate the child object. The
     * locator for the child will always be found by invoking parent.findElement() - which prunes the DOM down to the most localized elements necessary
     * to find the one you're interested in, and which also reduces the chance of an ambiguous element
     *
     * @param by       The locator to be used by this element
     * @param parentBy The parent element's locator--has the effect of calling this element's by locator relative to the parent
     */
    public AbstractWebElement(By by, By parentBy) {
        this.setBy(by);
        this.setParentBy(parentBy);
        initWait();
    }

    /**
     * You don't get to do this very often. When it's invoked, the calling method uses reflection to make it accessible
     *
     * @param element The underlying Selenium webelement wrapped by this class
     */
    protected AbstractWebElement(WebElement element) {
        this.baseElement = element;
        initWait();
    }

    @Override
    public WebElementWait getWait() {
        return webElementWait;
    }

    /**
     * @param name
     * @return
     * @see WebElement#getAttribute(String)
     */
    @Override
    public String getAttribute(String name) {
        return getWebElement().getAttribute(name);
    }

    /**
     * Checks for null, then determines whether the element is enabled on the page or not (if the element is null, it's considered "disabled", and will
     * return false, rather than just throw an exception
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        var element = this.getWebElement(false);
        return element != null && element.isEnabled();
    }

    /**
     * Indicates whether or not an element is displayed. This method will swallow a NoSuchElementException and return false if the element doesn't exist
     *
     * @return
     */
    @Override
    public boolean isDisplayed() {
        try {
            var element = this.getWebElement(false);
            return element != null && element.isDisplayed();
        } catch (WebDriverException e) {
            // Not logging the exception here as the output isn't really useful to use
            return false;
        }
    }

    /**
     * @return A Dimension object
     * @see WebElement#getSize()
     */
    @Override
    public Dimension getSize() {
        return getWebElement().getSize();
    }

    @Override
    public String toString() {
        //This will be built according to what we have available to us--there's a small chance it could end up being just the webelement.toString() in the end
        var toString = "";

        //If there's a parentBy or a parentElement, start with their toString() method. There should only ever be one or the other
        if (null != parentBy) {
            toString = String.format("Parent By: [%s]", parentBy.toString());
        } else if (null != parentElement) {
            toString = String.format("Parent webelement: [%s]", parentElement.toString());
        }

        //If there's no by locator, there should be a baseElement. If that's all we've got, we're no worse off than we were with webelement.toString()
        if (null != by) {
            if (hasParentElement()) {
                toString += ", ";
            }
            toString += String.format("By: [%s]", by.toString());
        } else if (null != baseElement) {
            toString += String.format("webelement: [%s]", baseElement.toString());
        }

        //By this time whatever we return should be recognizable
        return toString.isEmpty() ? "This element was not properly initialized with a By locator or a base element. Please check your code" : toString;
    }

    /**
     * @return
     * @see WebElement#getText()
     */
    @Override
    public String getText() {
        return this.getWebElement().getText();
    }

    public <T extends AbstractWebElement> T findElement(Class<T> elementClass, By by) {
        By combinedBy = null;
        var parentBy = getBy();
        // Basically here if both locators are css locators, we're going to go ahead and combine them.
        // If they're not, well use the given by locator or cached webelement -- whichever is applicable
        if(parentBy instanceof By.ByCssSelector && by instanceof By.ByCssSelector) {
            combinedBy = getCombinedByLocator(parentBy, by);
        }
        T returnElement;
        try {
            if (combinedBy != null ) {
                Constructor<T> constructor = elementClass.getConstructor(By.class);
                returnElement = constructor.newInstance( combinedBy );
            } else {
                Constructor<T> constructor = elementClass.getConstructor(WebElement.class);
                // Locate the child element to pass into the constructor
                WebElement element = getWebElement().findElement(by);
                returnElement = constructor.newInstance( element );
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate Element properly: " + e);
        }
        return returnElement;
    }

    public <T extends AbstractWebElement> List<T> findElements(Class<T> elementClass, By by) {
        By parentBy = getBy();
        List<WebElement> elements;
        // Basically here if both locators are css locators, we're going to go ahead and combine them
        if(parentBy instanceof By.ByCssSelector && by instanceof By.ByCssSelector) {
            By combinedBy = getCombinedByLocator(parentBy, by);
            elements = getWebDriverWrapper().findElements(combinedBy);
        } else {
            elements = getWebElement().findElements(by);
        }
        List<T> newElements = new ArrayList<>();
        for (WebElement element: elements) {
            try {
                Constructor<T> constructor = elementClass.getConstructor(WebElement.class);
                T newElement = constructor.newInstance(element);
                newElements.add(newElement);
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate Element properly: " + e);
            }
        }
        return newElements;
    }

    /**
     * Returns the By locator for this webelement. This will not work when the element was initialized by supplying a Selenium webelement in the constructor
     *
     * @return The locator used by this element
     */
    @Override
    public By getBy() {
        return this.by;
    }

    /**
     * Retrieves the backing element instance.
     *
     * @return The backing element.
     */
    @Override
    public WebElement getBackingElement() {
        return backingElement;
    }

    /**
     * You can set this, but you'll never get it back with an accessor. This gives you the ability to base a Rich webelement on an existing webelement, but you get no
     * locator information, no parent element, etc., and this could lead to trouble. It's intended for when you can't get that other information, you only
     * have a webelement and you want to give it a particular type (LinkWebElement, etc)
     *
     * @param baseElement
     */
    @Override
    public void setBaseElement(WebElement baseElement) {
        if (this.by != null) {
            log.debug("This element already has locator information. Assigning a base webelement at this point risks a StaleElementException!!");
        }
        this.baseElement = baseElement;
    }

    /**
     * Returns true if the element exists in the DOM, false otherwise (irrespective of whether or not it's displayed)
     *
     * @return
     */
    @Override
    public boolean exists() {
        var element = getWebElement(false);
        return element != null;
    }

    /**
     * Returns the underlying webelement.  If an raw underlying webelement exists rather than a By locator,
     * that is what will be returned instead of a "fresh" lookup
     *
     * @return
     */
    @Override
    public WebElement getWebElement() {
        // Wait for the element to be displayed if configured
        if (TestContext.baseContext().getSetting(Boolean.class, TestContextSetting.WAIT_FOR_DISPLAY_ENABLED)) {
            getWait().waitUntilDisplayed();
        }
        return getWebElement(true);
    }

    /**
     * Forces WebDriver to scroll the webelement into view so that it can be clicked without
     * running into a ElementNotVisibleException:
     */
    @Override
    public void scrollIntoView() {
        try {
            getWebDriverWrapper().getJavascriptExecutor().executeScript("arguments[0].scrollIntoView(true);", getWebElement());
        } catch (Exception e) {
            log.warn(String.format("Error scrolling into view for %s: %s", by, e));
        }
    }

    /**
     * Set the By locator used to identify the parent element
     *
     * @param parentBy
     */
    @Override
    public void setParentBy(By parentBy) {
        this.parentBy = parentBy;
    }

    /**
     * Set the parent element
     *
     * @param parentElement
     */
    @Override
    public void setParentElement(WebElement parentElement) {
        if (parentElement != null) {
            this.parentElement = parentElement;
        }
    }

    /**
     * @return
     * @see WebElement#getTagName()
     */
    @Override
    public String getTagName() {
        return getWebElement().getTagName();
    }

    /**
     * @return A Point object
     * @see WebElement#getLocation()
     */
    @Override
    public Point getLocation() {
        return getWebElement().getLocation();
    }

    /**
     * @param propertyName The name of the CSS property you need to look up
     * @return The CSS Value for the indicated property
     * @see WebElement#getCssValue(String)
     */
    @Override
    public String getCssValue(String propertyName) {
        return getWebElement().getCssValue(propertyName);
    }

    /**
     * If the element was defined with a baseElement, return it--though there's a risk it could be "stale"
     *
     * @return
     */
    protected WebElement getWebElement(boolean throwExceptionIfNotFound) {
        try {
            if (baseElement != null) {
                log.debug("Using potentially stale webelement: " + this.getClass().getSimpleName());
                return baseElement;
            }
            if (hasParentElement()) {
                log.debug("Locating element relative to parent element [%s]", by);
                return getParentElement().findElement(by);
            }
            log.debug("Locating element on page: [%s]", by);
            return getWebDriverWrapper().findElement(by);
        } catch (NoSuchElementException e) {
            if (throwExceptionIfNotFound) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Returns the underlying locator used to locate this element on the page
     *
     * @param by
     * @return
     */
    protected String getUnderlyingLocator(By by) {
        var string = by.toString();
        var index = string.indexOf(" ");
        return string.substring(index + 1, string.length());
    }

    /**
     * Returns the "full" By locator used for this element. If the element has a "parent" defined, it will return the locator used
     *
     * @param parentBy
     * @param childBy
     * @return
     */
    protected By getCombinedByLocator(By parentBy, By childBy) {
        if (!(parentBy instanceof By.ByCssSelector) || !(childBy instanceof By.ByCssSelector)) {
            throw new IllegalArgumentException("Invalid arguments: " + parentBy.toString() + " " + childBy.toString());
        }
        var locator = getUnderlyingLocator(parentBy) + " " + getUnderlyingLocator(childBy);
        return By.cssSelector(locator);
    }

    /**
     * Returns null if there is no parent element or parent By defined
     *
     * @return
     */
    protected WebElement getParentElement() {
        if (null != parentBy) {
            return getWebDriverWrapper().findElement(parentBy);
        }
        return parentElement;
    }

    /**
     * Get the By locator used to identify the parent element
     *
     * @return
     */
    public By getParentBy() {
        return parentBy;
    }

    /**
     * Returns true if a parent element has been defined, and is not null
     *
     * @return
     */
    private boolean hasParentElement() {
        //If the parentElement is null, this element has no parent element, so return false
        return parentElement != null || parentBy != null;
    }

    /**
     * Set the By locator for this element
     *
     * @param by The locator to be used by this element
     */
    private void setBy(By by) {
        this.by = by;
    }

    /**
     * Initializes the webElementWait field by passing in the WebDriver and a copy of this element
     */
    private void initWait() {
        this.webElementWait = new WebElementWait(getWebDriverWrapper(), this);

    }

    protected WebDriverWrapper getWebDriverWrapper() {
        return TestContext.baseContext().getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
    }
}
