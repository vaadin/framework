package com.vaadin.tests.server.component.tree;

import junit.framework.TestCase;

import com.vaadin.ui.Tree;

public class TestHasChildren extends TestCase {

    private Tree tree;

    @Override
    protected void setUp() {
        tree = new Tree();
        tree.addItem("parent");
        tree.addItem("child");
        tree.setChildrenAllowed("parent", true);
        tree.setParent("child", "parent");
    }

    public void testRemoveChildren() {
        assertTrue(tree.hasChildren("parent"));
        tree.removeItem("child");
        assertFalse(tree.hasChildren("parent"));
    }
}
