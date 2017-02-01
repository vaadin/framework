package com.vaadin.tests.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ReplaceDataProviderTest extends SingleBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void test_grid_data_communication_with_replaced_data_provider() {
        GridElement grid = $(GridElement.class).first();
        ButtonElement replaceDataProviderButton = $(ButtonElement.class)
                .first();

        Assert.assertEquals(20, grid.getRowCount());
        grid.getCell(0, 0).click();
        assertCellText("a", 0, 0);

        replaceDataProviderButton.click();

        Assert.assertEquals(10, grid.getRowCount());
        assertCellText("b", 0, 0);
        for (int i = 1; i < 10; i++) {
            assertCellText("a", i, 0);
        }

        Assert.assertFalse(grid.getRow(0).isSelected());

        grid.getCell(0, 0).click();
        assertCellText("b", 0, 0);

        // This button should replace the data provider and do a server side
        // select on the second item
        $(ButtonElement.class).get(1).click();
        grid.getRow(1).isSelected();
    }

    private void assertCellText(String text, int rowIndex, int colIndex) {
        String firstCellText = $(GridElement.class).first()
                .getCell(rowIndex, colIndex).getText();
        Assert.assertEquals(text, firstCellText);
    }
}
