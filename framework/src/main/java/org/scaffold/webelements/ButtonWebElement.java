package io.github.kgress.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of a Button {@link WebElement}.
 */
public class ButtonWebElement extends AbstractClickable {

    public ButtonWebElement(By by) {
        super(by);
    }

    public ButtonWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public ButtonWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public ButtonWebElement(WebElement element) {
        super(element);
    }
}
