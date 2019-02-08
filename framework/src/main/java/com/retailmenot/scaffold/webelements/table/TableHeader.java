package com.retailmenot.scaffold.webelements.table;

import com.retailmenot.scaffold.webelements.AbstractWebElement;
import org.openqa.selenium.WebElement;

public class TableHeader extends AbstractWebElement {

    private final int columnIndex;

    public TableHeader(WebElement element, int columnIndex) {
        super(element);
        this.columnIndex = columnIndex;
    }

    /**
     * Get index of specified table header.
     *
     * @return - {@code int} - one-based table header index
     */
    public int getColumnIndex() {
        return this.columnIndex + 1;
    }
}
