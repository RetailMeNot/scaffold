//package io.github.kgress.scaffold.webelements.table;
//
//import io.github.kgress.scaffold.BaseWebElement;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//
//public class TableHeader extends BaseWebElement {
//
//    private int columnIndex;
//
//    public TableHeader(String cssSelector, int columnIndex) {
//        super(cssSelector);
//        this.columnIndex = columnIndex;
//    }
//
//    public TableHeader(By by, int columnIndex) {
//        super(by);
//        this.columnIndex = columnIndex;
//    }
//
//    public TableHeader(By by, By parentBy, int columnIndex) {
//        super(by, parentBy);
//        this.columnIndex = columnIndex;
//    }
//
//    public TableHeader(WebElement webElement, int columnIndex) {
//        super(webElement);
//        this.columnIndex = columnIndex;
//    }
//
//    public TableHeader(By by, WebElement webElement, int columnIndex) {
//        super(by, webElement);
//        this.columnIndex = columnIndex;
//    }
//
//    public TableHeader(By by, By parentBy, WebElement webElement, int columnIndex) {
//        super(by, parentBy, webElement);
//        this.columnIndex = columnIndex;
//    }
//
//    public TableHeader(String cssSelector) {
//        super(cssSelector);
//    }
//
//    public TableHeader(By by) {
//        super(by);
//    }
//
//    public TableHeader(By by, By parentBy) {
//        super(by, parentBy);
//    }
//
//    public TableHeader(WebElement webElement) {
//        super(webElement);
//    }
//
//    public TableHeader(By by, WebElement webElement) {
//        super(by, webElement);
//    }
//
//    public TableHeader(By by, By parentBy, WebElement webElement) {
//        super(by, parentBy, webElement);
//    }
//
//    /**
//     * Get index of specified table header.
//     *
//     * @return - {@code int} - one-based table header index
//     */
//    public int getColumnIndex() {
//        return this.columnIndex + 1;
//    }
//}
