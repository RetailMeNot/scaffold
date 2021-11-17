package io.github.kgress.scaffold;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.webelements.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

/**
 * This class represents base level interactions that can be done with any element at all times. Since, in theory,
 * not all types of elements have a clickable nature, other classes can inherit this class and expand on
 * functionality, such as {@link BaseClickableWebElement}. Mimics the same functionality provided by {@link WebElement}
 * but includes scaffold specific strong type support.
 *
 * - {@link ButtonWebElement}
 * - {@link CheckBoxWebElement}
 * - {@link DateWebElement}
 * - {@link DivWebElement}
 * - {@link DropDownWebElement}
 * - {@link ImageWebElement}
 * - {@link InputWebElement}
 * - {@link LinkWebElement}
 * - {@link StaticTextWebElement}
 *
 *  Creating a new strong typed element with the provided constructors will never invoke a {@link #getRawWebElement()}
 *  call. {@link #getRawWebElement()} is only ever invoked when a strong typed element is interacted with. This means
 *  it's ideal for use as class variables in a page object to increase the performance of page object instantiation.
 *
 *  Example scenario:
 *  <pre>{@code
 *      &#64;Getter
 *      public class LoginPage extends BasePage {
 *          private InputWebElement usernameInput = new InputWebElement("#username");
 *          private InputWebElement passwordInput = new InputWebElement("#password");
 *          private ButtonWebElement submitButton = new ButtonWebElement("#submit");
 *
 *          public LoginPage() {
 *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
 *          }
 *      }
 *  }
 *  </pre>
 *
 *  If you want to use {@link #findElement(Class, String)} or {@link #findElements(Class, By)}, use them in methods
 *  instead of class variables. The key takeaway here is that you want to preserve your page object instantiation
 *  to only memory references. Invoking the web driver will only slow down your page instantiation if the element(s)
 *  you're trying to find in your variable don't exist. When invoking it, your page will explicitly wait based on the
 *  {@link DesiredCapabilitiesConfigurationProperties#setWaitTimeoutInSeconds(Long)} defined in your spring profile.
 *
 *  Example scenario:
 *  <pre>{@code
 *      &#64;Getter
 *      public class LoginPage extends BasePage {
 *          private InputWebElement usernameInput = new InputWebElement("#username");
 *          private InputWebElement passwordInput = new InputWebElement("#password");
 *          private ButtonWebElement submitButton = new ButtonWebElement("#submit");
 *          private DivWebElement legaleseContainer = new DivWebElement("#legalese");
 *
 *          public LoginPage() {
 *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
 *          }
 *
 *          public List&#60;LinkWebElement&#62; getLegaleseLinks() {
 *              var legaleseLinks = "a";
 *              return getLegaleseContainer().findElements(LinkWebElement.class, legaleseLinks");
 *          }
 *      }
 *  }
 *  </pre>
 */
@Slf4j
public abstract class BaseWebElement {

    @Getter @Setter private By parentBy;
    @Getter @Setter protected By by;

    /**
     * This is set during {@link #BaseWebElement(WebElement)}, {@link #BaseWebElement(By, WebElement)}, and
     * {@link #BaseWebElement(By, By, WebElement)} and represents the raw selenium web element. When this is set,
     * it completely bypasses {@link #getRawWebElement()}'s re-find logic. Therefore, it's possible when interacting
     * with this element, you will encounter a {@link StaleElementReferenceException} since this is only a
     * "last known found location" reference of the element in the DOM.
     *
     * Usage of this variable and {@link #getRawWebElement()} are fundamentally the same.
     */
    protected WebElement baseElement;

    /**
     * Gets the {@link WebElementWait} for the current {@link BaseWebElement} being interacted with.
     */
    @Getter private WebElementWait webElementWait;

    /**
     * Create a new element using the supplied {@link By#cssSelector(String)}. This does not call or invoke WebDriver
     * in any way, nor does it try to find the element on a page. The element is used as a reference for use later.
     * Once the element is set, we create a new {@link WebElementWait} for it.
     *
     * The advantage of using this constructor is to reduce code count when using {@link By#cssSelector(String)}
     * to instantiate your elements. It is highly recommended using {@link By#cssSelector(String)} over
     * {@link By#xpath(String)} in almost all cases as it can be less flaky and less reliant on DOM hierarchy.
     *
     * @param cssSelector   the string value of the {@link By#cssSelector(String)}
     */
    public BaseWebElement(String cssSelector) {
        this.setBy(By.cssSelector(cssSelector));
        setWebElementWait();
    }

