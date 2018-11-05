package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void collapse_disabled_mode_change_with_expanded_rows() {
        // Assert first row expands
        assertExpandRow(0);

        // Assert second row expands and collapses
        assertExpandRow(1);
        assertCollapseRow(1);

        selectMenuPath("Component", "Features", "Collapse allowed",
                "depth 1 disabled");

        assertTrue("First row should still be expanded",
                grid.isRowExpanded(0, 0));

        // Assert second row expands but does not collapse
        assertExpandRow(1);
        assertCollapseRowDisabled(1);

        // Assert first row still collapses
        assertCollapseRow(0);
    }

    private void assertExpandRow(int row) {
        assertFalse(grid.isRowExpanded(row, 0));
        grid.expandWithClick(row);
        assertTrue(grid.isRowExpanded(row, 0));
    }

    private void assertCollapseRow(int row) {
        assertTrue("Row not expanded", grid.isRowExpanded(row, 0));
        grid.collapseWithClick(row);
        assertFalse("Row did not collapse", grid.isRowExpanded(row, 0));
    }

    private void assertCollapseRowDisabled(int row) {
        assertTrue("Row not expanded", grid.isRowExpanded(row, 0));
        grid.collapseWithClick(row);
        assertTrue("Row should not collapse", grid.isRowExpanded(row, 0));
    }
}
