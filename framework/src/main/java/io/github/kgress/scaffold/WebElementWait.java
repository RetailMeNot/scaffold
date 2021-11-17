package io.github.kgress.scaffold;

import lombok.Getter;
import org.openqa.selenium.WebElement;

public class WebElementWait {

    @Getter
    private final BaseWebElement element;

    @Getter
    private final WebDriverWrapper webDriverWrapper;

    WebElementWait(WebDriverWrapper webDriverWrapper, BaseWebElement element) {
        this.webDriverWrapper = webDriverWrapper;
        this.element = element;
    }

    /**
     * Waits for an element to be displayed using {@link AutomationWait#waitUntilDisplayed(BaseWebElement, Long)}
     * @return as {@link WebElement}
     */
    public WebElement waitUntilDisplayed() {
        return getWebDriverWrapper().getAutomationWait().waitUntilDisplayed(getElement());
    }

    /**
     * Waits for the page to return to a loaded state.
     * @return as {@link Boolean}
     */
    public Boolean waitUntilPageIsLoaded() {
        return getWebDriverWrapper().getAutomationWait().waitUntilPageIsLoaded();
    }
}
