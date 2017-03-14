package com.vaadin.tests.components.treegrid;

import org.junit.Test;

import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.TextRenderer;

public class TreeGridColumnTest {

    private TreeGrid<String> treeGrid = new TreeGrid<>();

    @Test(expected = RuntimeException.class)
    public void testChangeRendererOfHierarchyColumn() {
        treeGrid.addColumn(Object::toString).setId("foo");
        treeGrid.setHierarchyColumn("foo");
        // This should not be allowed.
        treeGrid.getColumn("foo").setRenderer(new TextRenderer());
    }
}
