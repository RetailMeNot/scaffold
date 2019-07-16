package org.scaffold.webelements.table;

import org.scaffold.webelements.AbstractWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTable extends AbstractWebElement {

    public AbstractTable(By by) {
        super(by);
    }

    /**
     * Get list of table headers.
     *
     * @return the headers as a {@link List} of {@link TableHeader}.
     */
    public List<TableHeader> getHeaders() {
        List<WebElement> headerElements = getWebElement().findElements(By.cssSelector("th"));
        List<TableHeader> headers = new LinkedList<TableHeader>();
        for (int i = 0; i < headerElements.size(); i++) {
            headers.add(new TableHeader(headerElements.get(i), i));
        }
        return headers;
    }

    /**
     * Get list of table rows.
     *
     * @return the rows as a {@link List} of {@link TableRow};
     */
    public List<TableRow> getRows() {
        List<WebElement> rowElements = getWebElement().findElements(By.cssSelector("tbody tr"));
        List<TableRow> rows = new LinkedList<TableRow>();
        for (int i = 0; i < rowElements.size(); i++) {
            rows.add(new TableRow(rowElements.get(i), i));
        }
        return rows;
    }

    /**
     * Attempts to locate the specified TableCell in the specified table.
     *
     * @param column   Locator to identify which column to search off of
     * @param cellText Text to identify the row
     * @return the {@link TableCell}
     */
    protected TableCell getTableCell(By column, String cellText) {
        int columnIndex = getHeaderIndex(column);
        // If we couldn't find the column we want to look off of we need to just
        // kick out here
        // and return null
        if (columnIndex == -1) {
            return null;
        }
        // Now that we know the column index, we need to find the row
        List<TableRow> rowElements = getRows();
        // If we don't have any row elements, return null
        if (null == rowElements) {
            return null;
        }
        for (TableRow row : rowElements) {
            if (row.getWebElement().findElement(By.cssSelector(":first-of-type")).getTagName().equals("th")) {
                continue;
            }
            WebElement columnElementForRow = row.getWebElement().findElement(By.cssSelector(String.format("td:nth-of-type(%d)", columnIndex)));
            String text = columnElementForRow.getText();
            if (null != text && text.contains(cellText)) {
                return new TableCell(columnElementForRow, columnIndex, row.getRowIndex());
            }
        }
        // If we didn't find our target row element, we'll just return null here
        // rather than throwing any
        // sort of exception
        return null;
    }

    /**
     * Get index of a specified table header.
     *
     * @param by - by locator to find the table header
     * @return {@code int} - table header index
     */
    protected abstract int getHeaderIndex(By by);

    /**
     * Get a specified row from a table.
     *
     * @param column   - column Locator to identify which column to search off of
     * @param cellText - unique cell text in specified column
     * @return {@code TableRow}
     */
    protected TableRow getTableRow(By column, String cellText) {
        TableCell tableCell = getTableCell(column, cellText);
        int rowIndex = tableCell.getRowLocation();
        return getTableRow(rowIndex);
    }

    /**
     * Get a specified row from a table by index.
     *
     * @param rowIndex - index of the row
     * @return {@code TableRow}
     */
    public TableRow getTableRow(int rowIndex) {
        return new TableRow(getWebElement().findElement(By.cssSelector(String.format("tbody tr:nth-of-type(%d)", rowIndex))), rowIndex);
    }

    /**
     * Attempts to locate the specified TableCell in the specified column by its index.
     *
     * @param column   - locator to identify which column to search off of
     * @param rowIndex - index to identify the row (NOTE: index is one-based)
     * @return the {@link TableCell}
     */
    protected TableCell getTableCellByIndex(By column, int rowIndex) {
        int columnIndex = getHeaderIndex(column);
        TableRow row = getTableRow(rowIndex);
        WebElement element = row.getWebElement().findElement(By.cssSelector(String.format("td:nth-of-type(%d)", columnIndex)));
        return new TableCell(element, columnIndex, rowIndex);
    }

    /**
     * Check whether TableCell exists.
     *
     * @param column     - by locator to identify which column to search off of
     * @param uniqueTerm - unique cell text in specified column
     * @return {@code true} - if TableCell can be found in table
     */
    protected boolean entityExists(By column, String uniqueTerm) {
        return null != getTableCell(column, uniqueTerm);
    }
}
