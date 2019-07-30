package io.github.kgress.scaffold.webelements.interfaces;

import io.github.kgress.scaffold.webdriver.WebDriverWrapper;

// Use this interface to specify a blocking custom wait condition to happen
public interface DisplayWaitCondition {
    /**
     * Waits for the custom condition set here before proceeding
     *
     * @param driver the instance of the {@link WebDriverWrapper}
     */
    void waitUntilLoaded(WebDriverWrapper driver);
}
