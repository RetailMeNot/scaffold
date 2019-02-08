package com.retailmenot.scaffold.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A strongly typed representation of a DropDown {@link WebElement}.
 */
public class DropDownWebElement extends AbstractWebElement {

    public DropDownWebElement(By by) {
        super(by);
    }

    public DropDownWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public DropDownWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public DropDownWebElement(WebElement element) {
        super(element);
    }

    /**
     * Returns a list of options in the DropDown
     *
     * @return the list of options.
     */
    public List<String> getOptionsText() {
        return getSelectElement().getOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * @param index the index to select
     * @see Select#selectByIndex(int)
     */
    public void selectByIndex(int index) {
        getSelectElement().selectByIndex(index);
    }

    /**
     * @param value the value to select
     * @see Select#selectByIndex(int)
     */
    public void selectByValue(String value) {
        getSelectElement().selectByValue(value);
    }

    public void getValue() {
        getWebElement().getAttribute("value");
    }

    /**
     * Sets the drop-down to the value contained in the visible of the <option> tag. If @value is blank, it will just
     * leave the field alone--If someone is doing data-driven tests that contain many variables, we don't want to require
     * them to surround every instance of this method with the same if block.
     *
     * @param value the value to select
     */
    public void selectByVisibleText(String value) {
        //If the string is null or empty, just leave the field as-is. It is preferable to do this check here than have to surround the call with this logic every time
        if (value.isBlank()) {
            getSelectElement().selectByVisibleText(value);
        }
    }

    /**
     * Return a Selenium Select object (a combo box) based on the underlying {@link WebElement}
     *
     * @return the {@link Select} object
     */
    private Select getSelectElement() {
        return new Select(this.getWebElement());
    }
}
