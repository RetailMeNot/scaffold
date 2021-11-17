package io.github.kgress.scaffold;

import io.github.kgress.scaffold.environment.config.DesiredCapabilitiesConfigurationProperties;
import io.github.kgress.scaffold.exception.WebDriverWrapperException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.github.kgress.scaffold.util.AutomationUtils.sleep;

/**
 * This serves as a buffer between us and Selenium to help guard against drastic changes to their API and functionality
 * in the {@link WebDriver}. It mostly delegates to the underlying WebDriver, but in many cases we'll code up special
 * behavior for it.
 */
@Slf4j
public class WebDriverWrapper {

    private static final Long WINDOW_TIME_OUT_IN_SECONDS = 60L;

    @Getter
    final WebDriver baseWebDriver;

    @Getter
    private final AutomationWait automationWait;

    @Getter
    @Setter
    private LinkedList<String> registeredWindows = new LinkedList<>();

    /**
     * Takes a raw {@link WebDriver} instance and wraps it up in a wonderful blanket for thread-safe handling.
     * @param baseWebDriver         the root {@link WebDriver}
     * @param waitTimeoutInSeconds  the timeout we set for the {@link AutomationWait} from
     *                              {@link DesiredCapabilitiesConfigurationProperties}
     */
    WebDriverWrapper(WebDriver baseWebDriver, Long waitTimeoutInSeconds) {
        this.baseWebDriver = baseWebDriver;
        this.automationWait = new AutomationWait(this, waitTimeoutInSeconds);
    }

    /**
     * Finds a raw {@link WebElement} on the page using a {@link By} locator
     *
     * @param by    the means in which the element is being found using {@link By}
     * @return      the element as a {@link WebElement}
     */
    public WebElement findElement(By by) {
        return getBaseWebDriver().findElement(by);
    }

    /**
     * Find all raw {@link WebElement} on the page using a {@link By} locator
     *
     * @param by    the means in which the element is being found using {@link By}
     * @return      the list of elements as a {@link List} of {@link WebElement}
     */
    public List<WebElement> findElements(By by) {
        return getBaseWebDriver().findElements(by);
    }

    /**
     * Navigate to the URL provided in the parameter
     *
     * @param url the URL to navigate to
     */
    public void get(String url) {
        getBaseWebDriver().get(url);
    }

    /**
     * Returns the JavascriptExecutor for the current WebDriver instance
     *
     * @return the {@link JavascriptExecutor}
     */
    public JavascriptExecutor getJavascriptExecutor() {
        if (!(getBaseWebDriver() instanceof JavascriptExecutor)) {
            throw new WebDriverWrapperException("Current WebDriver instance does not support JavascriptExecutor");
        }
        return (JavascriptExecutor) this.baseWebDriver;
    }

    /**
     * Returns a new Actions object
     *
     * @return the actions as {@link Actions}
     */
    public Actions getActions() {
        return new Actions(getBaseWebDriver());
    }

    /**
     * Adds the specified cookie
     *
     * @param newCookie Cookie to add
     */
    public void addCookie(Cookie newCookie) {
        manage().addCookie(newCookie);
    }

    /**
     * Returns the Cookie given by the cookieName parameter. If the cookie does not exist, an Exception will be thrown.
     * To get the value of the cookie, invoke the getValue method on the Cookie object.
     *
     * @param cookieName the name of the cookie
     * @return as a {@link Cookie}
     * @throws WebDriverWrapperException the exception to throw if the cookie cannot be found
     */
    public Cookie getCookie(String cookieName)
            throws WebDriverWrapperException {
        var cookie = manage().getCookieNamed(cookieName);
        if (cookie == null) {
            var error = "Could not locate the following cookie: %s";
            error = String.format(error, cookieName);
            throw new WebDriverWrapperException(error);
        }
        return cookie;
    }

    /**
     * Deletes the specified cookie. return True if the cookie was deleted successfully, false otherwise
     *
     * @param oldCookie Cookie to delete
     */
    public void deleteCookie(Cookie oldCookie) {
        manage().deleteCookie(oldCookie);
    }

    /**
     * Navigate to a specified URL
     *
     * @return as a {@link Navigation}
     */
    public Navigation navigate() {
        return getBaseWebDriver().navigate();
    }

    /**
     * Returns the page source for the current page
     *
     * @return the page source as a {@link String}
     */
    public String getPageSource() {
        return getBaseWebDriver().getPageSource();
    }

    /**
     * Returns the title of the page, as detailed in the DOM.
     *
     * @return the title of the page as a {@link String}
     * @see WebDriver#getTitle()
     */
    public String getTitle() {
        return getBaseWebDriver().getTitle();
    }

    /**
     * Return the Current URL the browser is looking at
     *
     * @return the current URL as a {@link String}
     */
    public String getCurrentUrl() {
        return getBaseWebDriver().getCurrentUrl();
    }

    /**
     * Opens a new window with the given URL, then switches context back to it
     *
     * @param url the url desired to be navigated to in the new window
     */
    public void openUrlInNewWindow(String url) {
        //First, use javascript to open a new window
        getJavascriptExecutor().executeScript("window.open()");
        // Secondly, synchronize the windows to account for the popup
        synchronizeWindows();
        // Finally, open the url
        get(url);
    }

