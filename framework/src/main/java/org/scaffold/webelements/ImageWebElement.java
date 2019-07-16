package org.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of an Image {@link WebElement}.
 */
public class ImageWebElement extends AbstractWebElement {

    public ImageWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public ImageWebElement(By by) {
        super(by);
    }

    public ImageWebElement(WebElement element) {
        super(element);
    }

    /**
     * Returns the contents of the "src" attribute in the Image link (usually a pointer to a URL)
     *
     * @return the image source as {@link String}
     */
    public String getImageSource() {
        return getWebElement().getAttribute("src");
    }
}
