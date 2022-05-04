package io.github.kgress.scaffold;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import java.util.HashMap;
import java.util.Map;

import io.github.kgress.scaffold.util.AutomationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A class of Wait mechanisms useful for testing web applications. These are meant to supplement those available
 * via WebDriver's built-in wait mechanism.
 *
 * In most cases, custom wait conditions won't be completely necessary since Scaffold already handles waits behind
 * the scenes when interacting with elements (i.e., whenever "getWebElement" is invoked). However, there are times when
 * a website is particularly difficult to interact with due to extraneous situations such as slow or abundant firing
 * JS. Only invoke a custom wait when you absolutely need to and try not to overpopulate your code base with a ton of
 * custom waiting.
 *
 * If you're experiencing timeout issues, there could be an issue with how your code base is using Scaffold. Or, there
 * could be an area of improvement required with Scaffold (e.g., perhaps a missing feature). Be sure to take advantage
 * of {@link BasePage#verifyIsOnPage(BaseWebElement...)} for all of your page objects to ensure the page is loaded
 * and verified prior to interacting with it.
 */
@Slf4j
@Getter
public class AutomationWait {

    private final static String CLASS_ATTRIBUTE = "class";

    @Getter
    private final WebDriverWrapper webDriverWrapper;

    @Getter
    private final Map<Long, WebDriverWait> waits = new HashMap<>();

    @Getter
    @Setter
    private Long timeoutInSeconds;

    /**
     *
     * @param webDriverWrapper  the {@link WebDriverWrapper} this automation wait is being assigned to
     * @param timeoutInSeconds  the timeout set by {@link DesiredCapabilitiesConfigurationProperties}
     */
    public AutomationWait(WebDriverWrapper webDriverWrapper, Long timeoutInSeconds) {
        this.webDriverWrapper = webDriverWrapper;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    /**
     * Waits for a custom condition using {@link ExpectedConditions} from Selenium. Can also be used by passing
     * in a lambda to access the element directly.
     *
     * Examples:
     * <pre>{@code
     *      waitForCustomCondition(ExpectedConditions.urlContains(someString));
     * }
     * </pre>
     *
     * <pre>{@code
     *      waitForCustomCondition(input -> element.isDisplayed());
     * }
     * </pre>
     *
     * @param expectedCondition     the expected condition to wait for
     * @param <T>                   the type reference
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      the custom wait condition as the Type Reference T
     */
    public <T> T waitForCustomCondition(ExpectedCondition<T> expectedCondition, Long setTempTimeout) {
        T customCondition;
        var previousTimeout = getTimeoutInSeconds();

        try {
            if (setTempTimeout != null) {
                setTimeoutInSeconds(setTempTimeout);
                customCondition = createWebDriverWait().until(expectedCondition);
                setTimeoutInSeconds(previousTimeout);
            } else {
                customCondition = createWebDriverWait().until(expectedCondition);
            }
        } catch (TimeoutException e) {
            setTimeoutInSeconds(previousTimeout);
            throw e;
        }
        return customCondition;
    }

    /**
     * Waits for a custom condition using {@link ExpectedConditions} from Selenium. Can also be used by passing
     * in a lambda to access the element directly. Does not set a temp wait time.
     *
     * Examples:
     * <pre>{@code
     *      waitForCustomCondition(ExpectedConditions.urlContains(someString));
     * }
     * </pre>
     *
     * <pre>{@code
     *      waitForCustomCondition(input -> element.isDisplayed());
     * }
     * </pre>
     *
     * @param expectedCondition     the expected condition to wait for
     * @param <T>                   the type reference
     * @return                      the custom wait condition as the Type Reference T
     */
    public <T> T waitForCustomCondition(ExpectedCondition<T> expectedCondition) {
        return waitForCustomCondition(expectedCondition, null);
    }

    /**
     * Waits for an element's text box to contain a specific string.
     *
     * @param element               the {@link BaseWebElement} we are checking
     * @param text                  the text we're waiting the element to contain
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      as {@link Boolean}
     */
    public Boolean waitForTextToContain(BaseWebElement element, String text, Long setTempTimeout) {
        return waitForCustomCondition(input -> element.getText().contains(text), setTempTimeout);
    }

    /**
     * Waits for an element's text box to contain a specific string. Does not set a temp wait time.
     *
     * @param element               the {@link BaseWebElement} we are checking
     * @param text                  the text we're waiting the element to contain
     * @return                      as {@link Boolean}
     */
    public Boolean waitForTextToContain(BaseWebElement element, String text) {
        return waitForTextToContain(element, text, null);
    }

    /**
     * Waits for an element to become enabled
     *
     * @param element               the {@link BaseWebElement} we are waiting on
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      as {@link Boolean}
     */
    public Boolean waitUntilElementIsEnabled(BaseWebElement element, Long setTempTimeout) {
        return waitForCustomCondition(input -> element.isEnabled(), setTempTimeout);
    }

    /**
     * Waits for an element to become enabled. Does not set a temp wait time.
     *
     * @param element               the {@link BaseWebElement} we are waiting on
     * @return                      as {@link Boolean}
     */
    public Boolean waitUntilElementIsEnabled(BaseWebElement element) {
        return waitUntilElementIsEnabled(element, null);
    }

    /**
     * An extension to the pre canned {@link ExpectedConditions#attributeContains(WebElement, String, String)} method.
     * Waits for an element to contain a specific class string.
     *
     * @param element               the {@link BaseWebElement} we are checking
     * @param className             the string we're expecting the class name to be
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      as {@link Boolean}
     */
    public Boolean waitForElementToHaveClass(BaseWebElement element, String className, Long setTempTimeout) {
        return waitForCustomCondition(ExpectedConditions.attributeContains(
                element.getRawWebElement(), CLASS_ATTRIBUTE, className), setTempTimeout
        );
    }

    /**
     * An extension to the pre canned {@link ExpectedConditions#attributeContains(WebElement, String, String)} method.
     * Waits for an element to contain a specific class string. Does not set a temp wait time.
     *
     * @param element               the {@link BaseWebElement} we are checking
     * @param className             the string we're expecting the class name to be
     * @return                      as {@link Boolean}
     */
    public Boolean waitForElementToHaveClass(BaseWebElement element, String className) {
        return waitForElementToHaveClass(element, className, null);
    }

    /**
     * An extension to the pre canned {@link ExpectedConditions#attributeContains(WebElement, String, String)} method.
     * Waits for an element to not contain a specific class string.
     *
     * @param element               the {@link BaseWebElement} we are checking
     * @param className             the string we're expecting the class name to not be
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      as {@link Boolean}
     */
    public Boolean waitForElementToNotHaveClass(BaseWebElement element, String className, Long setTempTimeout) {
        return waitForCustomCondition(ExpectedConditions.not(
                        ExpectedConditions.attributeContains(element.getRawWebElement(), CLASS_ATTRIBUTE, className)
                ), setTempTimeout);
    }

    /**
     * An extension to the pre canned {@link ExpectedConditions#attributeContains(WebElement, String, String)} method.
     * Waits for an element to not contain a specific class string. Does not set a temp wait time.
     *
     * @param element               the {@link BaseWebElement} we are checking
     * @param className             the string we're expecting the class name to not be
     * @return                      as {@link Boolean}
     */
    public Boolean waitForElementToNotHaveClass(BaseWebElement element, String className) {
        return waitForElementToNotHaveClass(element, className, null);
    }

    /**
     * A custom wait condition to wait until the page's DOM has switched to the complete status. Useful for page
     * navigation to wait on returning a new page object until the DOM is loaded.
     *
     * This is already called in {@link BasePage#verifyIsOnPage(BaseWebElement...)} to allow users verification of the
     * web page they've navigated to.
     * It is unnecessary to call this again after a page has loaded. However, this might come in handy when
     * interactions on your web page change the state of the dom.
     *
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      as {@link Boolean}
     */
    public Boolean waitUntilPageIsLoaded(Long setTempTimeout) {
        return waitUntilPageIsLoaded(setTempTimeout, 0);
    }

    /**
     * Recursive self terminating loop to catch "target frame detached" WebDriverExceptions.
     *
     * This exception gets thrown when trying to use the javascript executor between page loads and primarily affects
     * the Chrome browser. After 5 attempts, it will rethrow the exception, in case it is an actual exception, ie.
     * trying to interact with an object in an iframe after the iframe focus has been released.
     *
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @param retryCount            counter for attempts
     * @return                      as {@link Boolean}
     */
    Boolean waitUntilPageIsLoaded(Long setTempTimeout, int retryCount) {
        var domReadyStateScript = "return document.readyState";
        try {
            return waitForCustomCondition(page -> getWebDriverWrapper()
                            .getJavascriptExecutor()
                            .executeScript(domReadyStateScript)
                            .equals("complete"),
                    setTempTimeout);
        } catch (WebDriverException e) {
            if (e.getMessage().contains("target frame detached") && retryCount < 5) {
                AutomationUtils.sleep(200);
                return waitUntilPageIsLoaded(setTempTimeout, ++retryCount);
            } else {
                throw(e);
            }
        }
    }

    /**
     * A custom wait condition to wait until the page's DOM has switched to the complete status. Useful for page
     * navigation to wait on returning a new page object until the DOM is loaded.
     *
     * This is already called in {@link BasePage#verifyIsOnPage(BaseWebElement...)} to allow users verification of the
     * web page they've navigated to. It is unnecessary to call this again after a page has loaded. However, this
     * might come in handy when interactions on your web page change the state of the dom. Does not set a temp
     * wait time.
     *
     * @return                      as {@link Boolean}
     */
    public Boolean waitUntilPageIsLoaded() {
        return waitUntilPageIsLoaded(null);
    }

    /**
     * An extension to the pre canned {@link ExpectedConditions#visibilityOfElementLocated(By)} method. Waits for an
     * element to be displayed prior to interacting with it.
     *
     * @param element               the {@link BaseWebElement} we are interacting with
     * @param setTempTimeout        an option to temporarily set the timeout to a value other than what's set
     *                              in the spring profile
     * @return                      as a {@link WebElement}
     */
    public WebElement waitUntilDisplayed(BaseWebElement element, Long setTempTimeout) {
        return getWebDriverWrapper().getAutomationWait().waitForCustomCondition(
                ExpectedConditions.visibilityOfElementLocated(element.getBy()), setTempTimeout);
    }

    /**
     * An extension to the pre canned {@link ExpectedConditions#visibilityOfElementLocated(By)} method. Waits for an
     * element to be displayed prior to interacting with it. Does not set a temp wait time.
     *
     * @param element               the {@link BaseWebElement} we are interacting with
     * @return                      as a {@link WebElement}
     */
    public WebElement waitUntilDisplayed(BaseWebElement element) {
        return waitUntilDisplayed(element, null);
    }

    /**
     * Creates a new {@link WebDriverWait} using the defined timeout in seconds
     *
     * @return the {@link WebDriverWait}
     */
    private WebDriverWait createWebDriverWait() {
        var timeoutInSeconds = getTimeoutInSeconds();
        if (!waits.containsKey(timeoutInSeconds)) {
            waits.put(timeoutInSeconds, new WebDriverWait(getWebDriverWrapper().getBaseWebDriver(), timeoutInSeconds));
        }
        return waits.get(timeoutInSeconds);
    }
}
