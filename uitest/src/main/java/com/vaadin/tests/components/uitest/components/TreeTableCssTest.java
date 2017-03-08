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

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableCssTest {
    private int debugIdCounter = 0;

    public TreeTableCssTest(TestSampler parent) {
        TreeTable treeTable = new TreeTable();
        treeTable.setId("treetable" + debugIdCounter++);
        treeTable.setWidth("100%");
        parent.addComponent(treeTable);

        HierarchicalContainer hc = createHierarchicalContainer();

        treeTable.setContainerDataSource(hc);

        for (Object itemId : treeTable.getItemIds()) {
            treeTable.setCollapsed(itemId, false);
        }
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
