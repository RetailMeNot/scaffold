package io.github.kgress.scaffold.webdriver;

import io.github.kgress.scaffold.webelements.DivWebElement;

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
     * A method to be overridden by the implementing project. Typically, this will look something like:
     *
     * <pre>{@code
     *     @Override
     *     public boolean isOnPage() {
     *         return getHeader.isDisplayed();
     *     }
     *}</pre>
     *
     * In this above example, we assume there is already a new {@link DivWebElement} created as a property named "header."
     * The isOnPage() method will then get that header and ensure that it is displayed. Typically, it's best to ues elements
     * that are unique to the page object that is being navigated to.
     *
     * @return the {@link Boolean} value to determine if the page is correctly loaded
     */
    protected abstract boolean isOnPage();
}
