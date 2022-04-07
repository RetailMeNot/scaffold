package io.github.kgress.scaffold.webelements;

import io.github.kgress.scaffold.BaseWebElement;
import io.github.kgress.scaffold.WebElementWait;
import io.github.kgress.scaffold.util.AutomationUtils;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Scaffold's strongly typed interpretation of a date element.
 *
 * This will frequently need to be subclassed in order to work with particular javascript calendar controls, etc., but
 * the intention is to provide a simple way to put dates in fields, which can frequently be tricky.
 */
@Slf4j
public class DateWebElement extends BaseWebElement {

    // Global DateFormat to be used
    private static DateFormat dateFormat;

    // Local DateFormat which will override the global DateFormat
    private DateFormat localDateFormat;

    /**
     * Creates a new {@link DateWebElement}. It is highly recommended using {@link By#cssSelector(String)} over
     * another method, such as {@link By#xpath(String)}, in almost all cases as it can be less flaky and less reliant
     * on DOM hierarchy.
     *
     * @see BaseWebElement#BaseWebElement(String)
     * @param cssSelector   the value of the {@link By#cssSelector(String)}
     */
    public DateWebElement(String cssSelector) {
        super(cssSelector);
    }

    /**
     * Creates a new {@link DateWebElement} and mark whether the element is hidden. It is highly recommended using
     * {@link By#cssSelector(String)} over another method, such as {@link By#xpath(String)}, in almost all cases as
     * it can be less flaky and less reliant on DOM hierarchy.
     *
     * @param cssSelector the string value of the {@link By#cssSelector(String)}
     * @param isHidden    a {@link boolean} to specify if this element could be hidden
     */
    public DateWebElement(String cssSelector, boolean isHidden) {
        super(cssSelector, isHidden);
    }

    /**
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)}. We strongly recommend using {@link #DateWebElement(String cssSelector)}
     * in almost all cases.
     *
     * @see BaseWebElement#BaseWebElement(By)
     * @param by    the {@link By} locator
     */
    public DateWebElement(By by) {
        super(by);
    }

    /**
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)} and mark whether the element is hidden. We strongly recommend using
     * {@link #DateWebElement(String cssSelector)} in almost all cases.
     *
     * @param by       the {@link By} locator to be used by this element
     * @param isHidden a {@link boolean} to specify if this element could be hidden
     */
    public DateWebElement(By by, boolean isHidden) {
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
    public DateWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    /**
     * Use this constructor when you'd like to locate an element with a child and parent {@link By} together, and mark
     * whether the element is hidden. Useful when you want a more verbose element definition in context of your
     * websites' DOM.
     *
     * @param by       the {@link By} locator to be used by this element
     * @param parentBy the {@link By} locator for the parent element
     * @param isHidden a {@link boolean} to specify if this element could be hidden
     */
    public DateWebElement(By by, By parentBy, boolean isHidden) {
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
    public DateWebElement(By by, By parentBy, WebElement webElement) {
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
    public DateWebElement(By by, WebElement webElement) {
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
    public DateWebElement(WebElement webElement) {
        super(webElement);
    }

    /**
     * Sets a DateFormat which will be globally used by all DateWebElements.
     *
     * @param dateFormat the {@link DateFormat} to use
     */
    public static void setGlobalDateFormat(DateFormat dateFormat) {
        DateWebElement.dateFormat = dateFormat;
    }

    /**
     * Sets a DateFormat which will be used by this DateWebElement only.
     *
     * @param dateFormat the {@link DateFormat} to use
     * @return the date as {@link DateWebElement}
     */
    public DateWebElement setDateFormat(DateFormat dateFormat) {
        this.localDateFormat = dateFormat;
        return this;
    }

    public Date getValue() {
        var value = getAttribute("value");
        Date d = null;
        if (value != null && value.length() > 0) {
            try {
                d = getDateFormat().parse(value);
            } catch (ParseException p) {
                log.error("Error parsing date: " + AutomationUtils.getStackTrace(p));
            }
        }
        return d;
    }

    /**
     * Returns the applicable DateFormat.
     *
     * @return the {@link DateFormat}
     */
    private DateFormat getDateFormat() {
        if (localDateFormat != null) {
            return localDateFormat;
        } else {
            return dateFormat;
        }
    }
}
