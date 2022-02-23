package io.github.kgress.scaffold;

import io.github.kgress.scaffold.exception.WebDriverContextException;
import io.github.kgress.scaffold.models.TestInformation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * A list of common methods available to the {@link TestContext} singleton - Primarily used for obtaining a WebDriverManager
 * for a specific test.
 */
@Slf4j
public class BaseTestContext {

    // Associates the WebDriver instance to the current thread its operating in
    private static final ThreadLocal<WebDriverContext> DRIVER_MANAGER = new ThreadLocal<>();
    private final Map<String, Object> settings = new ConcurrentHashMap<>();
    private final Map<String, TestInformation> testInformationManager = new ConcurrentHashMap<>();

    BaseTestContext() {
        addSetting(TestContextSetting.IMPLICIT_SCROLLING_ENABLED, false);
    }

    private TestInformation getTestInformation(String testName) {
        TestInformation localTestInformation;
        synchronized (testInformationManager) {
            if (!testInformationManager.containsKey(testName)) {
                testInformationManager.put(testName, new TestInformation());
            }
            localTestInformation = testInformationManager.get(testName);
        }
        return localTestInformation;
    }

    /**
     * Adds the specified setting to the key.
     *
     * @param key the key to add
     * @param value the value to add
     */
    public void addSetting(String key, Object value) {
        settings.put(key, value);
    }

    /**
     * Returns the setting for the specified key.
     *
     * @param key the key to return
     * @param clazz the Class of the Type Reference that the setting is being pulled from
     * @param <S> the type reference
     * @return the setting as provided by the Type Reference
     */
    public <S> S getSetting(Class<S> clazz, String key) {
        var obj = settings.get(key);
        if (obj == null) {
            return null;
        }
        if (clazz.isAssignableFrom(obj.getClass())) {
            return clazz.cast(obj);
        } else {
            throw new RuntimeException(String.format("Return type did not match.  Expected: %s Actual: %s", clazz, obj.getClass()));
        }
    }

    /**
     * Adds the exception for the specified test name.
     *
     * @param testName the test name to add the exception for.
     * @param t the exception as {@link Throwable}
     */
    public void addExceptionForTest(String testName, Throwable t) {
        var previousThrowable = getTestInformation(testName).exception(t).getException();
        if (previousThrowable != null) {
            log.error(String.format("Previous exception for test %s was found: %s", testName, previousThrowable));
        }
        log.debug(String.format("Exception %s added for test %s", t, testName));
    }

    /**
     * Returns the associated exception for the test.
     *
     * @param testName the test name to get the exception from.
     * @return the exception as {@link Throwable}.
     */
    public Throwable getExceptionForTest(String testName) {
        var returnException = getTestInformation(testName).getException();
        log.debug(String.format("Exception %s retrieved for test %s", returnException, testName));
        return returnException;
    }

    /**
     * Gets a web driver webdrivercontext from a pair.
     */
    private WebDriverContext getContext() {
        var webDriverContext = DRIVER_MANAGER.get();

        if (webDriverContext == null || webDriverContext.getWebDriverManager() == null) {
            webDriverContext = new WebDriverContext(null, null);
            DRIVER_MANAGER.set(webDriverContext);
        }
        return webDriverContext;
    }

    /**
     * Adds the specified driver to the webdrivercontext.
     *
     * @param webDriverManager the {@link WebDriverManager} that is being set
     * @param testName the test name that is being set
     */
    public void setContext(WebDriverManager webDriverManager, String testName) {
        // We want to make sure the caller is removing this before trying to add a new one so that we do not have leftover windows
        // or improperly managed resources
        var webDriverContext = getContext();
        if (webDriverContext.getWebDriverManager() != null) {
            throw new WebDriverContextException(
                    String.format("WebDriverContext already exists for this thread.  Please remove it via removeContext() first.  " +
                            "Existing: %s New: %s", webDriverContext.getWebDriverManager(), testName)
            );
        }

        webDriverContext
                .webDriverManager(webDriverManager)
                .setTestName(testName);
        DRIVER_MANAGER.set(webDriverContext);
        log.debug(String.format("Setting webdrivercontext for %s", testName));
    }

    /**
     * Removes the webdrivercontext if it exists.
     */
    public void removeContext() {
        log.debug("Attempting to remove webdrivercontext.");
        var webDriverContext = getContext();
        var webDriverManager = webDriverContext.getWebDriverManager();
        if (webDriverManager != null) {
            // Make sure and close the driver (if it exists) to ensure no windows are left open
            // and that we don't have any abandoned WebDriver instances
            webDriverManager.closeDriver();
            webDriverContext
                    .webDriverManager(null)
                    .setTestName(null);
            log.debug("Context existed and removed.");
        }
    }

    /**
     * Returns the WebDriverContext for the current thread.
     *
     * @return the {@link WebDriverContext} from the current thread.
     */
    public WebDriverContext getWebDriverContext() {
        log.debug("Retrieving webdrivercontext");
        return getContext();
    }
}