    /**
     * Forces WebDriver to switch to a different window or frame
     *
     * @return as a {@link TargetLocator}
     */
    public TargetLocator switchTo() {
        return getBaseWebDriver().switchTo();
    }

    /**
     * Switches to the specified window by index (e.g. 0 switches to the base (bottom) window)
     *
     * @param index the index to switch to
     */
    public void switchToWindow(int index) {
        var window = getRegisteredWindows().get(index);
        switchToWindow(window);
    }

    /**
     * Switches to the specified window
     *
     * @param windowHandle the window id
     */
    public void switchToWindow(String windowHandle) {
        getBaseWebDriver().switchTo().window(windowHandle);
    }

    /**
     * Switches to the second window that is within the current window handles
     */
    public void switchToSecondWindow() {
        final var windowsHandles = getWindowHandles();
        final var windowHandle = getWindowHandle();

        // Switch to the window handle that is not currently focused
        for (var handle : windowsHandles) {
            if (Objects.equals(handle, windowHandle)) {
                continue;
            }
            switchToWindow(handle);
        }
    }

    /**
     * Returns a set of window handles which can be used to iterate over all open windows of this WebDriver instance
     *
     * @return the window handles as a {@link Set} of {@link String}
     */
    public Set<String> getWindowHandles() {
        return getBaseWebDriver().getWindowHandles();
    }

    /**
     * Synchronizes the registered windows with the currently open windows.  Handles closed and new windows (popups).
     */
    public void synchronizeWindows() {
        // This custom timeout will only last for the duration of the window handling
        getAutomationWait().setTimeoutInSeconds(WINDOW_TIME_OUT_IN_SECONDS);
        getAutomationWait().waitForCustomCondition(createWindowExpectedCondition(), null);
        // Once we know we've found a window change, lets settle down for a few seconds and
        // freshly retrieve our window handles before attempting any logic
        sleep(200);
        var windows = getBaseWebDriver().getWindowHandles();
        // Clear out our registered window handles so we can resynchronize them
        getRegisteredWindows().clear();
        for (var window : windows) {
            getRegisteredWindows().addLast(window);
        }
        // Now make sure and switch to the last window to be opened
        this.switchToWindow(getRegisteredWindows().getLast());
    }

    /**
     * Takes a screen shot of the current browser state (includes the ENTIRE browser image, including what
     * is scrolled off-screen)
     *
     * @return A base64-encoded String representation of the screen shot
     */
    public String getScreenShot() {
        if (!TakesScreenshot.class.isAssignableFrom(getBaseWebDriver().getClass())) {
            throw new WebDriverWrapperException("Driver does not support taking screenshots: " + getBaseWebDriver());
        }
        return ((TakesScreenshot) getBaseWebDriver()).getScreenshotAs(OutputType.BASE64);
    }

    /**
     * Takes a screen shot of the current browser state and returns it as a File object
     *
     * @return a File object representation of the screen shot
     */
    public File getScreenShotAsFile() {
        if (!TakesScreenshot.class.isAssignableFrom(getBaseWebDriver().getClass())) {
            throw new WebDriverWrapperException("Driver does not support taking screenshots: " + getBaseWebDriver());
        }
        return ((TakesScreenshot) getBaseWebDriver()).getScreenshotAs(OutputType.FILE);
    }

    /**
     * Returns the window handle of the current window
     *
     * @return the window handle as a {@link String}
     */
    public String getWindowHandle() {
        return getBaseWebDriver().getWindowHandle();
    }

    /**
     * Returns the interface used to manage WebDriver properties
     *
     * @return the manager as {@link Options}
     */
    public Options manage() {
        return getBaseWebDriver().manage();
    }

    /**
     * Quits the current WebDriver instance, closing all open windows
     */
    public void quit() {
        try {
            getBaseWebDriver().quit();
        } catch (Exception e) {
            log.error("Error quitting driver: " + e);
        }
    }

    /**
     * Closes the current window, quitting the current WebDriver instance if it is the only window opened
     */
    public void close() {
        var synchronizeWindows = false;
        // We have to handle switching back to the prior window if dealing with multiple windows
        if (this.getWindowHandles().size() > 1) {
            synchronizeWindows = true;
        }
        getBaseWebDriver().close();
        // After closing the window, we can synchronize our windows
        if (synchronizeWindows) {
            this.synchronizeWindows();
        }
    }

    /**
     * A custom expected condition for {@link #synchronizeWindows()}
     *
     * @return as {@link ExpectedCondition}
     */
    private ExpectedCondition<Boolean> createWindowExpectedCondition() {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver input) {
                var innerWindows = getBaseWebDriver().getWindowHandles();
                sleep(200);
                // Wait until we know that we have retrieved windows and their size is different than our registered windows
                // which equates to a window change of some sort
                return (innerWindows != null && innerWindows.size() != getRegisteredWindows().size());
            }

            @Override
            public String toString() {
                // Failure message if no window changes are detected
                return String.format("window change to happen. %d registered windows present", getRegisteredWindows().size());
            }
        };
    }
}
