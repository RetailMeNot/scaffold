package io.github.kgress.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A strongly typed representation of an Input {@link WebElement}.
 */
public class InputWebElement extends AbstractClickable {

    public InputWebElement(By by) {
        super(by);
    }

    public InputWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public InputWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public InputWebElement(WebElement element) {
        super(element);
    }

    /**
     * Returns the attribute "value" from the input.
     *
     * @return the value of the input as {@link String}
     */
    public String getValue() {
        return getWebElement().getAttribute("value");
    }

    /**
     * @param keys the text to send to the input.
     *
     * @see WebElement#sendKeys(CharSequence...)
     */
    public void sendKeys(String keys) {
        getWebElement().sendKeys(keys);
    }

    /**
     * Clears the text from the input.
     *
     * @see WebElement#clear()
     */
    public void clear() {
        getWebElement().clear();
    }

    /**
     * Clears the input field and sends the given keys. If the string is null or empty, this will simply have the effect
     * of clearing the field. NOTE: If you just send whitespace, it *will* be typed into the field.
     *
     * @param keys the text to send to the input
     */
    public void clearAndSendKeys(String keys) {
        this.clear();
        // If the input is null or empty, clearing the element is sufficient and we don't need to unnecessarily
        // send an empty string as the field will already be empty
        if (keys != null && keys.length() > 0) {
            this.sendKeys(keys);
        }
    }
}
