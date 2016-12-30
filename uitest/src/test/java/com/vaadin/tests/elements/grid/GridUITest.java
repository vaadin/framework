package com.vaadin.tests.elements.grid;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridUITest extends MultiBrowserTest {

    @Test
    @Ignore("API for getRowCount is missing in FW8 ATM")
    public void testRowCount() {
        openTestURL("rowCount=0");
        Assert.assertEquals(0, getRowCount());
        openTestURL("rowCount=1&restartApplication");
        Assert.assertEquals(1, getRowCount());
        openTestURL("rowCount=10&restartApplication");
        Assert.assertEquals(10, getRowCount());
        openTestURL("rowCount=1000&restartApplication");
        Assert.assertEquals(1000, getRowCount());
    }

    private long getRowCount() {
        return $(GridElement.class).first().getRowCount();
    }

    private Iterable<GridRowElement> getRows() {
        return $(GridElement.class).first().getRows();
    }

    @Test
    @Ignore("API needed for getRows working is missing in FW8 ATM")
    public void testGetRows() {
        openTestURL("rowCount=0");
        Assert.assertEquals(0, checkRows());
        openTestURL("rowCount=1&restartApplication");
        Assert.assertEquals(1, checkRows());
        openTestURL("rowCount=10&restartApplication");
        Assert.assertEquals(10, checkRows());
        openTestURL("rowCount=100&restartApplication");
        Assert.assertEquals(100, checkRows());
    }

    private int checkRows() {
        int rowCount = 0;
        for (final GridRowElement row : getRows()) {
            Assert.assertEquals("foo " + rowCount, row.getCell(0).getText());
            Assert.assertEquals("bar " + rowCount, row.getCell(1).getText());
            rowCount++;
        }
        return rowCount;
    }
}
