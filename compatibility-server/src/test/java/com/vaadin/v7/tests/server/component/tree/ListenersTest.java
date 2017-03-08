/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.tests.server.component.tree;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.Tree.CollapseEvent;
import com.vaadin.v7.ui.Tree.CollapseListener;
import com.vaadin.v7.ui.Tree.ExpandEvent;
import com.vaadin.v7.ui.Tree.ExpandListener;

public class ListenersTest implements ExpandListener, CollapseListener {
    private int expandCalled;
    private int collapseCalled;
    private Object lastExpanded;
    private Object lastCollapsed;

    @Before
    public void setUp() {
        expandCalled = 0;
    }

    @Test
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
        List<Object> c = new ArrayList<Object>(
                tree.getChildren(rootIds.get(1)));

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

    @Test
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
        List<Object> c = new ArrayList<Object>(
                tree.getChildren(rootIds.get(1)));
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
