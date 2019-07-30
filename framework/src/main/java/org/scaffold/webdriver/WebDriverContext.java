package io.github.kgress.scaffold.webdriver;

/**
 * A model that is used to pair a {@link WebDriverManager} with a TestName. This is used in the {@link BaseTestContext} class.
 *
 * As of now, this file lives under the framework module for ease of use. It is not included under modules due to a cyclical
 * dependency.
 */
public class WebDriverContext {

    private WebDriverManager webDriverManager;
    private String testName;

    public WebDriverContext(WebDriverManager webDriverManager, String testName) {
        this.webDriverManager = webDriverManager;
        this.testName = testName;
    }

    public WebDriverManager getWebDriverManager() {
        return webDriverManager;
    }

    public String getTestName() {
        return testName;
    }

    public WebDriverContext webDriverManager(WebDriverManager webDriverManager) {
        this.webDriverManager = webDriverManager;
        return this;
    }

    public WebDriverContext testName(String testName) {
        this.testName = testName;
        return this;
    }
}
