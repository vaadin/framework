package com.vaadin.tests.components;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.event.CollapseEvent;
import com.vaadin.event.CollapseEvent.CollapseListener;
import com.vaadin.event.ExpandEvent;
import com.vaadin.event.ExpandEvent.ExpandListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Tree;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreeTest {

    public static final String TEST_CAPTION = "test caption";
    public static final String TEST_DESCRIPTION = "test description";
    public static final String TEST_RESOURCE_ID = "nothing.gif";

    private static class TreeCollapseExpandListener
            implements ExpandListener<String>, CollapseListener<String> {

        private boolean collapsed = false;
        private boolean expanded = false;
        private final Tree<String> tree;

        public TreeCollapseExpandListener(Tree<String> tree) {
            this.tree = tree;
        }

        @Override
        public void itemCollapse(CollapseEvent<String> event) {
            assertEquals("Source component was incorrect", tree,
                    event.getComponent());
            Assert.assertFalse("Multiple collapse events", collapsed);
            collapsed = true;
        }

        @Override
        public void itemExpand(ExpandEvent<String> event) {
            assertEquals("Source component was incorrect", tree,
                    event.getComponent());
            Assert.assertFalse("Multiple expand events", expanded);
            expanded = true;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public boolean isCollapsed() {
            return collapsed;
        }
    }

    @Test
    public void event_source_is_tree() {
        Tree<String> tree = new Tree<>();
        TreeData<String> treeData = new TreeData<>();
        treeData.addItem(null, "Foo");
        treeData.addItem("Foo", "Bar");
        treeData.addItem("Foo", "Baz");
        tree.setDataProvider(new TreeDataProvider<>(treeData));

        TreeCollapseExpandListener listener = new TreeCollapseExpandListener(
                tree);
        tree.addExpandListener(listener);
        tree.addCollapseListener(listener);

        Assert.assertFalse(listener.isExpanded());
        tree.expand("Foo");
        assertTrue("Item not expanded", tree.isExpanded("Foo"));
        assertTrue("Expand event not fired", listener.isExpanded());
        Assert.assertFalse(listener.isCollapsed());
        tree.collapse("Foo");
        Assert.assertFalse("Item not collapsed", tree.isExpanded("Foo"));
        assertTrue("Collapse event not fired", listener.isCollapsed());
    }

    @Test
    public void testComponentProperties() {
        Tree<String> tree = new Tree<>();
        tree.setCaption(TEST_CAPTION);
        tree.setDescription(TEST_DESCRIPTION);
        tree.setIcon(new ThemeResource(TEST_RESOURCE_ID));

        assertEquals(TEST_CAPTION,tree.getCaption());
        assertEquals(TEST_DESCRIPTION,tree.getDescription());
        assertEquals(TEST_RESOURCE_ID,tree.getIcon().toString());

        assertFalse(tree.isCaptionAsHtml());
        tree.setCaptionAsHtml(true);
        assertTrue(tree.isCaptionAsHtml());
    }
}
