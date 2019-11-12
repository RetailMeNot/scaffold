package io.github.kgress.scaffold.webdriver;

import org.openqa.selenium.interactions.Actions;

/**
 * The purpose of this object is to provide a set of common functionality that can be shared across page objects in
 * an implementing project.
 *
 * A common use case for all pages is a form of verification that the page is correct when navigated to. For now, the inclusion
 * of {@link #isOnPage()} will be used. As we continue development, we can continue to add functionality here.
 *
 * Note: We should also be very protective about page objects *not* having access to the web driver. The page object, by
 * design, should be agnostic to any relationship with the web driver and only have the knowledge of our strongly typed
 * elements. With access to the web driver, it will be very easy to get carried away with back doors that could break
 * threading for testing.
 */
public abstract class BasePage {

    /**
     * A method to be overridden by the implementing project. Typically, it's best to use elements that are unique to
     * the page object that is being navigated to.
     *
     * @return the {@link Boolean} value to determine if the page is correctly loaded
     */
    public abstract boolean isOnPage();

    protected Actions getActions() {
        return getWebDriverWrapper().getActions();
    }

    private WebDriverWrapper getWebDriverWrapper() {
        return TestContext.baseContext().getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
    }
}
