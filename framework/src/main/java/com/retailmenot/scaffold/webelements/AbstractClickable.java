package com.retailmenot.scaffold.webelements;

import com.retailmenot.scaffold.webdriver.TestContext;
import com.retailmenot.scaffold.webdriver.WebDriverManager;
import com.retailmenot.scaffold.webdriver.interfaces.TestContextSetting;
import com.retailmenot.scaffold.webelements.interfaces.DisplayWaitCondition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The base {@link WebElement} wrapper around all clickable WebElements. This class provides some syntactic sugar around commonly
 * used clickable web elements (buttons, links, etc) and some added functionality we've found helpful in many scenarios
 * when dealing with most web applications.
 */
public abstract class AbstractClickable extends AbstractWebElement {

    private boolean popupsExpected = false;
    protected DisplayWaitCondition waitCondition;

    public AbstractClickable(By by) {
        super(by);
    }

    protected AbstractClickable(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    protected AbstractClickable(By by, By parentBy) {
        super(by, parentBy);
    }

    protected AbstractClickable(WebElement element) {
        super(element);
    }

    /**
     * Clicks the element gracefully.
     *
     * It first ensures that implicit scrolling is enabled prior to interacting with the element. By default, this is enabled
     * when instantiating the new {@link WebDriverManager}. It then scrolls the element into view, clicks it, and checks
     * for popups based on {@link #popupsExpected}. Afterward, it will perform any applicable wait logic after clicking.
     *
     */
    public void click() {
        // wait for the element to be visible before attempting to click if configured
        if (TestContext.baseContext().getSetting(Boolean.class, TestContextSetting.IMPLICIT_SCROLLING_ENABLED)) {
            scrollIntoView();
        }

        getWebElement().click();
        if (popupsExpected) {
            getWebDriverWrapper().synchronizeWindows();
        }
        waitAfterClick();
    }

    /**
     * Performs wait logic, if applicable.
     */
    private void waitAfterClick() {
        // If we have a custom wait condition set on this element, go ahead and execute that wait condition before continuing
        if (waitCondition != null) {
            waitCondition.waitUntilLoaded(getWebDriverWrapper());
        } else {
            // If we do NOT have a wait condition set on this element, check for a global wait condition set as a fall back.  Element level
            // wait conditions will have precedence over global wait conditions
            var globalWaitCondition = TestContext.baseContext().getSetting(DisplayWaitCondition.class, TestContextSetting.WAIT_CONDITION);
            // If the global wait condition is set, then go ahead and wait for it here
            if (globalWaitCondition != null) {
                globalWaitCondition.waitUntilLoaded(getWebDriverWrapper());
            }
        }
    }

    /**
     * Enables this element to use popup handling logic.
     */
    public void expectPopups() {
        popupsExpected = true;
    }

    /**
     * Sets a custom wait condition to be used by this element only
     *
     * @param waitCondition the type of {@link DisplayWaitCondition} to use.
     */
    public void setCustomWaitCondition(DisplayWaitCondition waitCondition) {
        this.waitCondition = waitCondition;
    }
}
