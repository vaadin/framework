package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.TextRenderer;

public class TreeGridTest {

    private TreeGrid<String> treeGrid = new TreeGrid<>();
    private boolean expandEventFired = false;
    private boolean collapseEventFired = false;

    @Test
    public void testChangeRendererOfHierarchyColumn() {
        treeGrid.addColumn(Object::toString).setId("foo");
        treeGrid.setHierarchyColumn("foo");
        // This should be allowed.
        treeGrid.getColumn("foo").setRenderer(new TextRenderer());
    }

    @Test
    public void testExpandAndCollapseEvents() {
        TreeData<String> treeData = new TreeData<>();
        treeData.addItem(null, "Foo");
        treeData.addItem("Foo", "Bar");
        treeData.addItem("Foo", "Baz");
        treeGrid.setDataProvider(new TreeDataProvider<>(treeData));

        treeGrid.addExpandListener(event -> expandEventFired = true);
        treeGrid.addCollapseListener(event -> collapseEventFired = true);

        // Test expand event
        assertFalse(expandEventFired);
        treeGrid.expand("Foo");
        assertTrue("Item not expanded", treeGrid.isExpanded("Foo"));
        assertTrue("Expand event not fired", expandEventFired);

        // Test collapse event
        assertFalse(collapseEventFired);
        treeGrid.collapse("Foo");
        assertFalse("Item not collapsed", treeGrid.isExpanded("Foo"));
        assertTrue("Collapse event not fired", collapseEventFired);
    }

    @Test
    public void testSetAndGetHierarchyColumn() {
        Column<String, String> column = treeGrid.addColumn(Object::toString)
                .setId("foo");
        treeGrid.setHierarchyColumn("foo");
        assertEquals("Hierarchy column was not correctly returned", column,
                treeGrid.getHierarchyColumn());
    }

}
