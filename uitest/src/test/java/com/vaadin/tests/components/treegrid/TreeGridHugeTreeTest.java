package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridHugeTreeTest extends SingleBrowserTest {

    private TreeGridElement grid;

    @Test
    public void toggle_expand_when_row_out_of_cache() {
        openTestURL();

        grid = $(TreeGridElement.class).first();
        ButtonElement expandSecondRowButton = $(ButtonElement.class).get(0);
        ButtonElement collapseSecondRowButton = $(ButtonElement.class).get(1);

        grid.expandWithClick(2);
        grid.expandWithClick(3);
        grid.scrollToRow(300);
        grid.waitForVaadin();

        expandSecondRowButton.click();

        grid.scrollToRow(0);
        assertCellTexts(0, 0, new String[] { "Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2", "Dad 2/0" });

        grid.scrollToRow(300);
        grid.waitForVaadin();
        collapseSecondRowButton.click();
        grid.scrollToRow(0);
        grid.waitForVaadin();
        assertCellTexts(0, 0, new String[] { "Granddad 0", "Granddad 1",
                "Granddad 2", "Dad 2/0" });

        grid.scrollToRow(300);
        grid.waitForVaadin();
        expandSecondRowButton.click();
        collapseSecondRowButton.click();
        grid.scrollToRow(0);
        grid.waitForVaadin();
        assertCellTexts(0, 0, new String[] { "Granddad 0", "Granddad 1",
                "Granddad 2", "Dad 2/0" });
    }

    @Test
    public void collapsed_rows_invalidated_correctly() {
        openTestURL();
        grid = $(TreeGridElement.class).first();
        grid.expandWithClick(2);
        grid.expandWithClick(3);
        grid.expandWithClick(0);
        grid.collapseWithClick(0);
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        assertCellTexts(0, 0,
                new String[] { "Granddad 0", "Dad 0/0", "Son 0/0/0" });
    }

    private void assertCellTexts(int startRowIndex, int cellIndex,
            String[] cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }
}