    /**
     * Create a new element using the supplied {@link By} locator. This does not call or invoke WebDriver in any way,
     * nor does it try to find the element on a page. The element is used as a reference for use later. Once the
     * element is set, we create a new {@link WebElementWait} for it.
     *
     * Use this constructor when you'd like to locate an element with a {@link By} method different from
     * {@link By#cssSelector(String)}. We strongly recommend using {@link #BaseWebElement(String)} in almost all
     * cases.
     *
     * @param by    the {@link By} locator to be used by this element
     */
    public BaseWebElement(By by) {
        this.setBy(by);
        setWebElementWait();
    }

    /**
     * Creates a new element with a parent element using the supplied {@link By} locators for both elements.
     * Useful when you want a more verbose element definition in context of your websites' DOM. The element is used
     * as a reference for use later. Once the element is set, we create a new {@link WebElementWait} for it.
     *
     * For example, perhaps you have a modal on your website:
     * <pre>{@code
     *      &#64;Getter
     *      public class LoginPage extends BasePage {
     *          private InputWebElement usernameInput = new InputWebElement(
     *                                                      By.cssSelector("#username"), By.cssSelector(.modal));
     *          private InputWebElement passwordInput = new InputWebElement(
     *                                                      By.cssSelector("#password"), By.cssSelector(.modal));
     *          private ButtonWebElement submitButton = new ButtonWebElement(
     *                                                      By.cssSelector("#submit"), By.cssSelector(.modal));
     *
     *          public LoginPage() {
     *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
     *          }
     *      }
     * }
     * </pre>
     *
     * @param by        the {@link By} locator to be used by this element
     * @param parentBy  the {@link By} locator for the parent element
     */
    public BaseWebElement(By by, By parentBy) {
        this.setBy(by);
        this.setParentBy(parentBy);
        setWebElementWait();
    }

    /**
     * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during construction of
     * elements in the {@link #findElements(Class, By)} method.
     *
     * When instantiating new elements with this constructor, There is a risk of a
     * {@link StaleElementReferenceException} occurring when interacting with elements since
     * {@link #getRawWebElement()} will return the {@link #baseElement} on being present. This means we are not re
     * finding the element prior to interacting with it. Use this constructor at your own risk.
     *
     * @param webElement    the {@link WebElement} being wrapped
     */
    public BaseWebElement(WebElement webElement) {
        this.baseElement = webElement;
        setWebElementWait();
    }

    /**
     * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during construction of
     * elements in the {@link #findElements(Class, By)} method.
     *
     * When instantiating new elements with this constructor, There is a risk of a
     * {@link StaleElementReferenceException} occurring when interacting with elements since
     * {@link #getRawWebElement()} will return the {@link #baseElement} on being present. This means we are not re
     * finding the element prior to interacting with it. Use this constructor at your own risk.
     *
     * @param by            the {@link By} locator to be used by this element
     * @param webElement    the {@link WebElement} being wrapped
     */
    public BaseWebElement(By by, WebElement webElement) {
        this.setBy(by);
        this.baseElement = webElement;
        setWebElementWait();
    }

    /**
     * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during construction of
     * elements in the {@link #findElements(Class, By)} method.
     *
     * When instantiating new elements with this constructor, There is a risk of a
     * {@link StaleElementReferenceException} occurring when interacting with elements since
     * {@link #getRawWebElement()} will return the {@link #baseElement} on being present. This means we are not re
     * finding the element prior to interacting with it. Use this constructor at your own risk.
     *
     * @param by            the {@link By} locator to be used by this element
     * @param parentBy      the {@link By} locator to be used by the parent element
     * @param webElement    the {@link WebElement} being wrapped
     */
    public BaseWebElement(By by, By parentBy, WebElement webElement) {
        this.setBy(by);
        this.setParentBy(parentBy);
        this.baseElement = webElement;
        setWebElementWait();
    }

    /**
     * Indicates if the element is enabled
     *
     * @see WebElement#isEnabled()
     * @return  the result as {@link boolean}.
     */
    public boolean isEnabled() {
        return getRawWebElement().isEnabled();
    }

    /**
     * Indicates if the element is displayed
     *
     * @see WebElement#isDisplayed()
     * @return  the result as {@link boolean}.
     */
    public boolean isDisplayed() {
        return getRawWebElement().isDisplayed();
    }

    /**
     * Checks to see if an element's class is active
     *
     * @return the response as true or false
     */
    public boolean isActive() {
        return getRawWebElement().getAttribute("class").contains("active");
    }

    /**
     * Checks to see if an element contains a class with the specified string
     *
     * @param text  the text we're checking the class for
     * @return      as true or false
     */
    public boolean hasClass(String text) {
        return getRawWebElement().getAttribute("class").contains(text);
    }

