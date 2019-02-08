package com.retailmenot.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of a Link {@link WebElement}.
 */
public class LinkWebElement extends AbstractClickable {

    public LinkWebElement(By by) {
        super(by);
    }

    public LinkWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public LinkWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public LinkWebElement(WebElement element) {
        super(element);
    }

    /**
     * Returns the link text as rendered in the UI
     *
     * @return the link as {@link String}
     */
    public String getLinkText() {
        return getWebElement().getText();
    }

    /**
     * Returns the link href (the destination URL)
     *
     * @return the link's URL as {@link String}
     */
    public String getLinkHref() {
        return getWebElement().getAttribute("href");
    }
}
