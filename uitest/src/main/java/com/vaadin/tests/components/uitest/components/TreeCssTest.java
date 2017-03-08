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
package com.vaadin.tests.components.uitest.components;

import com.vaadin.event.Action;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Tree;

public class TreeCssTest {
    private int debugIdCounter = 0;

    public TreeCssTest(TestSampler parent) {
        // Actions for the context menu
        final Action ACTION_ADD = new Action("Add child item");
        final Action ACTION_DELETE = new Action("Delete");
        final Action[] ACTIONS = new Action[] { ACTION_ADD, ACTION_DELETE };

        final Tree tree = new Tree();
        tree.setId("tree" + debugIdCounter++);

        HierarchicalContainer hc = createHierarchicalContainer();

        tree.setContainerDataSource(hc);

        tree.addActionHandler(new Action.Handler() {

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                // We don't care about functionality, we just want the UI for
                // testing..

            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                // TODO Auto-generated method stub
                return ACTIONS;
            }
        });

        // Expand whole tree
        for (Object id : tree.rootItemIds()) {
            tree.expandItemsRecursively(id);
        }

        parent.addComponent(tree);
    }

    private HierarchicalContainer createHierarchicalContainer() {
        String[] itemNames = new String[] { "Foo", "Baar" };

        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty("NAME", String.class, null);

        for (String parentId : itemNames) {
            Item parent = hc.addItem(parentId);
            parent.getItemProperty("NAME").setValue(parentId);
            hc.setChildrenAllowed(parent, true);
            for (int i = 0; i < 5; i++) {
                String childId = parentId + i;
                Item child = hc.addItem(childId);
                child.getItemProperty("NAME").setValue(childId);
                if (!hc.setParent(childId, parentId)) {
                    System.out.println("Unable to set parent \"" + parentId
                            + "\" for child with id: \"" + childId + "\"");
                }
            }
        }
        return hc;
    }

}
