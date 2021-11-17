//package io.github.kgress.scaffold.webelements.table;
//
//import io.github.kgress.scaffold.BaseWebElement;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//
//public class TableRow extends BaseWebElement {
//
//    private int rowIndex;
//
//    public TableRow(String cssSelector, int rowIndex) {
//        super(cssSelector);
//        this.rowIndex = rowIndex;
//    }
//
//    public TableRow(By by, int rowIndex) {
//        super(by);
//        this.rowIndex = rowIndex;
//    }
//
//    public TableRow(By by, By parentBy, int rowIndex) {
//        super(by, parentBy);
//        this.rowIndex = rowIndex;
//    }
//
//    public TableRow(WebElement webElement, int rowIndex) {
//        super(webElement);
//        this.rowIndex = rowIndex;
//    }
//
//    public TableRow(By by, WebElement webElement, int rowIndex) {
//        super(by, webElement);
//        this.rowIndex = rowIndex;
//    }
//
//    public TableRow(By by, By parentBy, WebElement webElement, int rowIndex) {
//        super(by, parentBy, webElement);
//        this.rowIndex = rowIndex;
//    }
//
//    public TableRow(String cssSelector) {
//        super(cssSelector);
//    }
//
//    public TableRow(By by) {
//        super(by);
//    }
//
//    public TableRow(By by, By parentBy) {
//        super(by, parentBy);
//    }
//
//    public TableRow(WebElement webElement) {
//        super(webElement);
//    }
//
//    public TableRow(By by, WebElement webElement) {
//        super(by, webElement);
//    }
//
//    public TableRow(By by, By parentBy, WebElement webElement) {
//        super(by, parentBy, webElement);
//    }
//
//    /**
//     * Get index of table row.
//     *
//     * @return - {@code int} - one-based table row index
//     */
//    public int getRowIndex() {
//        return rowIndex + 1;
//    }
//}
