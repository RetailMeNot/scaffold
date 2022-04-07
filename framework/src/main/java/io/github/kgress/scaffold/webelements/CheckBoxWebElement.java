package io.github.kgress.scaffold.webelements;

import io.github.kgress.scaffold.BaseWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Scaffold's strongly typed interpretation of a checkbox element.
 */
public class CheckBoxWebElement extends BaseClickableWebElement {

    /**
     * Creates a new {@link CheckBoxWebElement}. It is highly recommended using {@link By#cssSelector(String)} over
     * another method, such as {@link By#xpath(String)}, in almost all cases as it can be less flaky and less reliant
     * on DOM hierarchy.
     *
     * @see BaseWebElement#BaseWebElement(String)
     * @param cssSelector   the value of the {@link By#cssSelector(String)}
     */
    public CheckBoxWebElement(String cssSelector) {
        super(cssSelector);
    }

    /**
     * Creates a new {@link CheckBoxWebElement} and marks whether the element is hidden. It is highly recommended using
     * {@link By#cssSelector(String)} over another method, such as {@link By#xpath(String)}, in almost all cases as it
     * can be less flaky and less reliant on DOM hierarchy.
     *
     * @see BaseWebElement#BaseWebElement(String)
     * @param cssSelector   the value of the {@link By#cssSelector(String)}
     * @param isHidden      a {@link boolean} to specify if this element could be hidden
     */
    public CheckBoxWebElement(String cssSelector, boolean isHidden) {
        super(cssSelector, isHidden);
    }

    /**
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)}. We strongly recommend using {@link #CheckBoxWebElement(String cssSelector)}
     * in almost all cases.
     *
     * @see BaseWebElement#BaseWebElement(By)
     * @param by    the {@link By} locator
     */
    public CheckBoxWebElement(By by) {
        super(by);
    }

    /**
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)} and mark whether the element is hidden. We strongly recommend using
     * {@link #CheckBoxWebElement(String cssSelector)} in almost all cases.
     *
     * @see BaseWebElement#BaseWebElement(By)
     * @param by        the {@link By} locator
     * @param isHidden  a {@link boolean} to specify if this element could be hidden
     */
    public CheckBoxWebElement(By by, boolean isHidden) {
        super(by, isHidden);
    }

    /**
     * Use this constructor when you'd like to locate an element with a child and parent {@link By} together. Useful
     * when you want a more verbose element definition in context of your websites' DOM.
     *
     * @see BaseWebElement#BaseWebElement(By, By)
     * @param by        the {@link By} locator for the child element
     * @param parentBy  the {@link By} locator for the parent element
     */
    public CheckBoxWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    /**
     * Use this constructor when you'd like to locate an element with a child and parent {@link By} together and mark
     * whether the element is hidden. Useful when you want a more verbose element definition in context of your
     * websites' DOM.
     *
     * @param by       the {@link By} locator to be used by this element
     * @param parentBy the {@link By} locator for the parent element
     * @param isHidden a {@link boolean} to specify if this element could be hidden
     */
    public CheckBoxWebElement(By by, By parentBy, boolean isHidden) {
        super(by, parentBy, isHidden);
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
    public CheckBoxWebElement(By by, By parentBy, WebElement webElement) {
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
     * @param by            the {@link By} locator to be used by this element
     * @param webElement    the {@link WebElement} being wrapped
     */
    @Deprecated
    public CheckBoxWebElement(By by, WebElement webElement) {
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
     * @param webElement    the {@link WebElement} being wrapped
     */
    @Deprecated
    public CheckBoxWebElement(WebElement webElement) {
        super(webElement);
    }

    /**
     * Performs a check action on a checkbox element based on the value passed in.
     *
     * @param value the state in which the checkbox should be in.
     */
    public void check(boolean value) {
        if (value) {
            check();
        } else {
            uncheck();
        }
    }

    /**
     * Checks the checkbox in {@link #check(boolean)}
     */
    private void check() {
        if (!getRawWebElement().isSelected()) {
            getRawWebElement().click();
        }
    }

    /**
     * Unchecks the checkbox in {@link #check(boolean)}
     */
    private void uncheck() {
        if (getRawWebElement().isSelected()) {
            getRawWebElement().click();
        }
    }
}
