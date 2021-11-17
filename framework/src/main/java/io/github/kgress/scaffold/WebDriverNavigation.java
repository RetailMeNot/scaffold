package io.github.kgress.scaffold;

/**
 * This class is for handling threading during navigation for an implementing project.
 *
 * Implementing projects should include an "AutomationNavigation" file that extends off of this. Doing so will allow
 * you to encapsulate navigation actions outside of Page Objects and test files.
 *
 * // TODO We should move the {@link WebDriverWrapper}'s navigate method to here in the future for further encapsulation.
 */
public abstract class WebDriverNavigation {

    /**
     * Gets the {@link WebDriverWrapper} from the current thread.
     *
     * @return the {@link WebDriverWrapper} from the current thread
     */
    protected WebDriverWrapper getWebDriverWrapper() {
        return getContext().getWebDriverManager().getWebDriverWrapper();
    }

    /**
     * Grabs the {@link WebDriverContext} for the current thread. This allows us to get the {@link WebDriverWrapper} for that specific thread.
     *
     * @return the {@link TestContext}
     */
    private WebDriverContext getContext() {
        return TestContext.baseContext().getWebDriverContext();
    }
}
