package io.github.kgress.scaffold.util;

import io.github.kgress.scaffold.exception.AutomationWaitException;
import io.github.kgress.scaffold.webdriver.BasePage;
import io.github.kgress.scaffold.webdriver.WebDriverWrapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;

/**
 * A class of Wait mechanisms useful for testing web applications. These are meant to supplement those available
 * via WebDriver's built-in wait mechanism.
 */
@Slf4j
@Getter
public class AutomationWait {

    private final static Long FIVE_SECONDS = 5L;
    private final WebDriverWrapper driver;

    // Custom timeout the developer can set if they wish to override the default timeout of 60 seconds on a
    private Long customTimeout = null;
    private boolean useCustomTimeoutIndefinitely = false;
    private Map<Long, WebDriverWait> waits = new HashMap<>();

    public AutomationWait(WebDriverWrapper webDriverWrapper) {
        this.driver = webDriverWrapper;
    }

    /**
     * Uses {@link #setCustomTimeout(Long, boolean)} to set a custom timeout with the @param useCustomTimeoutIndefinitely
     * set to false.
     *
     * @param timeoutInSeconds the time to set in seconds
     * @return as an {@link AutomationWait}
     */
    public AutomationWait setCustomTimeout(Long timeoutInSeconds) {
        setCustomTimeout(timeoutInSeconds, false);
        return this;
    }

    /**
     * Sets the custom timeout with user specified parameters of the timeout, in seconds, and indefinite timeout.
     *
     * @param timeoutInSeconds the time to set in seconds
     * @param useCustomTimeoutIndefinitely a boolean for setting indefinite timeout
     */
    public void setCustomTimeout(Long timeoutInSeconds, boolean useCustomTimeoutIndefinitely) {
        customTimeout = timeoutInSeconds;
        this.useCustomTimeoutIndefinitely = useCustomTimeoutIndefinitely;
    }

    /**
     * First get the default wait, represented by {@link WebDriverWait}, and wait until the expected condition is met before
     * proceeding.
     *
     * @param expectedCondition the expected condition to wait for
     * @param <T> the type reference
     * @return the custom wait condition as the Type Reference T
     */
    public <T> T waitForCustomCondition(ExpectedCondition<T> expectedCondition) {
        return getDefaultWait().until(expectedCondition);
    }

    /**
     * Uses {@link #waitForCustomCondition(ExpectedCondition)} to create an expected condition for text to be present
     * on the current view.
     *
     * First, ensure the driver is not null. Then, find the element on the page using {@link By}. Ensure the element
     * found contains the text expected.
     *
     * @param by the method in which the text is being located
     * @param text the text to wait for
     */
    public void waitForTextPresent(By by, String text) {
        waitForCustomCondition(driver -> {
            assert driver != null;
            return driver.findElement(by).getText().contains(text);
        });
    }

    /**
     * A custom wait condition to wait until the page's DOM has switched to the complete status. Useful for page navigation
     * to wait on returning a new page object until the DOM is loaded.
     *
     * This is already called in {@link BasePage} isOnPage to allow users verification of the web page they've navigated to.
     * It is unnecessary to call this again after a page has loaded. However, this might come in handy when interactions
     * on your web page change the state of the dom.
     *
     * @return as {@link Boolean}
     */
    public Boolean waitUntilPageIsLoaded() {
        var domReadyStateScript = "return document.readyState";
        return waitForCustomCondition(
                page -> getDriver().getJavascriptExecutor().executeScript(domReadyStateScript).equals("complete"));
    }

    /**
     * Returns the set timeout in seconds.
     *
     * @return the timeout as a {@link Long} in seconds.
     */
    private Long getTimeoutInSeconds() {
        Long returnTimeout;
        // If useCustomTimeoutIndefinitely is set to true, use our custom timeout value.
        if (useCustomTimeoutIndefinitely) {
            // Throw an exception if this is null as this should be not be configured to use this way
            if (null == customTimeout) {
                throw new AutomationWaitException("Custom timeout was null when set to use custom timeouts");
            }
            returnTimeout = customTimeout;
        } else {
            // If we have a custom timeout and useCustomTimeoutIndefinitely is set to false use it
            if (null != customTimeout) {
                returnTimeout = customTimeout;
                // Remember to set it to null so we don't attempt to use it again.
                customTimeout = null;
            } else {
                // If we get here, then use our default timeout
                returnTimeout = FIVE_SECONDS;
            }
        }
        return returnTimeout;
    }

    /**
     * Returns a new WebDriver wait with the default timeout.
     *
     * @return the {@link WebDriverWait}
     */
    private WebDriverWait getDefaultWait() {
        var timeOutToUse = getTimeoutInSeconds();
        if (!waits.containsKey(timeOutToUse)) {
            waits.put(timeOutToUse, new WebDriverWait(driver.getBaseWebDriver(), timeOutToUse));
        }
        return waits.get(timeOutToUse);
    }
}
