package io.github.kgress.scaffold.webelements;

import io.github.kgress.scaffold.BaseWebElement;
import io.github.kgress.scaffold.WebDriverWrapper;
import io.github.kgress.scaffold.WebElementWait;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * This class represents base level clickable options and actions for elements that can be interacted with through
 * a click. Clickable elements should also inherit all properties of a {@link BaseWebElement}.
 *
 * TODO add retry logic to click actions https://github.com/kgress/scaffold/issues/90
 */
public class BaseClickableWebElement extends BaseWebElement {

    /**
     * Creates a new {@link ButtonWebElement}. It is highly recommended using {@link By#cssSelector(String)} over
     * another method, such as {@link By#xpath(String)}, in almost all cases as it can be less flaky and less reliant
     * on DOM hierarchy.
     *
     * @see BaseWebElement#BaseWebElement(String)
     * @param cssSelector   the value of the {@link By#cssSelector(String)}
     */
    public BaseClickableWebElement(String cssSelector) {
        super(cssSelector);
    }

    /**
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)}. We strongly recommend using {@link #BaseClickableWebElement(String)} in almost
     * all cases.
     *
     * @see BaseWebElement#BaseWebElement(By)
     * @param by    the {@link By} locator
     */
    public BaseClickableWebElement(By by) {
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
    public BaseClickableWebElement(By by, By parentBy) {
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
    public BaseClickableWebElement(By by, WebElement webElement) {
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
    public BaseClickableWebElement(By by, By parentBy, WebElement webElement) {
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
    public BaseClickableWebElement(WebElement webElement) {
        super(webElement);
    }

    /**
     * Performs a click on the given element with the following process:
     *
     * - Scrolling the element into current view
     * - Performing the click action on the element
     * - Waits for the page to load prior to proceeding
     *
     * Scrolling the element into view will invoke {@link BaseWebElement#getRawWebElement()} and
     * therefore the element will {@link WebElementWait#waitUntilDisplayed()}. Because the wait
     * already is occurring during the scroll, we should not invoke another wait call and instead
     * find the element directly through selenium, using the {@link WebDriverWrapper}. After
     * the element is found and clicked on, wait until the page is loaded before proceeding.
     *
     * @see WebElement#click()
     */
    public void click() {
        /*
        Scrolls the element into view so selenium can click it. Ideally we always want to scroll
        with the least amount of effort required to get the element into view. This is why the
        script should always bring it into the nearest vertical and horizontal alignment.
         */
        scrollIntoView();

        /*
        Find the element through Selenium directly instead of using our custom getRawWebElement.
        scrollIntoView already waits for the element to be displayed, and we don't want to add
        more waits.
         */
        getWebDriverWrapper().findElement(this.getBy()).click();

        /*
        This is a nice catch all that should happen after the element is clicked on. Sometimes
        websites may have additional javascript or ajax calls when clicking on elements. This
        ensures the state of the page is ready.
         */
        getWebElementWait().waitUntilPageIsLoaded();
    }
}
