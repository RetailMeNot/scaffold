package io.github.kgress.scaffold;

import lombok.Getter;
import lombok.Setter;

/**
 * A model that is used to pair a {@link WebDriverManager} with a TestName. This is used in the {@link BaseTestContext} class.
 *
 * As of now, this file lives under the framework module for ease of use. It is not included under modules due to a cyclical
 * dependency.
 */
public class WebDriverContext {

    @Getter
    public WebDriverManager webDriverManager;

    @Getter @Setter public String testName;

    public WebDriverContext(WebDriverManager webDriverManager, String testName) {
        this.webDriverManager = webDriverManager;
        this.testName = testName;
    }

    public WebDriverContext webDriverManager(WebDriverManager webDriverManager) {
        this.webDriverManager = webDriverManager;
        return this;
    }
}
