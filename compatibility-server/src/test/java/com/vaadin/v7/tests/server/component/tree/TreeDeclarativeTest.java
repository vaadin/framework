package com.vaadin.v7.tests.server.component.tree;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.TreeDragMode;

/**
 * Tests the declarative support for implementations of {@link Tree}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TreeDeclarativeTest extends DeclarativeTestBase<Tree> {

    @Test
    public void testDragMode() {
        String design = "<vaadin7-tree drag-mode='node' />";

        Tree tree = new Tree();
        tree.setDragMode(TreeDragMode.NODE);

        testRead(design, tree);
        testWrite(design, tree);
    }

    @Test
    public void testEmpty() {
        testRead("<vaadin7-tree />", new Tree());
        testWrite("<vaadin7-tree />", new Tree());
    }

    @Test
    public void testNodes() {
        String design = "<vaadin7-tree>" //
                + "  <node text='Node'/>" //
                + "  <node text='Parent'>" //
                + "    <node text='Child'>" //
                + "      <node text='Grandchild'/>" //
                + "    </node>" //
                + "  </node>" //
                + "  <node text='With icon' icon='http://example.com/icon.png'/>" //
                + "</vaadin7-tree>";

        Tree tree = new Tree();

        tree.addItem("Node");

        tree.addItem("Parent");

        tree.addItem("Child");
        tree.setParent("Child", "Parent");

        tree.addItem("Grandchild");
        tree.setParent("Grandchild", "Child");

        tree.addItem("With icon");
        tree.setItemIcon("With icon",
                new ExternalResource("http://example.com/icon.png"));

        testRead(design, tree);
        testWrite(design, tree, true);
    }
}
