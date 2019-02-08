package com.retailmenot.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of a RadioWebElement {@link WebElement}.
 *
 * A RadioWebElement and a CheckBoxElement can sometimes be interchangeable. In the future, we should distinguish these
 * by adding functionality that is specific to their concept.
 */
public class RadioWebElement extends AbstractClickable {

    public RadioWebElement(By by) {
        super(by);
    }

    public RadioWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public RadioWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public RadioWebElement(WebElement element) {
        super(element);
    }

    /**
     * Indicates whether or not an element is selected.
     *
     @return the result as {@link boolean}
     */
    public boolean isSelected() {
        return getWebElement().isSelected();
    }
}
