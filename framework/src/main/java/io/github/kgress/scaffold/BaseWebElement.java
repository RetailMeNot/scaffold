package io.github.kgress.scaffold;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.util.AutomationUtils;
import io.github.kgress.scaffold.webelements.BaseClickableWebElement;
import io.github.kgress.scaffold.webelements.ButtonWebElement;
import io.github.kgress.scaffold.webelements.CheckBoxWebElement;
import io.github.kgress.scaffold.webelements.DateWebElement;
import io.github.kgress.scaffold.webelements.DivWebElement;
import io.github.kgress.scaffold.webelements.DropDownWebElement;
import io.github.kgress.scaffold.webelements.ImageWebElement;
import io.github.kgress.scaffold.webelements.InputWebElement;
import io.github.kgress.scaffold.webelements.LinkWebElement;
import io.github.kgress.scaffold.webelements.StaticTextWebElement;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntry;

/**
 * This class represents base level interactions that can be done with any element at all times.
 * Since, in theory, not all types of elements have a clickable nature, other classes can inherit
 * this class and expand on functionality, such as {@link BaseClickableWebElement}. Mimics the same
 * functionality provided by {@link WebElement} but includes scaffold specific strong type support.
 * <p>
 * - {@link ButtonWebElement} - {@link CheckBoxWebElement} - {@link DateWebElement} - {@link
 * DivWebElement} - {@link DropDownWebElement} - {@link ImageWebElement} - {@link InputWebElement} -
 * {@link LinkWebElement} - {@link StaticTextWebElement}
 * <p>
 * Creating a new strong typed element with the provided constructors will never invoke a {@link
 * #getRawWebElement()} call. {@link #getRawWebElement()} is only ever invoked when a strong typed
 * element is interacted with. This means it's ideal for use as class variables in a page object to
 * increase the performance of page object instantiation.
 * <p>
 * Example scenario:
 * <pre>{@code
 *      &#64;Getter
 *      public class LoginPage extends BasePage {
 *          private final InputWebElement usernameInput = new InputWebElement("#username");
 *          private final InputWebElement passwordInput = new InputWebElement("#password");
 *          private final ButtonWebElement submitButton = new ButtonWebElement("#submit");
 *
 *          public LoginPage() {
 *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
 *          }
 *      }
 *  }
 *  </pre>
 * <p>
 * If you want to use {@link #findElement(Class, String)} or {@link #findElements(Class, By)}, use
 * them in methods instead of class variables. The key takeaway here is that you want to preserve
 * your page object instantiation to only memory references. Invoking the web driver will only slow
 * down your page instantiation if the element(s) you're trying to find in your variable don't
 * exist. When invoking it, your page will explicitly wait based on the {@link
 * DesiredCapabilitiesConfigurationProperties#setWaitTimeoutInSeconds(Long)} defined in your spring
 * profile.
 * <p>
 * Example scenario:
 * <pre>{@code
 *      &#64;Getter
 *      public class LoginPage extends BasePage {
 *          private final InputWebElement usernameInput = new InputWebElement("#username");
 *          private final InputWebElement passwordInput = new InputWebElement("#password");
 *          private final ButtonWebElement submitButton = new ButtonWebElement("#submit");
 *          private final DivWebElement legaleseContainer = new DivWebElement("#legalese");
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

  @Getter
  private boolean isHidden = false;

  @Getter(AccessLevel.PUBLIC)
  @Setter(AccessLevel.PRIVATE)
  protected By parentBy;

  @Getter(AccessLevel.PUBLIC)
  @Setter(AccessLevel.PRIVATE)
  protected By by;

  /**
   * This is set during {@link #BaseWebElement(WebElement)}, {@link #BaseWebElement(By,
   * WebElement)}, and {@link #BaseWebElement(By, By, WebElement)} and represents the raw selenium
   * web element. When this is set, it completely bypasses {@link #getRawWebElement()}'s re-find
   * logic. Therefore, it's possible when interacting with this element, you will encounter a {@link
   * StaleElementReferenceException} since this is only a "last known found location" reference of
   * the element in the DOM.
   * <p>
   * Usage of this variable and {@link #getRawWebElement()} are fundamentally the same.
   */
  @Getter
  @Setter
  @Deprecated
  protected WebElement baseElement;

  /**
   * Gets the {@link WebElementWait} for the current {@link BaseWebElement} being interacted with.
   */
  @Getter
  private WebElementWait webElementWait;

  /**
   * Create a new element using the supplied {@link By#cssSelector(String)}. This does not call or
   * invoke WebDriver in any way, nor does it try to find the element on a page. The element is used
   * as a reference for use later. Once the element is set, we create a new {@link WebElementWait}
   * for it.
   * <p>
   * The advantage of using this constructor is to reduce code count when using {@link
   * By#cssSelector(String)} to instantiate your elements. It is highly recommended using {@link
   * By#cssSelector(String)} over {@link By#xpath(String)} in almost all cases as it can be less
   * flaky and less reliant on DOM hierarchy.
   *
   * @param cssSelector the string value of the {@link By#cssSelector(String)}
   */
  public BaseWebElement(String cssSelector) {
    this(By.cssSelector(cssSelector));
  }

  /**
   * Create a new element using the supplied {@link By#cssSelector(String)} and mark whether it is hidden.
   * This does not call or invoke WebDriver in any way, nor does it try to find the element on a page.
   * The element is used as a reference for use later. Once the element is set, we create a new
   * {@link WebElementWait} for it.
   * <p>
   * The advantage of using this constructor is to reduce code count when using {@link
   * By#cssSelector(String)} to instantiate your elements. It is highly recommended using {@link
   * By#cssSelector(String)} over {@link By#xpath(String)} in almost all cases as it can be less
   * flaky and less reliant on DOM hierarchy.
   *
   * @param cssSelector the string value of the {@link By#cssSelector(String)}
   * @param isHidden a {@link boolean} to specify if this element could be hidden
   */
  public BaseWebElement(String cssSelector, boolean isHidden) {
    this(By.cssSelector(cssSelector), isHidden);
  }

  /**
   * Create a new element using the supplied {@link By} locator. This does not call or invoke
   * WebDriver in any way, nor does it try to find the element on a page. The element is used as a
   * reference for use later. Once the element is set, we create a new {@link WebElementWait} for
   * it.
   * <p>
   * Use this constructor when you'd like to locate an element with a {@link By} method different
   * from {@link By#cssSelector(String)}. We strongly recommend using {@link
   * #BaseWebElement(String)} in almost all cases.
   *
   * @param by the {@link By} locator to be used by this element
   */
  public BaseWebElement(By by) {
    this(by, null, false);
  }

  /**
   * Create a new element using the supplied {@link By} locator and mark whether it is hidden. This
   * does not call or invoke WebDriver in any way, nor does it try to find the element on a page. The
   * element is used as a reference for use later. Once the element is set, we create a new
   * {@link WebElementWait} for it.
   * <p>
   * Use this constructor when you'd like to locate an element with a {@link By} method different
   * from {@link By#cssSelector(String)}. We strongly recommend using {@link
   * #BaseWebElement(String)} in almost all cases.
   *
   * @param by the {@link By} locator to be used by this element
   * @param isHidden a {@link boolean} to specify if this element could be hidden
   */
  public BaseWebElement(By by, boolean isHidden) {
    this(by, null, isHidden);
  }

  /**
   * Creates a new element with a parent element using the supplied {@link By} locators for both
   * elements. Useful when you want a more verbose element definition in context of your websites'
   * DOM. The element is used as a reference for use later. Once the element is set, we create a new
   * {@link WebElementWait} for it.
   * <p>
   * For example, perhaps you have a modal on your website:
   * <pre>{@code
   *      &#64;Getter
   *      public class LoginPage extends BasePage {
   *          private final InputWebElement usernameInput =
   *            new InputWebElement(By.cssSelector("#username"), By.cssSelector(.modal));
   *          private final InputWebElement passwordInput =
   *          new InputWebElement(By.cssSelector("#password"), By.cssSelector(.modal));
   *          private final ButtonWebElement submitButton =
   *          new ButtonWebElement(By.cssSelector("#submit"), By.cssSelector(.modal));
   *
   *          public LoginPage() {
   *              verifyIsOnPage(getUsernameInput(), getPasswordInput(), getSubmitButton());
   *          }
   *      }
   * }
   * </pre>
   *
   * @param by       the {@link By} locator to be used by this element
   * @param parentBy the {@link By} locator for the parent element
   */
  public BaseWebElement(By by, By parentBy) {
    this(by, parentBy, false);
  }

  /**
   * Creates a new element with a parent element using the supplied {@link By} locators for both
   * elements. Useful when you want a more verbose element definition in context of your websites'
   * DOM. The element is used as a reference for use later. Once the element is set, we create a new
   * {@link WebElementWait} for it.
   *
   * @param by       the {@link By} locator to be used by this element
   * @param parentBy the {@link By} locator for the parent element
   * @param isHidden a {@link boolean} to specify if this element could be hidden
   */
  public BaseWebElement(By by, By parentBy, boolean isHidden) {
    if (by instanceof By.ByXPath || parentBy instanceof By.ByXPath) {
      log.warn("It is strongly recommended to use a CSS selector for elements "
              + "instead of XPATH when instantiating new Scaffold elements. Failure in using "
              + "a CSS selector may hinder the availability "
              + "of functionality on the element.");
    }
    this.by = by;
    this.parentBy = parentBy;
    this.isHidden = isHidden;
    setWebElementWait();
  }

  /**
   * This constructor is {@link Deprecated}. Please use a constructor that uses a {@link By}
   * locator. Using a constructor with {@link WebElement} will bypass scaffold's core
   * functionality. An example of using a {@link By} locator constructor: <pre>{@code
   * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
   * }</pre>
   * <p>
   * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during
   * construction of elements in the {@link #findElements(Class, By)} method.
   * <p>
   * When instantiating new elements with this constructor, There is a risk of a {@link
   * StaleElementReferenceException} occurring when interacting with elements since {@link
   * #getRawWebElement()} will return the {@link #baseElement} on being present. This means we are
   * not re finding the element prior to interacting with it. Use this constructor at your own
   * risk.
   *
   * @param webElement the {@link WebElement} being wrapped
   */
  @Deprecated
  public BaseWebElement(WebElement webElement) {
    log.error(String.format("Usage of element [%s] with a WebElement constructor "
            + "bypasses core Scaffold functionality and will result in unpredictable "
            + "behavior. Please instantiate new Scaffold elements with a constructor that "
            + "does not have a WebElement.",
        webElement));
    this.setBaseElement(webElement);
    setWebElementWait();
  }

  /**
   * This constructor is {@link Deprecated}. Please use a constructor that uses a {@link By}
   * locator. Using a constructor with {@link WebElement} will bypass scaffold's core
   * functionality. An example of using a {@link By} locator constructor: <pre>{@code
   * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
   * }</pre>
   * <p>
   * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during
   * construction of elements in the {@link #findElements(Class, By)} method.
   * <p>
   * When instantiating new elements with this constructor, There is a risk of a {@link
   * StaleElementReferenceException} occurring when interacting with elements since {@link
   * #getRawWebElement()} will return the {@link #baseElement} on being present. This means we are
   * not re finding the element prior to interacting with it. Use this constructor at your own
   * risk.
   *
   * @param by         the {@link By} locator to be used by this element
   * @param webElement the {@link WebElement} being wrapped
   */
  @Deprecated
  public BaseWebElement(By by, WebElement webElement) {
    log.error(String.format("Usage of element [%s] with a WebElement constructor "
            + "bypasses core Scaffold functionality and will result in unpredictable "
            + "behavior. Please instantiate new Scaffold elements with a constructor that "
            + "does not have a WebElement.",
        webElement));
    this.setBy(by);
    this.setBaseElement(webElement);
    setWebElementWait();
  }

  /**
   * This constructor is {@link Deprecated}. Please use a constructor that uses a {@link By}
   * locator. Using a constructor with {@link WebElement} will bypass scaffold's core
   * functionality. An example of using a {@link By} locator constructor: <pre>{@code
   * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
   * }</pre>
   * <p>
   * Creates a new Scaffold element with a raw {@link WebElement}. This is primarily used during
   * construction of elements in the {@link #findElements(Class, By)} method.
   * <p>
   * When instantiating new elements with this constructor, There is a risk of a {@link
   * StaleElementReferenceException} occurring when interacting with elements since {@link
   * #getRawWebElement()} will return the {@link #baseElement} on being present. This means we are
   * not re finding the element prior to interacting with it. Use this constructor at your own
   * risk.
   *
   * @param by         the {@link By} locator to be used by this element
   * @param parentBy   the {@link By} locator to be used by the parent element
   * @param webElement the {@link WebElement} being wrapped
   */
  @Deprecated
  public BaseWebElement(By by, By parentBy, WebElement webElement) {
    log.error(String.format("Usage of element [%s] with a WebElement constructor "
            + "bypasses core Scaffold functionality and will result in unpredictable "
            + "behavior. Please instantiate new Scaffold elements with a constructor that "
            + "does not have a WebElement.",
        webElement));
    this.setBy(by);
    this.setParentBy(parentBy);
    this.setBaseElement(webElement);
    setWebElementWait();
  }

  /**
   * Indicates if the element is enabled
   *
   * @return the result as {@link boolean}.
   * @see WebElement#isEnabled()
   */
  public boolean isEnabled() {
    try {
      var element = getRawWebElement();
      return element != null && element.isEnabled();
    } catch (WebDriverException e) {
      return false;
    }
  }

  /**
   * Indicates if the element is displayed
   *
   * @return the result as {@link boolean}.
   * @see WebElement#isDisplayed()
   */
  public boolean isDisplayed() {
    try {
      var element = getRawWebElement();
      return element != null && element.isDisplayed();
    } catch (WebDriverException e) {
      return false;
    }
  }

  /**
   * Checks to see if an element's class is active
   *
   * @return the response as true or false
   */
  public boolean isActive() {
    try {
      var element = getRawWebElement();
      return element != null && element.getAttribute("class").contains("active");
    } catch (WebDriverException e) {
      return false;
    }
  }

  /**
   * Checks to see if an element contains a class with the specified string
   *
   * @param text the text we're checking the class for
   * @return as true or false
   */
  public boolean hasClass(String text) {
    try {
      var element = getRawWebElement();
      return element != null && element.getAttribute("class").contains(text);
    } catch (WebDriverException e) {
      return false;
    }
  }

  /**
   * Returns the value for the specified element's attribute
   *
   * @param name the attribute to search for
   * @return the attribute as {@link String}.
   * @see WebElement#getAttribute(String)
   */
  public String getAttribute(String name) {
    return getRawWebElement().getAttribute(name);
  }

  /**
   * Returns the text value of the element
   *
   * @return the text as {@link String}.
   * @see WebElement#getText()
   */
  public String getText() {
    return getRawWebElement().getText();
  }

  /**
   * Returns the tag name of the element
   *
   * @return the tag name as {@link String}.
   * @see WebElement#getTagName()
   */
  public String getTagName() {
    return getRawWebElement().getTagName();
  }

  /**
   * Returns the location of the element
   *
   * @return the location as {@link Point}
   * @see WebElement#getLocation()
   */
  public Point getLocation() {
    return getRawWebElement().getLocation();
  }

  /**
   * Returns the size of the element
   *
   * @return the size as {@link Dimension}
   * @see WebElement#getSize()
   */
  public Dimension getSize() {
    return getRawWebElement().getSize();
  }

  /**
   * Returns the rectangle of the element.
   *
   * @return the rectangle as {@link Rectangle}
   * @see WebElement#getRect()
   */
  public Rectangle getRect() {
    return getRawWebElement().getRect();
  }

  /**
   * Returns the css value for a property on the element.
   *
   * @param propertyName the name of the property
   * @return the property as {@link String}
   */
  public String getCssValue(String propertyName) {
    return getRawWebElement().getCssValue(propertyName);
  }

  /**
   * Gets the raw {@link WebElement}. This is invoked anytime a user interacts with a strongly typed
   * scaffold element. We will always explicitly wait for the element to be displayed prior to
   * returning. The timeout is defined by the {@link AutomationWait}, which the user sets in their
   * spring profile with {@link DesiredCapabilitiesConfigurationProperties#setWaitTimeoutInSeconds(Long)}.
   * <p>
   * It's strongly recommend to always use Scaffold's strongly typed elements at all times. However,
   * there may be a situation where finding the raw {@link WebElement} is necessary. Use sparingly
   * and efficiently!
   * <p>
   * In addition to finding the raw element, if an exception is encountered, we log errors from the
   * console. Useful for debugging.
   *
   * @return as   {@link WebElement}
   */
  public WebElement getRawWebElement() {
    try {
            /*
            WebElement constructors are deprecated, and we should warn the user to not use these
            anymore. They bypass core Scaffold functionality such as waiting for elements to be
            displayed, handling state elements, etc.
             */
      if (getBaseElement() != null) {
        log.error(String.format("Usage of element [%s] with a WebElement constructor "
                + "bypasses core Scaffold functionality and will result in unpredictable "
                + "behavior. Please instantiate new Scaffold elements with a constructor "
                + "that does not have a WebElement.",
            getBaseElement()));
        return getBaseElement();
      }

            /*
            Always wait for the element to be displayed prior to finding it. This gives the caller
            a decent amount of time to make sure the element is completely displayed prior to
            interacting with it.
             */
      if (!isHidden) {
        getWebElementWait().waitUntilDisplayed();
      }

            /*
            If the parent by is not null, we should do two separate find element calls to respect
            the fact these two By locators might be of completely different types.
             */
      if (getParentBy() != null) {
        log.debug(String.format("Locating element [%s] relative to parent element [%s]",
            getBy(), getParentBy()));
        var parentElement = getWebDriverWrapper().findElement(getParentBy());
        return parentElement.findElement(getBy());
      } else {
        log.debug(String.format("Locating element [%s]", getBy()));
        return getWebDriverWrapper().findElement(getBy());
      }
    } catch (NoSuchElementException | TimeoutException e) {
      reportBrowserLogs();
      throw e;
    }
  }

  /**
   * Gets the parent element as a raw {@link WebElement}.
   *
   * @return as {@link WebElement}
   */
  public WebElement getRawParentWebElement() {
    return (WebElement) getWebDriverWrapper()
        .getJavascriptExecutor()
        .executeScript("return arguments[0].parentNode;", getRawWebElement());
  }

  /**
   * Scrolls an element into view. Due to an issue found on https://github.com/kgress/scaffold/issues/115,
   * we updated the scroll to center align the element instead of top align.
   *
   * @return as {@link WebElement}
   */
  public WebElement scrollIntoView() {
    return (WebElement) getWebDriverWrapper()
        .getJavascriptExecutor()
        .executeScript("arguments[0].scrollIntoView({block: 'nearest', inline: 'nearest'});",
            getRawWebElement());
  }

  /**
   * This method is now {@link Deprecated} in favor of instantiating new elements with a constructor
   * that uses a {@link By} locator, like {@link BaseWebElement#BaseWebElement(By)}. Or, a
   * constructor that sets the parent, like {@link BaseWebElement#BaseWebElement(By)}.
   * <p>
   * Fundamentally, this functionality convolutes the intended design of instantiating new elements
   * on a page by introducing another failure point on the usage of an element that uses a {@link
   * WebElement} in the constructor, such as {@link BaseWebElement#BaseWebElement(WebElement)}.
   * Constructors that use {@link WebElement} completely bypass Scaffold provided magic and
   * introduce unpredictable behavior, such as {@link StaleElementReferenceException}'s.
   * <p>
   * When instantiating new elements, use a {@link By} constructor on a Page Object. For example:
   * <pre>{@code
   * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
   * private final DivWebElement linkOnHeader =
   *       new DivWebElement(By.cssSelector(".link_on_header"), By.cssSelector(".header"));
   * }</pre>
   * <p>
   * Finds an element on the current page and wraps it in a strongly typed scaffold element. The
   * advantage of using this method vs {@link #findElement(Class, By)} is to reduce code count when
   * using {@link By#cssSelector(String)} to find your elements. It is highly recommended using
   * {@link By#cssSelector(String)} over {@link By#xpath(String)} in almost all cases as it can be
   * less flaky and less reliant on DOM hierarchy.
   *
   * @param elementClass the class of the element that is being found
   * @param cssSelector  the css selector of the element
   * @param <T>          the Type Reference that extends off of {@link BaseWebElement}
   * @return the element as the specified Type Reference {@link BaseWebElement}
   * @see #findElement(Class, By)
   */
  @Deprecated
  public <T extends BaseWebElement> T findElement(Class<T> elementClass, String cssSelector) {
    return findElement(elementClass, By.cssSelector(cssSelector));
  }

  /**
   * This method is now {@link Deprecated} in favor of instantiating new elements with a constructor
   * that uses a {@link By} locator, like {@link BaseWebElement#BaseWebElement(By)}. Or, a
   * constructor that sets the parent, like {@link BaseWebElement#BaseWebElement(By)}.
   * <p>
   * Fundamentally, this functionality convolutes the intended design of instantiating new elements
   * on a page by introducing another failure point on the usage of an element that uses a {@link
   * WebElement} in the constructor, such as {@link BaseWebElement#BaseWebElement(WebElement)}.
   * Constructors that use {@link WebElement} completely bypass Scaffold provided magic and
   * introduce unpredictable behavior, such as {@link StaleElementReferenceException}'s.
   * <p>
   * When instantiating new elements, use a {@link By} constructor. For example: <pre>{@code
   * private final DivWebElement header = new DivWebElement(By.cssSelector(".header"));
   * }</pre>
   * <p>
   * Finds a child element based on a parent element from the current page and wraps it in a
   * strongly typed Scaffold element. Ideally, this should only ever be used in a scenario where
   * you'd like to find a child element based on a parent. If you're setting variables on a Page
   * Object, you'll want to instantiate new instances of them.
   * <p>
   * Example scenario:
   * <pre>{@code
   *      &#64;Getter
   *      public class LoginPage extends BasePage {
   *          private final InputWebElement usernameInput = new InputWebElement("#username");
   *          private final InputWebElement passwordInput = new InputWebElement("#password");
   *          private final ButtonWebElement submitButton = new ButtonWebElement("#submit");
   *          private final DivWebElement legaleseContainer = new DivWebElement("#legalese");
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
   * @param elementClass the strong typed class of the element being found
   * @param by           the mechanism of searching for the element
   * @param <T>          The type reference that extends off of {@link BaseWebElement}
   * @return the element as the specified Type Reference {@link BaseWebElement}
   */
  @Deprecated
  public <T extends BaseWebElement> T findElement(Class<T> elementClass, By by) {
    T returnElement;
    By combinedBy = null;
    By updatedParentBy = null;

    /*
    The below if logic only considers a situation where a findElement call is being invoked
    on another element. In other words, creating a new instance of a strong typed element and
    performing a findElement from it. That means there are a total of 3 by locators potentially
    in use:
      * The parent and child By locators from the "parent element"
      * The By locator being passed in to the method from the caller, the "child element"

    This logic does not directly take into account a scenario where this method is invoked
    from a class variable. I believe this scenario only is when users have created a custom web
    element that extends from a Scaffold strongly typed element, In situations like that, the
    following can be assumed:
      * This class's getParentBy() and getBy() likely will be null
      * This class's getParentBy() likely might not be null, and this class's getBy likely will
        be null

    With the above assumptions, the logic is that because the combinedBy and the updateParentBy
    will likely be null, the code logic path will still default to returning a new element that
    is built with the WebElement.class constructor. We are moving away from creating elements in
    this way, which is why findElement() is deprecated and eventually will be set private
    in a future update. https://github.com/kgress/scaffold/issues/132 addresses this band aid
    to allow existing users the same functionality from before while we move towards the
    breaking code change.
    */
    if (getParentBy() != null) {
      if (!(getParentBy() instanceof By.ByXPath) && !(getBy() instanceof By.ByXPath)) {
        combinedBy = combineByLocators(getParentBy(), by);
      }
    } else {
      updatedParentBy = getBy();
      if (!(by instanceof By.ByXPath) && !(updatedParentBy instanceof By.ByXPath)) {
        combinedBy = combineByLocators(updatedParentBy, by);
      }
    }

    try {
      if (combinedBy != null) {
        var constructor = elementClass.getConstructor(By.class);
        returnElement = constructor.newInstance(combinedBy);
      } else if (updatedParentBy != null) {
        var constructor = elementClass.getConstructor(By.class, By.class);
        returnElement = constructor.newInstance(by, updatedParentBy);
      } else {
                /*
                Worst case scenario here. We don't want a situation where the caller is
                interacting with a scaffold element without a By locator.
                */
        var constructor = elementClass.getConstructor(WebElement.class);
        var element = getRawWebElement().findElement(by);
        returnElement = constructor.newInstance(element);
      }
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
        IllegalAccessException e) {
      throw new RuntimeException("Could not instantiate Element properly: " + e);
    }
    return returnElement;
  }

  /**
   * Finds all elements on the current page and wraps it in a strongly typed scaffold element list.
   * <p>
   * The advantage of using this method vs {@link #findElements(Class, By)} is to reduce code count
   * when using {@link By#cssSelector(String)} to find your elements. It is advised to not invoke
   * this method on the declaration of a class variable. When doing so, it will invoke {@link
   * WebDriver#findElements(By)} which will reduce your Page Object instantiation. We also run the
   * risk of encountering a {@link StaleElementReferenceException} since the {@link #baseElement} is
   * being stored in the constructor.
   * <p>
   * Example scenario:
   * <pre>{@code
   *      &#64;Getter
   *      public class LoginPage extends BasePage {
   *          private final InputWebElement usernameInput = new InputWebElement("#username");
   *          private final InputWebElement passwordInput = new InputWebElement("#password");
   *          private final ButtonWebElement submitButton = new ButtonWebElement("#submit");
   *          private final DivWebElement legaleseContainer = new DivWebElement("#legalese");
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
   * @param elementClass the class of the element that is being found
   * @param cssSelector  the css selector of the element
   * @param <T>          the type reference that extends {@link BaseWebElement}
   * @return the list of elements as the specified Type Reference {@link BaseWebElement}
   * @see #findElements(Class, By)
   */
  public <T extends BaseWebElement> List<T> findElements(Class<T> elementClass,
      String cssSelector) {
    return findElements(elementClass, By.cssSelector(cssSelector));
  }

  /**
   * Finds a collection of child elements based on a parent element from the current page and wraps
   * it up in a list of strongly typed Scaffold elements. It is advised to not invoke this method on
   * the declaration of a class variable. When doing so, it will invoke {@link
   * WebDriver#findElements(By)} which will reduce your Page Object instantiation. We also run the
   * risk of encountering a {@link StaleElementReferenceException} since the {@link #baseElement} is
   * being stored in the constructor.
   * <p>
   * Example scenario:
   * <pre>{@code
   *      &#64;Getter
   *      public class LoginPage extends BasePage {
   *          private final InputWebElement usernameInput = new InputWebElement("#username");
   *          private final InputWebElement passwordInput = new InputWebElement("#password");
   *          private final ButtonWebElement submitButton = new ButtonWebElement("#submit");
   *          private final DivWebElement legaleseContainer = new DivWebElement("#legalese");
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
   * @param elementClass the strong typed class of the element being found
   * @param by           the mechanism of searching for the element
   * @param <T>          The type reference that extends off of {@link BaseWebElement}
   * @return the element as the specified Type Reference {@link BaseWebElement}
   */
  public <T extends BaseWebElement> List<T> findElements(Class<T> elementClass, By by) {
    /*
    Performs a find element first on this By (which waits for the element to be displayed),
    and then performs findElements() with the caller's by as the child.
     */
    List<WebElement> elements = getRawWebElement().findElements(by);
    if (elements.size() == 0) {
      return List.of();
    }

    if (by instanceof By.ByXPath) {
      return IntStream.range(0, elements.size())
          .mapToObj(i -> createXPathChild(elementClass, by, i))
          .collect(Collectors.toList());
    } else {
      final var siblingElementsOfElement = getAllSiblingElementsOfElement(elements.get(0));
      return IntStream.range(0, siblingElementsOfElement.size())
          .mapToObj(i -> createCssSelectorChild(elementClass, by, elements,
              siblingElementsOfElement, i))
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
  }

  public String toString() {
    /*
    This will be built according to what we have available to us--there's a small chance it
    could end up being just the webelement.toString() in the end
    */
    var toString = "";

    /*
    If there's a parentBy or a parentElement, start with their toString() method. There should
    only ever be one or the other
     */
    if (null != getParentBy()) {
      toString = String.format("Parent By: [%s]", getParentBy().toString());
    }

    /*
    If there's no by locator, there should be a baseElement. If that's all we've got, we're no
    worse off than we were with webelement.toString()
    */
    if (null != getBy()) {
      toString += String.format("By: [%s]", getBy().toString());
    } else if (null != getRawWebElement()) {
      toString += String.format("webelement: [%s]", getRawWebElement().toString());
    }

    // By this time whatever we return should be recognizable
    return toString.isEmpty() ?
        "This element was not properly initialized with a By locator or a base element. " +
            "Please check your code" : toString;
  }

  /**
   * Provides the current thread's {@link WebDriverWrapper} for executing javascript and checking
   * window handles.
   *
   * @return as {@link WebDriverWrapper}
   */
  protected WebDriverWrapper getWebDriverWrapper() {
    return TestContext.baseContext().getWebDriverContext().getWebDriverManager()
        .getWebDriverWrapper();
  }

  /**
   * Initializes the webElementWait field by passing in the WebDriver and a copy of this element
   */
  protected void setWebElementWait() {
    this.webElementWait = new WebElementWait(getWebDriverWrapper(), this);
  }

  /**
   * Combines the {@link By} locators of a parent and a child into a single locator. When combining,
   * we need to make sure the combined locator is of the same type, where type = ofCSS or XPATH.
   * ofCSS is a type that can be converted into a CSS selector, like tag, id, name, etc. XPATH only
   * has one potential By type provided by Selenium.
   *
   * @param parentBy the parent {@link By} locator
   * @param childBy  the child {@link By} locator
   * @return as a combined {@link By} with the parent and child
   */
  private By combineByLocators(By parentBy, By childBy) {
    if (parentBy instanceof By.ByXPath || childBy instanceof By.ByXPath) {
      var exceptionMessage = new RuntimeException(String.format(
          "Both By locators must be of type CSS when combining. Parent: %s. Child: %s",
          parentBy, childBy));
      if (!(parentBy instanceof By.ByXPath)) {
        throw exceptionMessage;
      } else if (!(childBy instanceof By.ByXPath)) {
        throw exceptionMessage;
      } else {
        var combinedLocator = getCombinedByLocatorAsString(parentBy, childBy);
        return By.xpath(combinedLocator);
      }
    } else {
      var convertedParentBy = convertIsOfCssByToCssSelector(parentBy);
      var convertedChildBy = convertIsOfCssByToCssSelector(childBy);
      var combinedLocator = getCombinedByLocatorAsString(convertedParentBy, convertedChildBy);
      return By.cssSelector(combinedLocator);
    }
  }

  /**
   * Creates a new element using {@link By.ByCssSelector}
   *
   * @param elementClass         The element type to be created
   * @param by                   The {@link By} selector to be used to create the element.
   * @param elements             The list of elements found using the Selenium
   *                             {@link WebElement#findElements} method.
   * @param siblingElementsByTag The list of elements that are siblings and are the same tag as the
   *                             elements found by {@link WebElement#findElements}
   * @param i                    The index used to find the correct element among siblings that are
   *                             the same.
   * @param <T>The               Type of element being created.
   * @return a new instance of type T
   */
  private <T extends BaseWebElement> T createCssSelectorChild(Class<T> elementClass, By by,
      List<WebElement> elements, List<WebElement> siblingElementsByTag, int i) {
    if (elements.contains(siblingElementsByTag.get(i))) {
      final By newElementSelector = createElementSelector(
          determineCombinedBy(getBy(), getParentBy(), by),
          i);

      try {
        if (newElementSelector != null) {
          final var constructor = elementClass.getConstructor(By.class);
          return constructor.newInstance(newElementSelector);
        } else {
          /*
          Worst case scenario here. We don't want a situation where the caller is
          interacting with a scaffold element without a By locator.
           */
          final var constructor = elementClass.getConstructor(WebElement.class);
          return constructor.newInstance(elements.get(i));
        }
      } catch (Exception e) {
        throw new RuntimeException("Could not instantiate Element properly: " + e);
      }
    }
    return null;
  }

  /**
   * A pure function that encapsulates the logic to determine what the base {@link By} selector for
   * the children elements.
   *
   * @param elementRootSelector the child root {@link By} selector as determined by
   *    {@link #determineCombinedBy(By, By, By)}
   * @param elementIndex the index of the rawElement found with {@link WebElement#findElements(By)}
   *
   * @return a new By selector to create elements that are children under the element that
   *         performed the findElements query.
   */
  private By createElementSelector(By elementRootSelector, int elementIndex) {
    if (elementRootSelector instanceof By.ByXPath) {
      return By.xpath(String.format("%s[%s]",
          AutomationUtils.getUnderlyingLocatorByString(elementRootSelector),
          elementIndex + 1));
    } else {
      return By.cssSelector(String.format("%s:nth-child(%s)",
          AutomationUtils.getUnderlyingLocatorByString(elementRootSelector),
          elementIndex + 1));
    }
  }

  /**
   * Creates a new element using {@link By.ByXPath}
   *
   * @param elementClass The element type to be created
   * @param by           The {@link By} selector to be used to create the element.
   * @param i            The index used to find the correct element among siblings that are the
   *                     same.
   * @param <T>          The Type of element being created.
   * @return a new instance of type T
   */
  private <T extends BaseWebElement> T createXPathChild(Class<T> elementClass, By by,
      int i) {
    try {
      final By newElementSelector = createElementSelector(by, i);
      final var constructor = elementClass.getConstructor(By.class, By.class);
      return constructor.newInstance(newElementSelector,
          getParentBy() == null ? getBy() : getParentBy());
    } catch (Exception e) {
      throw new RuntimeException("Could not instantiate Element properly: " + e);
    }
  }

  /**
   * A pure function that encapsulates the logic to determine parent selector for the children
   * elements that need to be instantiated.
   *
   * @param selfSelector the {@link By} selector referenced by {@link #by}
   * @param selfParentSelector the {@link By} selector referenced by {@link #parentBy}
   * @param queriedSelector the {@link By} selector passed in as argument to
   *    {@link #findElements(Class, By)}
   *
   * @return a instance of a {@link By} selector that is the combined selector of {@link #by} and
   *    {@link #parentBy}
   */
  private By determineCombinedBy(By selfSelector, By selfParentSelector, By queriedSelector) {
    /*
    The current By of the instance becomes the new parent, and the By passed in by the caller
    is the new child. This will happen in the event the strongly typed element is not
    instantiated with a parentBy locator using the (By, By) constructor.

    If the parentBy is already set on this class, and it differs from the By
    passed in, create a new variable that defines this classes By as the parent, and the By
    from the caller as the child.

    In either of these two cases, if those By locators are of type CSS, we should go ahead
    and combine them. Otherwise, if any of these By locators are XPATH, we need to invoke
    getRawWebElement to perform two find element calls, one for the parent and one for the
    parent + child.
    */
    if (queriedSelector instanceof By.ByXPath) {
      return queriedSelector;
    } else if (selfParentSelector != null) {
      if (!(selfParentSelector instanceof By.ByXPath) && !(selfSelector instanceof By.ByXPath)) {
        var existingParentChildBy = combineByLocators(selfParentSelector, selfSelector);
        return combineByLocators(existingParentChildBy, queriedSelector);
      }
    } else {
      if (!(selfSelector instanceof By.ByXPath)) {
        return combineByLocators(selfSelector, queriedSelector);
      }
    }
    return selfSelector;
  }

  /**
   * A pure function that encapsulates the logic to retrieve all possible sibling elements of the
   * same type as the provided {@link WebElement}.  The purpose is correctly index the pseudo class
   * nth-child when create the actual requested elements.
   *
   * @param webElement  The element to use, to retrieve all possible siblings
   * @return A list of all possible siblings that match the tag of the provided {@link WebElement}.
   */
  private List<WebElement> getAllSiblingElementsOfElement(WebElement webElement) {
    final String elementTagName = webElement.getTagName();

    /* This gets the parent element (didn't use selector provided because there may be an
     *  element between that parent selector and the actual child elements wanted.
     *  Using the parent raw element, find all elements of the same tag that was found
     *   using the provided by selector. We use this list to provide the real nth-child index.
     */
    return webElement.findElement(By.xpath("./..")).findElements(By.tagName(elementTagName));
  }

  /**
   * Returns the "full" By locator used for this element. If the element has a "parent" defined, it
   * will return the locator used
   *
   * @param parentBy the method in which the parent element is being located
   * @param childBy  the method in which the child element is being located
   * @return the locator as a {@link By}
   */
  private String getCombinedByLocatorAsString(By parentBy, By childBy) {
    return String.format("%s %s", AutomationUtils.getUnderlyingLocatorByString(parentBy),
        AutomationUtils.getUnderlyingLocatorByString(childBy));
  }

  /**
   * Converts an ofCSS type into a CSS selector. Cannot convert XPATH at this time.
   *
   * @param by the {@link By} type that is ofCSS
   * @return as {@link By}
   */
  private By convertIsOfCssByToCssSelector(By by) {
    var locator = AutomationUtils.getUnderlyingLocatorByString(by);

    if (by instanceof By.ByCssSelector) {
      return by;
    } else if (by instanceof By.ByTagName) {
      return By.cssSelector(locator);
    } else if (by instanceof By.ById) {
      return By.cssSelector(String.format("#%s", locator));
    } else if (by instanceof By.ByClassName) {
      return By.cssSelector(String.format(".%s", locator));
    } else if (by instanceof By.ByName) {
      return By.cssSelector(String.format("[name=%s]", locator));
    } else if (by instanceof By.ByLinkText) {
      return By.cssSelector(String.format("a[href=%s]", locator));
    } else if (by instanceof By.ByPartialLinkText) {
      return By.cssSelector(String.format("a[href~=%s]", locator));
    } else {
      throw new RuntimeException("Cannot convert XPATH to a CSS Selector");
    }
  }

  /**
   * Pulls the browser's error logs and adds them into the console.
   */
  private void reportBrowserLogs() {
    try {
      var logEntries = getWebDriverWrapper().manage().logs().get("browser");
      for (LogEntry entry : logEntries) {
        if (entry.getLevel().equals(SEVERE)) {
          log.error(
              new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        } else if (entry.getLevel().equals(WARNING)) {
          log.warn(
              new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        } else { // report anything else as info
          log.info(
              new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        }
      }
    } catch (UnsupportedCommandException n) {
      log.debug("Logging not supported for the supplied browser.");
    } catch (NullPointerException n) {
      log.debug("No Errors reported in Console Logs during failure.");
    }
  }
}
