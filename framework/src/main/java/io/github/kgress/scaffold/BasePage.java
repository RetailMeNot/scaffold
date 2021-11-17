package io.github.kgress.scaffold;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The purpose of this object is to provide a set of common functionality that can be shared across page objects in
 * an implementing project.
 *
 * A common use case for all pages is a form of verification that the page is correct when navigated to. For now,
 * the inclusion of {@link #verifyIsOnPage(BaseWebElement...)} will be used. As we continue development, we can
 * continue to add functionality here.
 *
 * Note: We should also be very protective about page objects *not* having access to the web driver. The page object,
 * by design, should be agnostic to any relationship with the web driver and only have the knowledge of our strongly
 * typed elements. With access to the web driver, it will be very easy to get carried away with back doors that could
 * break threading for testing.
 */
public class BasePage {

    /**
     * A method that is used to verify a number of elements are displayed on the page when navigating to it. In
     * addition, it will check to ensure the DOM is in a ready and loaded state prior to proceeding. This will help
     * alleviate issues with websites where it takes additional time to load the page due to extraneous db calls or
     * slow loading JS.
     *
     * It's best to use this method in the constructor of the page object as it will be invoked at the time the page is
     * instantiated. For example:
     *
     * <pre>{@code
     *  &#64;Getter
     *  public class LoginPage() {
     *         private InputWebElement emailInput = new InputWebElement("#email");
     *      private InputWebElement passwordInput = new InputWebElement("#password");
     *
     *      public LoginPage() {
     *             verifyIsOnPage(getEmailInput, getPasswordInput);
     *      }
     *  }
     * }
     * </pre>
     *
     * The elements passed in to this method will be checked by selenium to see if they are displayed. Make sure to
     * pass in elements that are unique to the page that won't show up on other pages. For example, a login page will
     * have an email and password input and would pass in those elements as parameters. Don't use elements from headers
     * or a logo that might appear across every page on your website.
     *
     * @param element the element(s) that will be checked if displayed
     * @return the {@link Boolean} value to determine if the page is correctly loaded
     */
    protected Boolean verifyIsOnPage(BaseWebElement... element) {
        // Remove any duplicates from the list
        var listOfElements = Arrays.stream(element)
                .distinct() //removes duplicates
                .collect(Collectors.toList());

        // Make sure the list of elements isn't empty
        if (listOfElements.isEmpty()) {
            throw new RuntimeException(String.format("No elements to search for when verifying %s. " +
                    "Please provide at least one element to verify the page.", getClass().getSimpleName()));
        }

        // Wait until the page is loaded then look for the elements
        var isPageLoaded = getAutomationWait().waitUntilPageIsLoaded();
        if (isPageLoaded) {
            listOfElements.forEach(elementOnPage -> {
                try {
                    elementOnPage.isDisplayed();
                } catch (TimeoutException e) {
                    throw new TimeoutException(String.format("Page verification failed. Could not find the element " +
                            "%s for the intended page: %s", elementOnPage, getClass().getSimpleName()));
                }
            });
        } else {
            throw new TimeoutException(String
                    .format("The intended page failed to load %s", getClass().getSimpleName())
            );
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
