package com.vaadin.tests.elements.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableGetRowTest extends MultiBrowserTest {

    private static final String GET_ROW_ERROR_MESSAGE = "TableElement.getRow() returns wrong row.";
    private static final String GET_CELL_ERROR_MESSAGE = "TableElement.getCell() returns wrong cell.";

    @Override
    protected Class<?> getUIClass() {
        return TableScroll.class;
    }

    TableElement table;
    int firstRow = 0;
    int firstCol = 0;

    @Before
    public void init() {
        openTestURL();
        table = $(TableElement.class).first();
    }

    @Test
    public void getTopRowTest() {
        TableRowElement row = table.getRow(0);
        WebElement cell = row.getCell(0);
        String expected = "col=0 row=0";
        String actual = cell.getText();

        Assert.assertEquals(GET_ROW_ERROR_MESSAGE, expected, actual);
    }

    @Test
    public void getFifthRowTest() {
        TableRowElement row = table.getRow(4);
        WebElement cell = row.getCell(1);
        String expected = "col=1 row=4";
        String actual = cell.getText();
        Assert.assertEquals(GET_ROW_ERROR_MESSAGE, expected, actual);
    }

    @Test
    public void rowGetCellTest() {
        TestBenchElement cellFromTable = table.getCell(firstRow, firstCol);
        WebElement cellFromRow = table.getRow(firstRow).getCell(firstCol);
        Assert.assertEquals(
                "Table.getCell() and Row.getCell() return different values",
                cellFromRow.getText(), cellFromTable.getText());
    }

    @Test
    public void tableGetCellTest() {
        TestBenchElement cell = table.getCell(firstRow, firstCol);
        String actual = cell.getText();
        String expected = "col=0 row=0";
        Assert.assertEquals(GET_CELL_ERROR_MESSAGE, expected, actual);
    }

    @Test(expected = NoSuchElementException.class)
    public void getRowExceptionTest() {
        table.getRow(-5);
    }

    @Test(expected = NoSuchElementException.class)
    public void tableGetCellExceptionTest() {
        table.getCell(-1, -1);
    }
}
