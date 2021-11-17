//package io.github.kgress.scaffold.webelements.table;
//
//import io.github.kgress.scaffold.BaseWebElement;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//
//public class TableCell extends BaseWebElement {
//
//    private int columnLocation, rowLocation;
//
//    public TableCell(String cssSelector, int columnLocation, int rowLocation) {
//        super(cssSelector);
//        this.columnLocation = columnLocation;
//        this.rowLocation = rowLocation;
//    }
//
//    public TableCell(By by, int columnLocation, int rowLocation) {
//        super(by);
//        this.columnLocation = columnLocation;
//        this.rowLocation = rowLocation;
//    }
//
//    public TableCell(By by, By parentBy, int columnLocation, int rowLocation) {
//        super(by, parentBy);
//        this.columnLocation = columnLocation;
//        this.rowLocation = rowLocation;
//    }
//
//    public TableCell(WebElement webElement, int columnLocation, int rowLocation) {
//        super(webElement);
//        this.columnLocation = columnLocation;
//        this.rowLocation = rowLocation;
//    }
//
//    public TableCell(By by, WebElement webElement, int columnLocation, int rowLocation) {
//        super(by, webElement);
//        this.columnLocation = columnLocation;
//        this.rowLocation = rowLocation;
//    }
//
//    public TableCell(By by, By parentBy, WebElement webElement, int columnLocation, int rowLocation) {
//        super(by, parentBy, webElement);
//        this.columnLocation = columnLocation;
//        this.rowLocation = rowLocation;
//    }
//
//    public TableCell(String cssSelector) {
//        super(cssSelector);
//    }
//
//    public TableCell(By by) {
//        super(by);
//    }
//
//    public TableCell(By by, By parentBy) {
//        super(by, parentBy);
//    }
//
//    public TableCell(WebElement webElement) {
//        super(webElement);
//    }
//
//    public TableCell(By by, WebElement webElement) {
//        super(by, webElement);
//    }
//
//    public TableCell(By by, By parentBy, WebElement webElement) {
//        super(by, parentBy, webElement);
//    }
//
//    /**
//     * Get column index of table cell.
//     *
//     * @return {@code int} - column index
//     */
//    public int getColumnLocation() {
//        return columnLocation;
//    }
//
//    /**
//     * Get row index of table cell.
//     *
//     * @return {@code int} - row index
//     */
//    public int getRowLocation() {
//        return rowLocation;
//    }
//}
