package io.github.kgress.scaffold.webelements.table;

import io.github.kgress.scaffold.webelements.AbstractWebElement;
import org.openqa.selenium.WebElement;

public class TableRow extends AbstractWebElement {

    private final int rowIndex;

    protected TableRow(WebElement element, int rowIndex) {
        super(element);
        this.rowIndex = rowIndex;
    }

    /**
     * Get index of table row.
     *
     * @return - {@code int} - one-based table row index
     */
    public int getRowIndex() {
        return rowIndex + 1;
    }
}
