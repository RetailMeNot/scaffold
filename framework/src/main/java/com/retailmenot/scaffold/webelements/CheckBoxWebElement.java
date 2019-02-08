package com.retailmenot.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of a CheckBox {@link WebElement}.
 */
public class CheckBoxWebElement extends AbstractClickable {

    public CheckBoxWebElement(By by) {
        super(by);
    }

    public CheckBoxWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public CheckBoxWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public CheckBoxWebElement(WebElement element) {
        super(element);
    }

    /**
     * Helper method to check a value with a given boolean param.
     *
     * @param value the state in which the checkbox should be in.
     */
    public void check(boolean value) {
        if (value) {
            check();
        } else {
            uncheck();
        }
    }

    /**
     * Indicates whether or not an element is selected.
     *
     @return the result as {@link boolean}
     */
    public boolean isSelected() {
        return getWebElement().isSelected();
    }

    private void check() {
        if (!getWebElement().isSelected()) {
            getWebElement().click();
        }
    }

    private void uncheck() {
        if (getWebElement().isSelected()) {
            getWebElement().click();
        }
    }
}
