package io.github.kgress.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * A strongly typed representation of a Static Text {@link WebElement}.
 *
 * This wraps around simple read-only text fields, allowing you to subclass and differentiate any special controls you
 * might run into
 */
public class StaticTextWebElement extends AbstractClickable {

    public StaticTextWebElement(By by) {
        super(by);
    }

    public StaticTextWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public StaticTextWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public StaticTextWebElement(WebElement element) {
        super(element);
    }
}
