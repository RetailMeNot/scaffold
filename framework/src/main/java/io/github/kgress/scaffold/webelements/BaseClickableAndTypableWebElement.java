package io.github.kgress.scaffold.webelements;

import io.github.kgress.scaffold.BaseWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class BaseClickableAndTypableWebElement extends BaseClickableWebElement {

    /**
     * Creates a new {@link BaseClickableAndTypableWebElement}. It is highly recommended using
     * {@link By#cssSelector(String)} over another method, such as {@link By#xpath(String)}, in almost all cases as it
     * can be less flaky and less reliant on DOM hierarchy.
     *
     * @see BaseWebElement#BaseWebElement(String)
     * @param cssSelector   the value of the {@link By#cssSelector(String)}
     */
    public BaseClickableAndTypableWebElement(String cssSelector) {
        super(cssSelector);
    }

    /**
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)}. We strongly recommend using {@link #BaseClickableAndTypableWebElement(String)}
     * in almost all cases.
     *
     * @see BaseWebElement#BaseWebElement(By)
     * @param by    the {@link By} locator
     */
    public BaseClickableAndTypableWebElement(By by) {
        super(by);
    }

    /**
     * Use this constructor when you'd like to locate an element with a child and parent {@link By} together. Useful
     * when you want a more verbose element definition in context of your websites' DOM.
     *
     * @see BaseWebElement#BaseWebElement(By, By)
     * @param by        the {@link By} locator for the child element
     * @param parentBy  the {@link By} locator for the parent element
     */
    public BaseClickableAndTypableWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    /**
     * This constructor is {@link Deprecated}. Please use a constructor that uses a {@link By}
     * locator. Using a constructor with {@link WebElement} will bypass scaffold's core
     * functionality. An example of using a {@link By} locator constructor: <pre>{@code
     * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
     * }</pre>
     *
     * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during construction of
     * elements in the {@link #findElements(Class, By)} method.
     *
     * When instantiating new elements with this constructor, There is a risk of a
     * {@link StaleElementReferenceException} occurring when interacting with elements since
     * {@link #getRawWebElement()} will return the raw web element on being present. This means we are not re
     * finding the element prior to interacting with it. Use this constructor at your own risk.
     *
     * @param by            the {@link By} locator to be used by this element
     * @param webElement    the {@link WebElement} being wrapped
     */
    @Deprecated
    public BaseClickableAndTypableWebElement(By by, WebElement webElement) {
        super(by, webElement);
    }

    /**
     * This constructor is {@link Deprecated}. Please use a constructor that uses a {@link By}
     * locator. Using a constructor with {@link WebElement} will bypass scaffold's core
     * functionality. An example of using a {@link By} locator constructor: <pre>{@code
     * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
     * }</pre>
     *
     * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during construction of
     * elements in the {@link #findElements(Class, By)} method.
     *
     * When instantiating new elements with this constructor, There is a risk of a
     * {@link StaleElementReferenceException} occurring when interacting with elements since
     * {@link #getRawWebElement()} will return the raw web element on being present. This means we are not re
     * finding the element prior to interacting with it. Use this constructor at your own risk.
     *
     * @param by            the {@link By} locator to be used by this element
     * @param parentBy      the {@link By} locator to be used by the parent element
     * @param webElement    the {@link WebElement} being wrapped
     */
    @Deprecated
    public BaseClickableAndTypableWebElement(By by, By parentBy, WebElement webElement) {
        super(by, parentBy, webElement);
    }

    /**
     * This constructor is {@link Deprecated}. Please use a constructor that uses a {@link By}
     * locator. Using a constructor with {@link WebElement} will bypass scaffold's core
     * functionality. An example of using a {@link By} locator constructor: <pre>{@code
     * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
     * }</pre>
     *
     * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during construction of
     * elements in the {@link #findElements(Class, By)} method.
     *
     * When instantiating new elements with this constructor, There is a risk of a
     * {@link StaleElementReferenceException} occurring when interacting with elements since
     * {@link #getRawWebElement()} will return the raw web element on being present. This means we are not re
     * finding the element prior to interacting with it. Use this constructor at your own risk.
     *
     * @param webElement    the {@link WebElement} being wrapped
     */
    @Deprecated
    public BaseClickableAndTypableWebElement(WebElement webElement) {
        super(webElement);
    }

    /**
     * Performs a {@link WebElement#sendKeys(CharSequence...)} operation to the element.
     *
     * @param keys  the text or keyboard action to execute
     */
    public void sendKeys(CharSequence ...keys) {
        getRawWebElement().sendKeys(keys);
    }

    /**
     * Gets the current value of the dropdown.
     *
     * @return as {@link String}
     */
    public String getValue() {
        return getRawWebElement().getAttribute("value");
    }

    /**
     * Clears the text from the input.
     *
     * @see WebElement#clear()
     */
    public void clear() {
        getRawWebElement().clear();
    }

    /**
     * Clears the input field and sends the given keys. If the string is null or empty, this will simply have the
     * effect of clearing the field. NOTE: If you just send whitespace, it *will* be typed into the field.
     *
     * @param keys the text to send to the input
     */
    public void clearAndSendKeys(String keys) {
        this.clear();
        if (keys != null && keys.length() > 0) {
            this.sendKeys(keys);
        }
    }
}
