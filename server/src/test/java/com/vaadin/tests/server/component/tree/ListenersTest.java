package com.vaadin.tests.server.component.tree;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;

public class ListenersTest extends TestCase implements ExpandListener,
        CollapseListener {
    private int expandCalled;
    private int collapseCalled;
    private Object lastExpanded;
    private Object lastCollapsed;

    @Override
    protected void setUp() {
        expandCalled = 0;
    }

    public void testExpandListener() {
        Tree tree = createTree(10, 20, false);
        tree.addListener((ExpandListener) this);
        List<Object> rootIds = new ArrayList<Object>(tree.rootItemIds());

        assertEquals(10, rootIds.size());
        assertEquals(10 + 10 * 20 + 10, tree.size());

        // Expanding should send one expand event for the root item id
        tree.expandItem(rootIds.get(0));
        assertEquals(1, expandCalled);
        assertEquals(rootIds.get(0), lastExpanded);

        // Expand should send one event for each expanded item id.
        // In this case root + child 4
        expandCalled = 0;
        tree.expandItemsRecursively(rootIds.get(1));
        assertEquals(2, expandCalled);
        List<Object> c = new ArrayList<Object>(tree.getChildren(rootIds.get(1)));

        assertEquals(c.get(4), lastExpanded);

        // Expanding an already expanded item should send no expand event
        expandCalled = 0;
        tree.expandItem(rootIds.get(0));
        assertEquals(0, expandCalled);
    }

    /**
     * Creates a tree with "rootItems" roots, each with "children" children,
     * each with 1 child.
     * 
     * @param rootItems
     * @param children
     * @param expand
     * @return
     */
    private Tree createTree(int rootItems, int children, boolean expand) {
        Tree tree = new Tree();
        for (int i = 0; i < rootItems; i++) {
            String rootId = "root " + i;
            tree.addItem(rootId);
            if (expand) {
                tree.expandItemsRecursively(rootId);
            } else {
                tree.collapseItemsRecursively(rootId);

            }
            for (int j = 0; j < children; j++) {
                String childId = "child " + i + "/" + j;
                tree.addItem(childId);
                tree.setParent(childId, rootId);
                tree.setChildrenAllowed(childId, false);
                if (j == 4) {
                    tree.setChildrenAllowed(childId, true);
                    Object grandChildId = tree.addItem();
                    tree.setParent(grandChildId, childId);
                    tree.setChildrenAllowed(grandChildId, false);
                    if (expand) {
                        tree.expandItemsRecursively(childId);
                    } else {
                        tree.collapseItemsRecursively(childId);
                    }
                }
            }
        }

        return tree;
    }

    public void testCollapseListener() {
        Tree tree = createTree(7, 15, true);
        tree.addListener((CollapseListener) this);

        List<Object> rootIds = new ArrayList<Object>(tree.rootItemIds());

        assertEquals(7, rootIds.size());
        assertEquals(7 + 7 * 15 + 7, tree.size());

        // Expanding should send one expand event for the root item id
        tree.collapseItem(rootIds.get(0));
        assertEquals(1, collapseCalled);
        assertEquals(rootIds.get(0), lastCollapsed);

        // Collapse sends one event for each collapsed node.
        // In this case root + child 4
        collapseCalled = 0;
        tree.collapseItemsRecursively(rootIds.get(1));
        assertEquals(2, collapseCalled);
        List<Object> c = new ArrayList<Object>(tree.getChildren(rootIds.get(1)));
        assertEquals(c.get(4), lastCollapsed);

        // Collapsing an already expanded item should send no expand event
        collapseCalled = 0;
        tree.collapseItem(rootIds.get(0));
        assertEquals(0, collapseCalled);
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        lastExpanded = event.getItemId();
        expandCalled++;

    }

    @Override
    public void nodeCollapse(CollapseEvent event) {
        lastCollapsed = event.getItemId();
        collapseCalled++;

    }
}
