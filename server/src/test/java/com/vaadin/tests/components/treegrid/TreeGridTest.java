package com.vaadin.tests.components.treegrid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.TextRenderer;

public class TreeGridTest {

    private TreeGrid<String> treeGrid = new TreeGrid<>();
    private boolean expandEventFired = false;
    private boolean collapseEventFired = false;

    @Test(expected = IllegalStateException.class)
    public void testChangeRendererOfHierarchyColumn() {
        treeGrid.addColumn(Object::toString).setId("foo");
        treeGrid.setHierarchyColumn("foo");
        // This should not be allowed.
        treeGrid.getColumn("foo").setRenderer(new TextRenderer());
    }

    @Test
    public void testExpandAndCollapseEvents() {
        TreeData<String> treeData = new TreeData<>();
        treeData.addItem(null, "Foo");
        treeData.addItem("Foo", "Bar");
        treeData.addItem("Foo", "Baz");
        treeGrid.setDataProvider(
                new TreeDataProvider<>(treeData));

        treeGrid.addExpandListener(e -> expandEventFired = true);
        treeGrid.addCollapseListener(e -> collapseEventFired = true);

        // Test expand event
        Assert.assertFalse(expandEventFired);
        treeGrid.expand("Foo");
        Assert.assertTrue("Expand event not fired", expandEventFired);

        // Test collapse event
        Assert.assertFalse(collapseEventFired);
        treeGrid.collapse("Foo");
        Assert.assertTrue("Collapse event not fired", collapseEventFired);
    }
}
