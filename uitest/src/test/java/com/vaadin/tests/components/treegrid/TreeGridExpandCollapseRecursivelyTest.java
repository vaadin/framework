package com.vaadin.tests.components.treegrid;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;

public class TreeGridExpandCollapseRecursivelyTest extends SingleBrowserTest {

    private static final int rowCount0 = 5;
    private static final int rowCount1 = rowCount0 + rowCount0 * 5;
    private static final int rowCount2 = rowCount1
            + (rowCount1 - rowCount0) * 5;
    private static final int rowCount3 = rowCount2
            + (rowCount2 - rowCount1) * 5;
    private static final int rowCount4 = rowCount3
            + (rowCount3 - rowCount2) * 5;

    private TreeGridElement grid;
    private RadioButtonGroupElement depthSelector;
    private ButtonElement expandButton;
    private ButtonElement collapseButton;

    @Before
    public void before() {
        openTestURL();
        grid = $(TreeGridElement.class).first();
        depthSelector = $(RadioButtonGroupElement.class).first();
        expandButton = $(ButtonElement.class).get(0);
        collapseButton = $(ButtonElement.class).get(1);
    }

    @Test
    public void expandVariousDepth() {
        assertEquals(rowCount0, grid.getRowCount());

        selectDepth(0);
        expandButton.click();

        assertEquals(rowCount1, grid.getRowCount());

        selectDepth(1);
        expandButton.click();

        assertEquals(rowCount2, grid.getRowCount());

        selectDepth(2);
        expandButton.click();

        assertEquals(rowCount3, grid.getRowCount());

        selectDepth(3);
        expandButton.click();

        assertEquals(rowCount4, grid.getRowCount());
    }

    @Test(timeout = 5000)
    public void expandAndCollapseAllItems() {
        assertEquals(rowCount0, grid.getRowCount());

        selectDepth(3);
        expandButton.click();

        assertEquals(rowCount4, grid.getRowCount());

        collapseButton.click();

        assertEquals(rowCount0, grid.getRowCount());
    }

    @Test
    public void partialCollapse() {
        assertEquals(rowCount0, grid.getRowCount());

        selectDepth(3);
        expandButton.click();

        assertEquals(rowCount4, grid.getRowCount());

        selectDepth(1);
        collapseButton.click();

        assertEquals(rowCount0, grid.getRowCount());

        selectDepth(0);
        expandButton.click();

        assertEquals(rowCount1, grid.getRowCount());

        // Open just one subtree to see if it is still fully expanded
        grid.getExpandElement(2, 0).click();

        assertEquals(rowCount1 + rowCount2, grid.getRowCount());
    }

    private void selectDepth(int depth) {
        depthSelector.setValue(String.valueOf(depth));
    }
}
