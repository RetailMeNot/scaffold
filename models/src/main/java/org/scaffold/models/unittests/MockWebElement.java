package org.scaffold.models.unittests;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockWebElement implements WebElement {

    private String text;
    private boolean isClicked = false;
    private boolean isSubmitted = false;
    private WebElement elementToFind;
    private List<WebElement> elementsToFind;
    private Map<String, String> attributes = new HashMap<>();
    private String tagName;
    private boolean isSelected = false;

    private boolean isEnabled = true;
    private boolean isDisplayed = true;
    private boolean throwExceptionEnabled = false;
    private boolean throwExceptionDisplayed = false;

    private boolean isMouseOverNative = false;
    private boolean isMouseOverSynthetic = false;

    @Override
    public void clear() {
        this.text = "";
    }

    @Override
    public void click() {
        this.isClicked = true;
        this.isSelected = !this.isSelected;
    }

    @Override
    public WebElement findElement(By arg0) {
        if(elementToFind == null) {
            throw new NoSuchElementException("Could not find element");
        }
        return elementToFind;
    }

    @Override
    public List<WebElement> findElements(By arg0) {
        if(elementsToFind == null) {
            throw new NoSuchElementException("Could not find element");
        }
        return elementsToFind;
    }

    @Override
    public String getAttribute(String arg0) {
        return attributes.get(arg0);
    }

    @Override
    public String getCssValue(String arg0) {
        return null;
    }

    @Override
    public Point getLocation() {
        return null;
    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public Rectangle getRect() {
        return elementToFind.getRect();
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public boolean isDisplayed() {
        if (this.throwExceptionDisplayed) {
            throw new NoSuchElementException("isDisplayed() exception");
        }
        return this.isDisplayed;
    }

    @Override
    public boolean isEnabled() {
        if (this.throwExceptionEnabled) {
            throw new NoSuchElementException("isEnabled() exception");
        }
        return this.isEnabled;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void submit() {
        isSubmitted = true;
    }

    @Override
    public void sendKeys(CharSequence... arg0) {
        this.text = StringUtils.join(arg0);
        this.setAttribute("value", StringUtils.join(arg0));
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIsDisplayed(boolean isDisplayed) {
        this.isDisplayed = isDisplayed;
    }

    public boolean getIsClicked() {
        return this.isClicked;
    }

    public boolean getIsSubmitted() {
        return this.isSubmitted;
    }

    public void setElementToFind(WebElement element) {
        this.elementToFind = element;
    }

    public void setElementsToFind(List<WebElement> elements) {
        this.elementsToFind = elements;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean getIsMouseOverNative() {
        return this.isMouseOverNative;
    }

    public boolean getIsMouseOverSynthetic() {
        return this.isMouseOverSynthetic;
    }

    public void setThrowExceptionEnabled(boolean flag) {
        this.throwExceptionEnabled = flag;
    }

    public void setThrowExceptionDisplayed(boolean flag) {
        this.throwExceptionDisplayed = flag;
    }

    public void setThrowExceptionOnMouseOverNative(boolean flag) {
        this.isMouseOverNative = flag;
    }

    public void setThrowExceptionOnMouseOverSynthetic(boolean flag) {
        this.isMouseOverSynthetic = flag;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String toString() {
        return "MockWebElement: I Mock Thee!";
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return null;
    }

    public MockWebElement text(String text) {
        this.text = text;
        return this;
    }

    public MockWebElement isClicked(boolean isClicked) {
        this.isClicked = isClicked;
        return this;
    }

    public MockWebElement isSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
        return this;
    }

    public MockWebElement elementToFind(WebElement elementToFind) {
        this.elementToFind = elementToFind;
        return this;
    }

    public MockWebElement elementsToFind(List<WebElement> elementsToFind) {
        this.elementsToFind = elementsToFind;
        return this;
    }

    public MockWebElement attributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MockWebElement tagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public MockWebElement throwExceptionEnabled(boolean throwExceptionEnabled) {
        this.throwExceptionEnabled = throwExceptionEnabled;
        return this;
    }

    public MockWebElement throwExceptionDisplayed(boolean throwExceptionDisplayed) {
        this.throwExceptionDisplayed = throwExceptionDisplayed;
        return this;
    }

    public MockWebElement isMouseOverNative(boolean isMouseOverNative) {
        this.isMouseOverNative = isMouseOverNative;
        return this;
    }

    public MockWebElement isMouseOverSynthetic(boolean isMouseOverSynthetic) {
        this.isMouseOverSynthetic = isMouseOverSynthetic;
        return this;
    }
}
