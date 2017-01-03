package com.vaadin.tests.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ReplaceDataProviderTest extends SingleBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void test_grid_data_communication_with_replaced_data_provider() {
        GridElement grid = $(GridElement.class).first();
        GridRowElement firstGridRow = grid.getRows().iterator().next();
        ButtonElement changeDataProviderButton = $(ButtonElement.class).first();

        Assert.assertEquals(20, grid.getRowCount());
        grid.getCell(0, 0).click();
        assertCellText("a", 0, 0);

        changeDataProviderButton.click();

        Assert.assertEquals(10, grid.getRowCount());
        assertCellText("b", 0, 0);
        for (int i = 1; i < 10; i++) {
            assertCellText("a", i, 0);
        }

        Assert.assertFalse(firstGridRow.isSelected());

        grid.getCell(0, 0).click();
        assertCellText("b", 0, 0);
    }

    private void assertCellText(String text, int rowIndex, int colIndex) {
        String firstCellText = $(GridElement.class).first()
                .getCell(rowIndex, colIndex)
                .getText();
        Assert.assertEquals(text, firstCellText);
    }
}
