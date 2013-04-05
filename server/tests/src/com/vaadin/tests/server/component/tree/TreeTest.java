package com.vaadin.tests.server.component.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Tree;

public class TreeTest {

    private Tree tree;
    private Tree tree2;
    private Tree tree3;
    private Tree tree4;

    @Before
    public void setUp() {
        tree = new Tree();
        tree.addItem("parent");
        tree.addItem("child");
        tree.setChildrenAllowed("parent", true);
        tree.setParent("child", "parent");

        tree2 = new Tree("Caption");
        tree2.addItem("parent");
        tree2.addItem("child");
        tree2.setChildrenAllowed("parent", true);
        tree2.setParent("child", "parent");

        tree3 = new Tree("Caption", null);
        tree3.addItem("parent");
        tree3.addItem("child");
        tree3.setChildrenAllowed("parent", true);
        tree3.setParent("child", "parent");

        tree4 = new Tree("Caption", new IndexedContainer());
        tree4.addItem("parent");
        tree4.addItem("child");
        tree4.setChildrenAllowed("parent", true);
        tree4.setParent("child", "parent");
    }

    @Test
    public void testRemoveChildren() {
        assertTrue(tree.hasChildren("parent"));
        tree.removeItem("child");
        assertFalse(tree.hasChildren("parent"));

        assertTrue(tree2.hasChildren("parent"));
        tree2.removeItem("child");
        assertFalse(tree2.hasChildren("parent"));

        assertTrue(tree3.hasChildren("parent"));
        tree3.removeItem("child");
        assertFalse(tree3.hasChildren("parent"));

        assertTrue(tree4.hasChildren("parent"));
        tree4.removeItem("child");
        assertFalse(tree4.hasChildren("parent"));
    }

    @Test
    public void testContainerTypeIsHierarchical() {
        assertTrue(HierarchicalContainer.class.isAssignableFrom(tree
                .getContainerDataSource().getClass()));
        assertTrue(HierarchicalContainer.class.isAssignableFrom(tree2
                .getContainerDataSource().getClass()));
        assertTrue(HierarchicalContainer.class.isAssignableFrom(tree3
                .getContainerDataSource().getClass()));
        assertFalse(HierarchicalContainer.class.isAssignableFrom(tree4
                .getContainerDataSource().getClass()));
        assertTrue(Container.Hierarchical.class.isAssignableFrom(tree4
                .getContainerDataSource().getClass()));
    }

    @Test
    public void testRemoveExpandedItems() throws Exception {
        tree.expandItem("parent");
        tree.expandItem("child");

        Field expandedField = tree.getClass().getDeclaredField("expanded");
        Field expandedItemIdField = tree.getClass().getDeclaredField(
                "expandedItemId");

        expandedField.setAccessible(true);
        expandedItemIdField.setAccessible(true);

        HashSet<Object> expanded = (HashSet<Object>) expandedField.get(tree);
        Object expandedItemId = expandedItemIdField.get(tree);

        assertEquals(2, expanded.size());
        assertTrue("Contains parent", expanded.contains("parent"));
        assertTrue("Contains child", expanded.contains("child"));
        assertEquals("child", expandedItemId);

        tree.removeItem("parent");

        expanded = (HashSet<Object>) expandedField.get(tree);
        expandedItemId = expandedItemIdField.get(tree);

        assertEquals(1, expanded.size());
        assertTrue("Contains child", expanded.contains("child"));
        assertEquals("child", expandedItemId);

        tree.removeItem("child");

        expanded = (HashSet<Object>) expandedField.get(tree);
        expandedItemId = expandedItemIdField.get(tree);

        assertEquals(0, expanded.size());
        assertNull(expandedItemId);
    }

    @Test
    public void testRemoveExpandedItemsOnContainerChange() throws Exception {
        tree.expandItem("parent");
        tree.expandItem("child");

        tree.setContainerDataSource(new HierarchicalContainer());

        Field expandedField = tree.getClass().getDeclaredField("expanded");
        Field expandedItemIdField = tree.getClass().getDeclaredField(
                "expandedItemId");

        expandedField.setAccessible(true);
        expandedItemIdField.setAccessible(true);

        HashSet<Object> expanded = (HashSet<Object>) expandedField.get(tree);
        assertEquals(0, expanded.size());

        Object expandedItemId = expandedItemIdField.get(tree);
        assertNull(expandedItemId);
    }

}
