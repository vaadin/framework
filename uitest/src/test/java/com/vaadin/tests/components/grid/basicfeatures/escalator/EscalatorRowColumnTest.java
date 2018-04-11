package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

public class EscalatorRowColumnTest extends EscalatorBasicClientFeaturesTest {

    /**
     * The scroll position of the Escalator when scrolled all the way down, to
     * reveal the 100:th row.
     */
    private static final int BOTTOM_SCROLL_POSITION = 1857;

    @Before
    public void open() {
        openTestURL("theme=reindeer");
    }

    @Test
    public void testInit() {
        assertNotNull(getEscalator());
        assertNull(getHeaderRow(0));
        assertNull(getBodyRow(0));
        assertNull(getFooterRow(0));

        assertLogContains("Columns: 0");
        assertLogContains("Header rows: 0");
        assertLogContains("Body rows: 0");
        assertLogContains("Footer rows: 0");
    }

    @Test
    public void testInsertAColumn() {
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        assertNull(getHeaderRow(0));
        assertNull(getBodyRow(0));
        assertNull(getFooterRow(0));
        assertLogContains("Columns: 1");
    }

    @Test
    public void testInsertAHeaderRow() {
        selectMenuPath(COLUMNS_AND_ROWS, HEADER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Header rows: 1");
    }

    @Test
    public void testInsertABodyRow() {
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Body rows: 1");
    }

    @Test
    public void testInsertAFooterRow() {
        selectMenuPath(COLUMNS_AND_ROWS, FOOTER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Footer rows: 1");
    }

    @Test
    public void testInsertAColumnAndAHeaderRow() {
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, HEADER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        assertNotNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Header rows: 1");
    }

    @Test
    public void testInsertAColumnAndABodyRow() {
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNotNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Body rows: 1");
    }

    @Test
    public void testInsertAColumnAndAFooterRow() {
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, FOOTER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNotNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Footer rows: 1");
    }

    @Test
    public void testInsertAHeaderRowAndAColumn() {
        selectMenuPath(COLUMNS_AND_ROWS, HEADER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        assertNotNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Header rows: 1");
    }

    @Test
    public void testInsertABodyRowAndAColumn() {
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNotNull(getBodyCell(0, 0));
        assertNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Body rows: 1");
    }

    @Test
    public void testInsertAFooterRowAndAColumn() {
        selectMenuPath(COLUMNS_AND_ROWS, FOOTER_ROWS, ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        assertNull(getHeaderCell(0, 0));
        assertNull(getBodyCell(0, 0));
        assertNotNull(getFooterCell(0, 0));
        assertLogContains("Columns: 1");
        assertLogContains("Footer rows: 1");
    }

    @Test
    public void testFillColRow() {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);
        scrollVerticallyTo(2000); // more like 1857, but this should be enough.

        // if not found, an exception is thrown here
        assertTrue("Wanted cell was not visible",
                isElementPresent(By.xpath("//td[text()='Cell: 9,99']")));
    }

    @Test
    public void testFillRowCol() {
        selectMenuPath(GENERAL, POPULATE_ROW_COLUMN);
        scrollVerticallyTo(2000); // more like 1857, but this should be enough.

        // if not found, an exception is thrown here
        assertTrue("Wanted cell was not visible",
                isElementPresent(By.xpath("//td[text()='Cell: 9,99']")));
    }

    @Test
    public void testClearColRow() {

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);
        selectMenuPath(GENERAL, CLEAR_COLUMN_ROW);

        assertNull(getBodyCell(0, 0));
    }

    @Test
    public void testClearRowCol() {

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);
        selectMenuPath(GENERAL, CLEAR_ROW_COLUMN);

        assertNull(getBodyCell(0, 0));
    }

    @Test
    public void testResizeColToFit() {

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, RESIZE_FIRST_COLUMN_TO_100PX);
        int originalWidth = getBodyCell(0, 0).getSize().getWidth();

        assertEquals(100, originalWidth);

        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS,
                RESIZE_FIRST_COLUMN_TO_MAX_WIDTH);
        int newWidth = getBodyCell(0, 0).getSize().getWidth();
        assertNotEquals("Column width should've changed", originalWidth,
                newWidth);
    }

    @Test
    public void testRemoveMoreThanPagefulAtBottomWhileScrolledToBottom()
            throws Exception {

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        scrollVerticallyTo(BOTTOM_SCROLL_POSITION);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_50_ROWS_FROM_BOTTOM);
        assertEquals("Row 49: 0,49", getBodyCell(-1, 0).getText());

        scrollVerticallyTo(0);

        // let the DOM organize itself
        Thread.sleep(500);

        // if something goes wrong, it'll explode before this.
        assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }

    @Test
    public void testRemoveMoreThanPagefulAtBottomWhileScrolledAlmostToBottom()
            throws Exception {

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        // bottom minus 15 rows.
        scrollVerticallyTo(BOTTOM_SCROLL_POSITION - 15 * 20);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS, REMOVE_50_ROWS_FROM_BOTTOM);
        assertEquals("Row 49: 0,49", getBodyCell(-1, 0).getText());

        scrollVerticallyTo(0);

        // let the DOM organize itself
        Thread.sleep(500);

        // if something goes wrong, it'll explode before this.
        assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }

    @Test
    public void testRemoveMoreThanPagefulNearBottomWhileScrolledToBottom()
            throws Exception {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        scrollVerticallyTo(BOTTOM_SCROLL_POSITION);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS,
                REMOVE_50_ROWS_FROM_ALMOST_BOTTOM);
        assertEquals("Row 49: 0,99", getBodyCell(-1, 0).getText());

        scrollVerticallyTo(0);

        // let the DOM organize itself
        Thread.sleep(500);

        // if something goes wrong, it'll explode before this.
        assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }

    @Test
    public void testRemoveMoreThanPagefulNearBottomWhileScrolledAlmostToBottom()
            throws Exception {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        // bottom minus 15 rows.
        scrollVerticallyTo(BOTTOM_SCROLL_POSITION - 15 * 20);
        selectMenuPath(COLUMNS_AND_ROWS, BODY_ROWS,
                REMOVE_50_ROWS_FROM_ALMOST_BOTTOM);

        // let the DOM organize itself
        Thread.sleep(500);
        assertEquals("Row 49: 0,99", getBodyCell(-1, 0).getText());

        scrollVerticallyTo(0);

        // let the DOM organize itself
        Thread.sleep(500);

        // if something goes wrong, it'll explode before this.
        assertEquals("Row 0: 0,0", getBodyCell(0, 0).getText());
    }
}
