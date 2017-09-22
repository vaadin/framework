package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class TreeGridCollapseExpandTest extends SingleBrowserTestPhantomJS2 {

    private TreeGridElement grid;

    @Override
    public Class<?> getUIClass() {
        return TreeGridBasicFeatures.class;
    }

    @Test
    public void no_race_condition_with_multiple_collapse_or_expand() {
        openTestURL();
        grid = $(TreeGridElement.class).first();
        testBench().disableWaitForVaadin();

        // toggle expand of two rows simultaneously
        // only the first of the expands should occur
        executeScript("arguments[0].click(); arguments[1].click()",
                grid.getExpandElement(0, 0), grid.getExpandElement(1, 0));
        waitUntilRowCountEquals(6);
        assertCellTexts(0, 0,
                new String[] { "0 | 0", "1 | 0", "1 | 1", "1 | 2", "0 | 1" });

        // toggle collapse of the expanded first row and immediately expand the
        // last row
        // only the collapse should occur
        executeScript("arguments[0].click(); arguments[1].click()",
                grid.getExpandElement(0, 0), grid.getExpandElement(5, 0));
        waitUntilRowCountEquals(3);
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
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

    private void waitUntilRowCountEquals(int expectedCount) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return grid.getRowCount() == expectedCount;
            }
        });
    }
}
