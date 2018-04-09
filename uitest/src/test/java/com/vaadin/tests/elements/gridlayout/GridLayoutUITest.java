package com.vaadin.tests.elements.gridlayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridLayoutUITest extends SingleBrowserTest {

    @Test
    public void getRows() {
        openTestURL();
        assertEquals(1, $(GridLayoutElement.class)
                .id(GridLayoutUI.ONE_ROW_ONE_COL).getRowCount());
        assertEquals(10, $(GridLayoutElement.class)
                .id(GridLayoutUI.TEN_ROWS_TEN_COLS).getRowCount());
    }

    @Test
    public void getColumns() {
        openTestURL();
        assertEquals(1, $(GridLayoutElement.class)
                .id(GridLayoutUI.ONE_ROW_ONE_COL).getColumnCount());
        assertEquals(10, $(GridLayoutElement.class)
                .id(GridLayoutUI.TEN_ROWS_TEN_COLS).getColumnCount());
    }

    @Test
    public void getCell() {
        openTestURL();
        GridLayoutElement grid = $(GridLayoutElement.class)
                .id(GridLayoutUI.TEN_ROWS_TEN_COLS);

        WebElement cell55 = grid.getCell(5, 5);
        assertEquals("v-gridlayout-slot", cell55.getAttribute("class"));
        assertEquals("5-5", cell55.getText());

        try {
            grid.getCell(4, 4);
            fail("Should throw for empty cell");
        } catch (NoSuchElementException e) {
        }

        WebElement cell77 = grid.getCell(7, 7);
        assertEquals("v-gridlayout-slot", cell77.getAttribute("class"));
        assertEquals("7-7 8-8", cell77.getText());

        try {
            grid.getCell(7, 8);
            fail("Should throw for merged cell");
        } catch (NoSuchElementException e) {
        }
    }
}
