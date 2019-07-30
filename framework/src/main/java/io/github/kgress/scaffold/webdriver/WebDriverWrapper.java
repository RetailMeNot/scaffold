package io.github.kgress.scaffold.webdriver;

import io.github.kgress.scaffold.exception.WebDriverWrapperException;
import io.github.kgress.scaffold.util.AutomationUtils;
import io.github.kgress.scaffold.util.AutomationWait;
import io.github.kgress.scaffold.webelements.AbstractWebElement;
import io.github.kgress.scaffold.webelements.interfaces.BaseWebElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    private AutomationWait automationWait;
    private WebDriver driver;
    private long seleniumObjectTimeout = 15;
    private LinkedList<String> registeredWindows = new LinkedList<>();
    private boolean implicitWaitsEnabled = true; // Flag here for us to determine if implicit waiting is enabled or disabled

    /**
     * Takes a "real" WebDriver instance and wraps it up in the facade for safe (and thread-safe) handling.
     * @param webDriver the root {@link WebDriver}
     */
    WebDriverWrapper(WebDriver webDriver) {
        this.driver = webDriver;
        this.automationWait = new AutomationWait(this);
    }

    /**
     * Find an element on the page
     *
     * @param by the means in which the element is being found using {@link By}
     * @return the element as a {@link WebElement}
     */
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    /**
     * Attempts to locate and return a {@code WebElement} for the specified {@code By} locator off of the parent element passed in
     *
     * @param element                  The element being located
     * @param by                       Locator to search by
     * @param throwExceptionIfNotFound If true and the element cannot be found, a {@code NoSuchElementException} will be thrown
     *                                 if the element cannot be located.
     * @return the element as a {@link WebElement}
     */
    public WebElement findElement(WebElement element, By by, boolean throwExceptionIfNotFound) {
        WebElement returnElement = null;
        try {
            returnElement = element.findElement(by);
        }
        // Only need to suppress no such element exceptions -- any other exceptions we run into
        // should be treated as unhandled
        catch (NoSuchElementException e) {
            // Only throw an exception if the caller specifies
            if (throwExceptionIfNotFound) {
                throw e;
            }
        }
        return returnElement;
    }

    /**
     * Attempts to locate and return a {@code List<WebElement>} for the specified {@code By} locator off of the parent element passed in
     *
     * @param element                  The elements being located
     * @param by                       Locator to search by
     * @param throwExceptionIfNotFound If true and the element(s) cannot be found, a {@code NoSuchElementException} will be thrown
     *                                 if the element cannot be located.
     * @return the elements as a {@link List} of {@link WebElement}
     */
    public List<WebElement> findElements(WebElement element, By by, boolean throwExceptionIfNotFound) {
        List<WebElement> elements = new ArrayList<>();
        try {
            elements = element.findElements(by);
        }
        // Only need to suppress no such element exceptions -- any other exceptions we run into
        // should be treated as unhandled
        catch (NoSuchElementException e) {
            // Only throw an exception if the caller specifies
            if (throwExceptionIfNotFound) {
                throw e;
            }
        }
        return elements;
    }

    /**
     * Returns a list of all the WebElements on the page that match the By locator
     *
     * @param by the means in which the element is being found using {@link By}
     * @return the list of elements as a {@link List} of {@link WebElement}
     */
    public List<WebElement> findElements(By by) {
        return this.driver.findElements(by);
    }

    /**
     * Returns a list of strongly typed WebElements
     *
     * @param elementClass the class of the element that is being found
     * @param by           the method in which the element is being found using {@link By}
     * @param <T>          the reference to the {@link AbstractWebElement}
     * @return the elements as a {@link List} of {@link T}
     */
    public <T extends AbstractWebElement> List<T> findElements(Class<T> elementClass, By by) {
        return this.findElements(elementClass, by, true);
    }

    /**
     * Returns a list of strongly typed WebElements
     *
     * @param elementClass The class of the element that is being found
     * @param by           the means in which the element is being found using {@link By}
     * @param elementInterface the interface of the element
     * @param <T>          the reference to the {@link AbstractWebElement}
     * @param <I>          the reference to the {@link AbstractWebElement}
     * @return the elements as a {@link List} of {@link WebElement}
     */
    public <T extends AbstractWebElement, I extends BaseWebElement> List<I> findElements(Class<T> elementClass, Class<I> elementInterface, By by) {
        if (!elementInterface.isAssignableFrom(elementClass)) {
            throw new ClassCastException(String.format("Class %s does not implement interface %s!", elementClass.getSimpleName(), elementInterface.getSimpleName()));
        }

        List<T> classElements = findElements(elementClass, by);
        List<I> interfaceElements = new ArrayList<I>();
        for (T classElement : classElements) {
            interfaceElements.add((I) classElement);
        }
        return interfaceElements;
    }

    /**
     * Returns a list of strongly typed WebElements
     *
     * @param elementClass             the class of the element that is being found
     * @param by                       the method in which the element is being found using {@link By}
     * @param throwExceptionIfNotFound a {@link Boolean} value of true or false if this error is expected
     * @param <T>                      the reference to the {@link AbstractWebElement}
     * @return the elements as a {@link List} of {@link T}
     */
    public <T extends AbstractWebElement> List<T> findElements(Class<T> elementClass, By by, boolean throwExceptionIfNotFound) {
        List<WebElement> elements = new ArrayList<>();
        try {
            elements = driver.findElements(by);
        }
        // Only need to suppress no such element exceptions -- any other exceptions we run into
        // should be treated as unhandled
        catch (NoSuchElementException e) {
            // Only throw an exception if the caller specifies
            if (throwExceptionIfNotFound) {
                throw e;
            }
        }
        List<T> returnElements = new ArrayList<>(elements.size());
        // elements should be an empty list if no WebElements were found, and the for/each will never execute, returning an empty list to the caller
        for (WebElement element : elements) {
            try {
                Constructor<T> constructor = elementClass.getConstructor(WebElement.class);
                T newElement = constructor.newInstance((WebElement) element);
                returnElements.add(newElement);
            } catch (Throwable t) {
                log.error("Error trying to construct webelement: " + AutomationUtils.getStackTrace(t));
            }
        }
        return returnElements;
    }

    /**
     * Navigate to the URL provided in the parameter
     *
     * @param url the URL to navigate to
     */
    public void get(String url) {
        this.driver.get(url);
    }

    /**
     * Returns the JavascriptExecutor for the current WebDriver instance
     *
     * @return the {@link JavascriptExecutor}
     */
    public JavascriptExecutor getJavascriptExecutor() {
        if (!(this.driver instanceof JavascriptExecutor)) {
            throw new WebDriverWrapperException("Current WebDriver instance does not support JavascriptExecutor");
        }
        return (JavascriptExecutor) this.driver;
    }

    /**
     * Returns a new Actions object
     *
     * @return the actions as {@link Actions}
     */
    public Actions getActions() {
        return new Actions(this.driver);
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
        return this.driver.navigate();
    }

    /**
     * Returns the page source for the current page
     *
     * @return the page source as a {@link String}
     */
    public String getPageSource() {
        return this.driver.getPageSource();
    }

    /**
     * Returns the title of the page, as detailed in the DOM.
     *
     * @return the title of the page as a {@link String}
     * @see WebDriver#getTitle()
     */
    public String getTitle() {
        return this.driver.getTitle();
    }

    /**
     * Return the Current URL the browser is looking at
     *
     * @return the current URL as a {@link String}
     */
    public String getCurrentUrl() {
        return this.driver.getCurrentUrl();
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
        this.synchronizeWindows();
        // Finally, open the url
        this.driver.get(url);
    }

    /**
     * Forces WebDriver to switch to a different window or frame
     *
     * @return as a {@link TargetLocator}
     */
    public TargetLocator switchTo() {
        return this.driver.switchTo();
    }

    /**
     * Switches to the specified window by index (e.g. 0 switches to the base (bottom) window)
     *
     * @param index the index to switch to
     */
    public void switchToWindow(int index) {
        var window = registeredWindows.get(index);
        this.switchToWindow(window);
    }

    /**
     * Switches to the specified window
     *
     * @param windowHandle the window id
     */
    public void switchToWindow(String windowHandle) {
        this.driver.switchTo().window(windowHandle);
    }

    /**
     * Switches to the second window that is within the current window handles
     */
    public void switchToSecondWindow() {
        final var windowsHandles = driver.getWindowHandles();
        final var windowHandle = driver.getWindowHandle();

        // Switch to the window handle that is not currently focused
        for (var handle : windowsHandles) {
            if (handle.equals(windowHandle)) {
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
        return this.driver.getWindowHandles();
    }

    /**
     * Synchronizes the registered windows with the currently open windows.  Handles closed and new windows (popups).
     */
    public void synchronizeWindows() {
        // This custom timeout will only last for the duration of the window handling
        var wait = getAutomationWait().setCustomTimeout(WINDOW_TIME_OUT_IN_SECONDS);
        wait.waitForCustomCondition(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                var innerWindows = driver.getWindowHandles();
                sleep(200);
                // Wait until we know that we have retrieved windows and their size is different than our registered windows
                // which equates to a window change of some sort
                return (innerWindows != null && innerWindows.size() != registeredWindows.size());
            }

            @Override
            public String toString() {
                // Failure message if no window changes are detected
                return String.format("window change to happen. %d registered windows present", registeredWindows.size());
            }
        });
        // Once we know we've found a window change, lets settle down for a few seconds and
        // freshly retrieve our window handles before attempting any logic
        sleep(200);
        var windows = driver.getWindowHandles();
        // Clear out our registered window handles so we can resynchronize them
        registeredWindows.clear();
        for (var window : windows) {
            registeredWindows.addLast(window);
        }
        // Now make sure and switch to the last window to be opened
        this.switchToWindow(registeredWindows.getLast());
    }

    /**
     * Returns the Selenium timeout for this WebDriver instance
     *
     * @return the {@link #seleniumObjectTimeout}
     */
    public long getSeleniumObjectTimeout() {
        return seleniumObjectTimeout;
    }

    /**
     * Sets the Selenium timeout for this WebDriver instance
     *
     * @param seleniumObjectTimeout the amount of time to set the selenium object timeout
     */
    public void setSeleniumObjectTimeout(long seleniumObjectTimeout) {
        this.seleniumObjectTimeout = seleniumObjectTimeout;
    }

    /**
     * Takes a screen shot of the current browser state (includes the ENTIRE browser image, including what
     * is scrolled off-screen)
     *
     * @return A base64-encoded String representation of the screen shot
     */
    public String getScreenShot() {
        if (!TakesScreenshot.class.isAssignableFrom(driver.getClass())) {
            throw new WebDriverWrapperException("Driver does not support taking screenshots: " + driver);
        }
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    /**
     * Takes a screen shot of the current browser state and returns it as a File object
     *
     * @return a File object representation of the screen shot
     */
    public File getScreenShotAsFile() {
        if (!TakesScreenshot.class.isAssignableFrom(driver.getClass())) {
            throw new WebDriverWrapperException("Driver does not support taking screenshots: " + driver);
        }
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    }

    /**
     * Returns the window handle of the current window
     *
     * @return the window handle as a {@link String}
     */
    public String getWindowHandle() {
        return this.driver.getWindowHandle();
    }

    /**
     * Returns the registered windows
     *
     * @return the {@link #registeredWindows}
     */
    public LinkedList<String> getRegisteredWindows() {
        return registeredWindows;
    }

    /**
     * Sets the registered windows
     *
     * @param registeredWindows the list of required windows
     */
    public void setRegisteredWindows(LinkedList<String> registeredWindows) {
        this.registeredWindows = registeredWindows;
    }

    /**
     * Gets the scaffold wait time
     *
     * @return as {@link AutomationWait}
     */
    public AutomationWait getAutomationWait() {
        return automationWait;
    }

    /**
     * Sets the scaffold wait time
     *
     * @param automationWait the {@link AutomationWait} required
     */
    public void setAutomationWait(AutomationWait automationWait) {
        this.automationWait = automationWait;
    }

    /**
     * Checks to see if implicit waits are enabled
     *
     * @return a true or false on state
     */
    public boolean isImplicitWaitsEnabled() {
        return implicitWaitsEnabled;
    }

    /**
     * Sets the implicit wait to enabled or disabled
     *
     * @param implicitWaitsEnabled the state in which the user wishes the implicit wait state
     */
    public void setImplicitWaitsEnabled(boolean implicitWaitsEnabled) {
        this.implicitWaitsEnabled = implicitWaitsEnabled;
    }

    /**
     * Returns the underlying WebDriver element.
     *
     * @return as a {@link WebDriver}
     */
    public WebDriver getBaseWebDriver() {
        return this.driver;
    }

    /**
     * Sets the underlying WebDriver instance for this object
     *
     * @param webDriver the web driver instance to set
     */
    public void setWebDriver(WebDriver webDriver) {
        this.driver = webDriver;
    }

    /**
     * Returns the interface used to manage WebDriver properties
     *
     * @return the manager as {@link Options}
     */
    public Options manage() {
        return this.driver.manage();
    }

    /**
     * Quits the current WebDriver instance, closing all open windows
     */
    public void quit() {
        try {
            this.driver.quit();
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
        this.driver.close();
        // After closing the window, we can synchronize our windows
        if (synchronizeWindows) {
            this.synchronizeWindows();
        }
    }
}
