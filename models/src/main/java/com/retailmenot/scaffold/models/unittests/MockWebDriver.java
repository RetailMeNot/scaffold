package com.retailmenot.scaffold.models.unittests;

import org.openqa.selenium.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockWebDriver implements WebDriver, JavascriptExecutor {

    private WebElement elementToFind;
    private List<WebElement> elementsToFind;
    private String pageSource;
    private boolean throwExceptionOnJavascriptExecute = false;

    @Override
    public void get(String url) {
    }

    @Override
    public String getCurrentUrl() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = this.elementsToFind;
        if(elements == null) {
            throw new NoSuchElementException("Could not locate element");
        }
        return elements;
    }

    @Override
    public WebElement findElement(By by) {
        WebElement element = this.elementToFind;
        if(element == null) {
            throw new NoSuchElementException("Could not locate element");
        }
        return element;
    }

    @Override
    public String getPageSource() {
        return pageSource;
    }

    @Override
    public void close() {
    }

    @Override
    public void quit() {
    }

    @Override
    public Set<String> getWindowHandles() {
        Set<String> handles = new HashSet<String>();
        handles.add("baseWindow");
        handles.add("childWindow");
        return handles;
    }

    @Override
    public String getWindowHandle() {
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return new MockTargetLocator();
    }

    @Override
    public Navigation navigate() {
        return null;
    }

    @Override
    public Options manage() {
        return new MockOptions();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        if (this.throwExceptionOnJavascriptExecute) {
            throw new RuntimeException("You asked for an exception, so here it is");
        }
        return "";
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return "";
    }

    public void setElementToFind(WebElement element) {
        this.elementToFind = element;
    }

    public void setElementsToFind(List<WebElement> elementsToFind) {
        this.elementsToFind = elementsToFind;
    }

    public void setPageSource(String pageSource) {
        this.pageSource = pageSource;
    }

    public void setThrowExceptionOnJavascriptExecute(boolean flag) {
        this.throwExceptionOnJavascriptExecute = flag;
    }
}
