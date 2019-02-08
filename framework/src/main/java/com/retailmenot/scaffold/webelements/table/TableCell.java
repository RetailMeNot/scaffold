package com.retailmenot.scaffold.webelements.table;

import com.retailmenot.scaffold.webelements.AbstractWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TableCell extends AbstractWebElement {
    protected int columnLocation, rowLocation;

    public TableCell(By by, int columnLocation, int rowLocation) {
        super(by);
        this.columnLocation = columnLocation;
        this.rowLocation = rowLocation;
    }

    public TableCell(WebElement element, int columnLocation, int rowLocation) {
        super(element);
        this.columnLocation = columnLocation;
        this.rowLocation = rowLocation;
    }

    public TableCell(WebElement parentElement) {
        super(parentElement);
    }

    /**
     * Get column index of table cell.
     *
     * @return {@code int} - column index
     */
    public int getColumnLocation() {
        return columnLocation;
    }

    /**
     * Get row index of table cell.
     *
     * @return {@code int} - row index
     */
    public int getRowLocation() {
        return rowLocation;
    }
}
