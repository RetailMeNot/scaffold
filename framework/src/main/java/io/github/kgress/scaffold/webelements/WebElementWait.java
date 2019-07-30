package io.github.kgress.scaffold.webelements;

import io.github.kgress.scaffold.webdriver.WebDriverWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class WebElementWait {
    private AbstractWebElement element;
    private WebDriverWrapper driver;

    WebElementWait(WebDriverWrapper driver, AbstractWebElement element) {
        this.driver = driver;
        this.element = element;
    }

    public void waitUntilDisplayed() {
        // Do a custom condition here to handle if we're using a webelement instead of a by locator for this element
        driver.getAutomationWait().waitForCustomCondition(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return element.isDisplayed();
            }

            @Override
            public String toString() {
                return element.toString();
            }
        });
    }
}
