package com.vaadin.tests.components.treegrid;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.performance.TreeGridMemory;
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

    @Test
    public void collapsed_subtrees_outside_of_cache_stay_expanded() {
        getDriver().get(StringUtils.strip(getBaseURL(), "/")
                + TreeGridMemory.PATH + "?items=200&initiallyExpanded");
        grid = $(TreeGridElement.class).first();

        String[] cellTexts = new String[100];
        for (int i = 0; i < 100; i++) {
            cellTexts[i] = grid.getRow(i).getCell(0).getText();
        }
        grid.scrollToRow(0);

        grid.collapseWithClick(1);
        grid.expandWithClick(1);

        assertCellTexts(0, 0, cellTexts);
    }

    private void assertCellTexts(int startRowIndex, int cellIndex,
            String[] cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            Assert.assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }
}
