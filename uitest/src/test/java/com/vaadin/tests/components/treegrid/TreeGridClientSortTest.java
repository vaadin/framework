package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridClientSortTest extends SingleBrowserTest {

    @Override
    public Class<?> getUIClass() {
        return TreeGridBasicFeatures.class;
    }

    @Test
    public void client_sorting_with_collapse_and_expand() {
        openTestURL();
        TreeGridElement grid = $(TreeGridElement.class).first();
        selectMenuPath("Component", "Features", "Set data provider",
                "TreeDataProvider");
        grid.getHeaderCell(0, 0).doubleClick();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.collapseWithClick(0);
        grid.expandWithClick(0);
        assertEquals("0 | 2", grid.getCell(0, 0).getText());
        assertEquals("1 | 2", grid.getCell(1, 0).getText());
        assertEquals("2 | 2", grid.getCell(2, 0).getText());
    }
}
