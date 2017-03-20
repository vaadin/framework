package com.vaadin.tests.components.treegrid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridCollapseDisabledTest extends SingleBrowserTest {

    private TreeGridElement grid;

    @Override
    protected Class<?> getUIClass() {
        return TreeGridBasicFeatures.class;
    }

    @Before
    public void before() {
        openTestURL();
        grid = $(TreeGridElement.class).first();
    }

    @Test
    public void collapse_disabled_for_all() {
        selectMenuPath("Component", "Features", "Collapse allowed",
                "all disabled");

        // Assert first and second row can be expanded, but not collapsed
        assertExpandRow(0);
        assertCollapseRowDisabled(0);

        assertExpandRow(1);
        assertCollapseRowDisabled(1);
    }

    @Test
    public void collapse_disabled_for_depth0() {
        selectMenuPath("Component", "Features", "Collapse allowed",
                "depth 0 disabled");

        // Assert first row expands
        assertExpandRow(0);

        // Assert second row expands and collapses
        assertExpandRow(1);
        assertCollapseRow(1);

        // Assert first row does not collapse
        assertCollapseRowDisabled(0);
    }

    @Test
    public void collapse_disabled_for_depth1() {
        selectMenuPath("Component", "Features", "Collapse allowed",
                "depth 1 disabled");

        // Assert first row expands
        assertExpandRow(0);

        // Assert second row expands but does not collapse
        assertExpandRow(1);
        assertCollapseRowDisabled(1);

        // Assert first row still collapses
        assertCollapseRow(0);
    }

    /**
     * The collapse disabled uses really similar key for data communication as
     * the cell description in Grid. This test is in place to make sure no one
     * changes it to be conflicting
     */
    @Test
    public void json_for_cell_description_not_overridden() {
        selectMenuPath("Component", "Features", "Collapse allowed",
                "depth 0 disabled");

        // Test that cell description works
        grid.getCell(0, 1).showTooltip();
        Assert.assertEquals("No description tooltip shown",
                "Hierarchy depth: 0", getTooltipElement().getText());

        // Test collapse disabled
        assertExpandRow(0);
        assertCollapseRowDisabled(0);

        // Test collapse enabled
        assertExpandRow(1);
        assertCollapseRow(1);
    }

    private void assertExpandRow(int row) {
        Assert.assertFalse(grid.isRowExpanded(row, 0));
        grid.expandWithClick(row);
        Assert.assertTrue(grid.isRowExpanded(row, 0));
    }

    private void assertCollapseRow(int row) {
        Assert.assertTrue("Row not expanded", grid.isRowExpanded(row, 0));
        grid.collapseWithClick(row);
        Assert.assertFalse("Row did not collapse", grid.isRowExpanded(row, 0));
    }

    private void assertCollapseRowDisabled(int row) {
        Assert.assertTrue("Row not expanded", grid.isRowExpanded(row, 0));
        grid.collapseWithClick(row);
        Assert.assertTrue("Row should not collapse",
                grid.isRowExpanded(row, 0));
    }
}