    /**
     * Returns the value for the specified element's attribute
     *
     * @see WebElement#getAttribute(String)
     * @param name  the attribute to search for
     * @return      the attribute as {@link String}.
     */
    public String getAttribute(String name) {
        return getRawWebElement().getAttribute(name);
    }

    /**
     * Returns the text value of the element
     *
     * @see WebElement#getText()
     * @return  the text as {@link String}.
     */
    public String getText() {
        return getRawWebElement().getText();
    }

    /**
     * Returns the tag name of the element
     *
     * @see WebElement#getTagName()
     * @return  the tag name as {@link String}.
     */
    public String getTagName() {
        return getRawWebElement().getTagName();
    }

    /**
     * Returns the location of the element
     *
     * @see WebElement#getLocation()
     * @return  the location as {@link Point}
     */
    public Point getLocation() {
        return getRawWebElement().getLocation();
    }

    /**
     * Returns the size of the element
     *
     * @see WebElement#getSize()
     * @return  the size as {@link Dimension}
     */
    public Dimension getSize() {
        return getRawWebElement().getSize();
    }

    /**
     * Returns the rectangle of the element.
     *
     * @see WebElement#getRect()
     * @return  the rectangle as {@link Rectangle}
     */
    public Rectangle getRect() {
        return getRawWebElement().getRect();
    }

    /**
     * Returns the css value for a property on the element.
     *
     * @param propertyName  the name of the property
     * @return              the property as {@link String}
     */
    public String getCssValue(String propertyName) {
        return getRawWebElement().getCssValue(propertyName);
    }

