package io.github.kgress.scaffold.webdriver;

import io.github.kgress.scaffold.util.AutomationWait;
import io.github.kgress.scaffold.webelements.AbstractWebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The purpose of this object is to provide a set of common functionality that can be shared across page objects in
 * an implementing project.
 *
 * A common use case for all pages is a form of verification that the page is correct when navigated to. For now, the inclusion
 * of {@link #verifyIsOnPage(AbstractWebElement...)} will be used. As we continue development, we can continue to add functionality here.
 *
 * Note: We should also be very protective about page objects *not* having access to the web driver. The page object, by
 * design, should be agnostic to any relationship with the web driver and only have the knowledge of our strongly typed
 * elements. With access to the web driver, it will be very easy to get carried away with back doors that could break
 * threading for testing.
 */
public abstract class BasePage {

    /**
     * {@link Deprecated} in favor of {@link #verifyIsOnPage(AbstractWebElement...)}. This method will be phased out in the next
     * breaking release.
     *
     * A method to be overridden by the implementing project. Typically, it's best to use elements that are unique to
     * the page object that is being navigated to.
     *
     * @return the {@link Boolean} value to determine if the page is correctly loaded
     */
    @Deprecated
    public abstract boolean isOnPage();

    /**
     * A method that is used to verify a number of elements are displayed on the page when navigating to it. In addition,
     * it will check to ensure the DOM is in a ready and loaded state prior to proceeding. This will help alleviate
     * issues with websites where it takes additional time to load the page due to extraneous db calls or slow loading JS.
     *
     * It's best to use this method in the constructor of the page object as it will be invoked at the time the page is
     * instantiated. For example:
     *
     * <pre>{@code
     * @Getter
     * public class LoginPage() {
     *     private InputWebElement emailInput = new InputWebElement("#email");
     *     private InputWebElement passwordInput = new InputWebElement("#password");
     *
     *     public LoginPage() {
     *         isOnPage(getEmailInput, getPasswordInput);
     *     }
     * }
     * }</pre>
     *
     * The elements passed in to this method will be checked by selenium to see if they are displayed. Make sure to pass in
     * elements that are unique to the page that won't show up on other pages. For example, a login page will have an email
     * and password input and would pass in those elements as parameters. Don't use elements from headers or a logo that
     * might appear on all of the pages.
     *
     * @param element the element(s) that will be checked if displayed
     * @return the {@link Boolean} value to determine if the page is correctly loaded
     */
    public Boolean verifyIsOnPage(AbstractWebElement... element) {
        // Get the list of elements and iterate over them so we can remove any duplicates
        var listOfElements = Arrays.stream(element)
                .distinct() //removes duplicates
                .collect(Collectors.toList());

        // Check to make sure the list isn't empty, just in case. We need to at least have one element so we can verify
        // it's displayed
        if (listOfElements.isEmpty()) {
            throw new RuntimeException(String.format("No elements to search for when verifying %s. " +
                    "Please provide at least one element to check it is displayed.", getClass().getSimpleName()));
        }

        // The wait condition should wait for the page to load into the complete state. When complete, check to make
        // sure the elements are displayed. However, if it doesn't (for example, if there's a time out), throw an exception.
        var isPageLoaded = getAutomationWait().waitUntilPageIsLoaded();
        if (isPageLoaded) {
            listOfElements.forEach(elementOnPage -> {
                var isDisplayed = elementOnPage.isDisplayed();
                if (!isDisplayed) {
                    throw new NoSuchElementException(String.format("Page verification failed. Could not find the element %s for " +
                            "the intended page: %s", elementOnPage.toString(), getClass().getSimpleName()));
                }
            });
        }
        return true;
    }

    /**
     * Gets the Selenium based {@link Actions} object for the current thread. This is currently not strongly typed and
     * should be added in a future update.
     *
     * TODO add a strongly typed {@link Actions} object
     *
     * @return {@link Actions}
     */
    protected Actions getActions() {
        return getWebDriverWrapper().getActions();
    }

    /**
     * Gets the selenium based {@link JavascriptExecutor} for the current thread.
     *
     * @return {@link JavascriptExecutor}
     */
    protected JavascriptExecutor getJavascriptExecutor() {
        return getWebDriverWrapper().getJavascriptExecutor();
    }

    /**
     * Gets the {@link AutomationWait} from the current thread's {@link WebDriverWrapper}
     * @return as {@link AutomationWait}
     */
    protected AutomationWait getAutomationWait() {
        return getWebDriverWrapper().getAutomationWait();
    }

    /**
     * Gets the {@link WebDriverWrapper} for the current thread.
     *
     * @return {@link WebDriverWrapper}
     */
    private WebDriverWrapper getWebDriverWrapper() {
        return TestContext.baseContext().getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
    }
}
