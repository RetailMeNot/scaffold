package com.retailmenot.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of a Div {@link WebElement}.
 *
 * This can usually stand in for any sort of displayable element that is otherwise difficult to classify, such as a span,
 * a paragraph, or any other format-related object in the DOM
 */
public class DivWebElement extends AbstractClickable {

    public DivWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public DivWebElement(By by) {
        super(by);
    }

    public DivWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public DivWebElement(WebElement element) {
        super(element);
    }
}
