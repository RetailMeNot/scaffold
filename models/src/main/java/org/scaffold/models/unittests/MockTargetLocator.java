package org.scaffold.models.unittests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MockTargetLocator implements WebDriver.TargetLocator {

    @Override
    public WebDriver frame(int index) {
        return new MockWebDriver();
    }

    @Override
    public WebDriver frame( String nameOrId) {
        return new MockWebDriver();
    }

    @Override
    public WebDriver frame( WebElement frameElement) {
        return new MockWebDriver();
    }

    @Override
    public WebDriver parentFrame() { return null; }

    @Override
    public WebDriver window( String nameOrHandle) {
        return new MockWebDriver();
    }

    @Override
    public WebDriver defaultContent() {
        return new MockWebDriver();
    }

    @Override
    public WebElement activeElement() {
        return new MockWebElement();
    }

    @Override
    public Alert alert() {
        return null;
    }

}