    /**
     * Gets the raw {@link WebElement}. This is invoked anytime a user interacts with a strongly typed scaffold
     * element. We will always explicitly wait for the element to be displayed prior to returning. The timeout is
     * defined by the {@link AutomationWait}, which the user sets in their spring profile with
     * {@link DesiredCapabilitiesConfigurationProperties#setWaitTimeoutInSeconds(Long)}.
     *
     * It's strongly recommend to always use Scaffold's strongly typed elements at all times. However, there may be
     * a situation where finding the raw {@link WebElement} is necessary. Use sparingly and efficiently!
     *
     * In addition to finding the raw element, if an exception is encountered, we log errors from the console.
     * Useful for debugging.
     *
     * @return as   {@link WebElement}
     */
    public WebElement getRawWebElement() {
        var parentBy = getParentBy();
        var by = getBy();

        try {
            if (baseElement != null) {
                log.debug(String.format("Using potentially stale WebElement: %s", this.getClass().getSimpleName()));
                return baseElement;
            }
            getWebElementWait().waitUntilDisplayed();
            // TODO https://github.com/kgress/scaffold/issues/108
            if (parentBy != null) {
                log.debug(String.format("Locating element [%s] relative to parent element [%s]", by, parentBy));
                var parentElement = getWebDriverWrapper().findElement(parentBy);
                return parentElement.findElement(by);
            }
            log.debug(String.format("Locating element on page: [%s]", by));
            return getWebDriverWrapper().findElement(by);
        } catch (NoSuchElementException | TimeoutException e) {
            // Try to pull the console error logs and report them. If none exist, add a debug log that none exist
            try {
                LogEntries logEntries = getWebDriverWrapper().manage().logs().get("browser");

                for (LogEntry entry : logEntries) {
                    if (entry.getLevel().equals(SEVERE)) {
                        log.error(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                    } else if (entry.getLevel().equals(WARNING)) {
                        log.warn(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                    } else { // report anything else as info
                        log.info(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                    }
                }
            } catch (UnsupportedCommandException n) {
                log.debug("Logging not supported for the supplied browser.");
            } catch (NullPointerException n) {
                log.debug("No Errors reported in Console Logs during failure.");
            }
            throw e;
        }
    }

    /**
     * Gets the parent element as a raw {@link WebElement}.
     *
     * @return  as {@link WebElement}
     */
    public WebElement getRawParentWebElement() {
        return (WebElement) getWebDriverWrapper()
                .getJavascriptExecutor()
                .executeScript("return arguments[0].parentNode;", getRawWebElement());
    }

    /**
     * Scrolls an element into view.
     *
     * @return as {@link WebElement}
     */
    public WebElement scrollIntoView() {
        return (WebElement) getWebDriverWrapper()
                .getJavascriptExecutor()
                .executeScript("arguments[0].scrollIntoView(true);", getRawWebElement());
    }

    /**
     * Finds an element on the current page and wraps it in a strongly typed scaffold element. The advantage of using
     * this method vs {@link #findElement(Class, By)} is to reduce code count when using {@link By#cssSelector(String)}
     * to find your elements. It is highly recommended using {@link By#cssSelector(String)} over
     * {@link By#xpath(String)} in almost all cases as it can be less flaky and less reliant on DOM hierarchy.
     *
     * @see #findElement(Class, By)
     * @param elementClass  the class of the element that is being found
     * @param cssSelector   the css selector of the element
     * @param <T>           the Type Reference that extends off of {@link BaseWebElement}
     * @return              the element as the specified Type Reference {@link BaseWebElement}
     */
    public <T extends BaseWebElement> T findElement(Class<T> elementClass, String cssSelector) {
        return findElement(elementClass, By.cssSelector(cssSelector));
    }

    /**
     * Finds all elements on the current page and wraps it in a strongly typed scaffold element list.
     *
     * The advantage of using this method vs {@link #findElements(Class, By)} is to reduce code count when
     * using {@link By#cssSelector(String)} to find your elements. It is advised to not invoke this method on the
     * declaration of a class variable. When doing so, it will invoke {@link WebDriver#findElements(By)} which will
     * reduce your Page Object instantiation. We also run the risk of encountering a
     * {@link StaleElementReferenceException} since the {@link #baseElement} is being stored in the constructor.
     *
     *  Example scenario:
     *  <pre>{@code
     *      &#64;Getter
     *      public class LoginPage extends BasePage {
     *          private InputWebElement usernameInput = new InputWebElement("#username");
     *          private InputWebElement passwordInput = new InputWebElement("#password");
     *          private ButtonWebElement submitButton = new ButtonWebElement("#submit");
     *          private DivWebElement legaleseContainer = new DivWebElement("#legalese");
     *
     *          public LoginPage() {
     *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
     *          }
     *
     *          public List&#60;LinkWebElement&#62; getLegaleseLinks() {
     *              var legaleseLinks = "a";
     *              return getLegaleseContainer().findElements(LinkWebElement.class, legaleseLinks");
     *          }
     *      }
     *  }
     *  </pre>
     *
     * @see #findElements(Class, By)
     * @param elementClass  the class of the element that is being found
     * @param cssSelector   the css selector of the element
     * @param <T>           the type reference that extends {@link BaseWebElement}
     * @return              the list of elements as the specified Type Reference {@link BaseWebElement}
     */
    public <T extends BaseWebElement> List<T> findElements(Class<T> elementClass, String cssSelector) {
        return findElements(elementClass, By.cssSelector(cssSelector));
    }

    /**
     * Finds a child element based on a parent element from the current page and wraps it in a strongly typed
     * Scaffold element. Ideally, this should only ever be used in a scenario where you'd like to find a child
     * element based on a parent. If you're setting variables on a Page Object, you'll want to instantiate new
     * instances of them.
     *
     *  Example scenario:
     *  <pre>{@code
     *      &#64;Getter
     *      public class LoginPage extends BasePage {
     *          private InputWebElement usernameInput = new InputWebElement("#username");
     *          private InputWebElement passwordInput = new InputWebElement("#password");
     *          private ButtonWebElement submitButton = new ButtonWebElement("#submit");
     *          private DivWebElement legaleseContainer = new DivWebElement("#legalese");
     *
     *          public LoginPage() {
     *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
     *          }
     *
     *          public List&#60;LinkWebElement&#62; getLegaleseLink() {
     *              var legaleseLink = "a";
     *              return getLegaleseContainer().findElement(LinkWebElement.class, legaleseLink");
     *          }
     *      }
     *  }
     *  </pre>
     *
     * @param elementClass  the strong typed class of the element being found
     * @param by            the mechanism of searching for the element
     * @param <T>           The type reference that extends off of {@link BaseWebElement}
     * @return              the element as the specified Type Reference {@link BaseWebElement}
     */
    public <T extends BaseWebElement> T findElement(Class<T> elementClass, By by) {
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
                returnElement = constructor.newInstance(combinedBy);
            } else {
                Constructor<T> constructor = elementClass.getConstructor(WebElement.class);
                // Locate the child element to pass into the constructor
                WebElement element = getRawWebElement().findElement(by);
                returnElement = constructor.newInstance(element);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate Element properly: " + e);
        }
        return returnElement;
    }

    /**
     * Finds a collection of child elements based on a parent element from the current page and wraps it up in
     * a list of strongly typed Scaffold elements. It is advised to not invoke this method on the declaration
     * of a class variable. When doing so, it will invoke {@link WebDriver#findElements(By)} which will reduce your
     * Page Object instantiation. We also run the risk of encountering a {@link StaleElementReferenceException} since
     * the {@link #baseElement} is being stored in the constructor.
     *
     *  Example scenario:
     *  <pre>{@code
     *      &#64;Getter
     *      public class LoginPage extends BasePage {
     *          private InputWebElement usernameInput = new InputWebElement("#username");
     *          private InputWebElement passwordInput = new InputWebElement("#password");
     *          private ButtonWebElement submitButton = new ButtonWebElement("#submit");
     *          private DivWebElement legaleseContainer = new DivWebElement("#legalese");
     *
     *          public LoginPage() {
     *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
     *          }
     *
     *          public List&#60;LinkWebElement&#62; getLegaleseLinks() {
     *              var legaleseLinks = "a";
     *              return getLegaleseContainer().findElements(LinkWebElement.class, legaleseLinks");
     *          }
     *      }
     *  }
     *  </pre>
     *
     * @param elementClass  the strong typed class of the element being found
     * @param by            the mechanism of searching for the element
     * @param <T>           The type reference that extends off of {@link BaseWebElement}
     * @return              the element as the specified Type Reference {@link BaseWebElement}
     */
    public <T extends BaseWebElement> List<T> findElements(Class<T> elementClass, By by) {
        By combinedBy = null;
        By parentBy = getBy();
        List<WebElement> elements;
        List<T> newElements = new ArrayList<>();

        // TODO https://github.com/kgress/scaffold/issues/108
        if(parentBy instanceof By.ByCssSelector && by instanceof By.ByCssSelector) {
            combinedBy = getCombinedByLocator(parentBy, by);
            elements = getWebDriverWrapper().findElements(combinedBy);
        } else {
            elements = getRawWebElement().findElements(by);
        }

        elements.forEach(element -> {
            try {
                Constructor<T> constructor = elementClass.getConstructor(WebElement.class);
                T newElement = constructor.newInstance(element);
                newElements.add(newElement);
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate Element properly: " + e);
            }
        });
        return newElements;
    }

    public String toString() {
        //This will be built according to what we have available to us--there's a small chance it could end up being
        // just the webelement.toString() in the end
        var toString = "";

        //If there's a parentBy or a parentElement, start with their toString() method. There should only ever be one
        // or the other
        if (null != parentBy) {
            toString = String.format("Parent By: [%s]", parentBy.toString());
        }

        //If there's no by locator, there should be a baseElement. If that's all we've got, we're no worse off than we
        // were with webelement.toString()
        if (null != by) {
            toString += String.format("By: [%s]", by.toString());
        } else if (null != getRawWebElement()) {
            toString += String.format("webelement: [%s]", getRawWebElement().toString());
        }

        //By this time whatever we return should be recognizable
        return toString.isEmpty() ? "This element was not properly initialized with a By locator or a base element. " +
                "Please check your code" : toString;
    }

    /**
     * Provides the current thread's {@link WebDriverWrapper} for executing javascript and checking window handles.
     *
     * @return  as {@link WebDriverWrapper}
     */
    protected WebDriverWrapper getWebDriverWrapper() {
        return TestContext.baseContext().getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
    }

    /**
     * Initializes the webElementWait field by passing in the WebDriver and a copy of this element
     */
    protected void setWebElementWait() {
        this.webElementWait = new WebElementWait(getWebDriverWrapper(), this);
    }

    /**
     * Returns the underlying locator used to locate this element on the page
     *
     * @param by the method in which the element is being located
     * @return the locator as a {@link String}
     */
    private String getUnderlyingLocator(By by) {
        var string = by.toString();
        var index = string.indexOf(" ");
        return string.substring(index + 1, string.length());
    }

    /**
     * Returns the "full" By locator used for this element. If the element has a "parent" defined, it will return the locator used
     *
     * @param parentBy the method in which the parent element is being located
     * @param childBy the method in which the child element is being located
     * @return the locator as a {@link By}
     */
    private By getCombinedByLocator(By parentBy, By childBy) {
        if (!(parentBy instanceof By.ByCssSelector) || !(childBy instanceof By.ByCssSelector)) {
            throw new IllegalArgumentException("Invalid arguments: " + parentBy.toString() + " " + childBy.toString());
        }
        var locator = getUnderlyingLocator(parentBy) + " " + getUnderlyingLocator(childBy);
        return By.cssSelector(locator);
    }

    public void setBaseElement(WebElement baseElement) {
        if (this.by != null) {
            log.debug("This element already has locator information. Assigning a base webelement at this point risks a StaleElementException!!");
        }
        this.baseElement = baseElement;
    }
}
