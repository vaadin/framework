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
package com.vaadin.tests.components;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Tree;

public class HierarchicalContainerSorting extends TestBase {
    IndexedContainer hierarchicalContainer = new HierarchicalContainer();

    IndexedContainer indexedContainer = new IndexedContainer();

    @Override
    public void setup() {

        populateContainer(indexedContainer);
        populateContainer(hierarchicalContainer);

        sort(indexedContainer);
        sort(hierarchicalContainer);

        HorizontalLayout hl = new HorizontalLayout();

        Tree tree1 = new Tree("Tree with IndexedContainer");
        tree1.setContainerDataSource(indexedContainer);
        tree1.setItemCaptionPropertyId("name");
        hl.addComponent(tree1);

        Tree tree2 = new Tree("Tree with HierarchicalContainer");
        tree2.setContainerDataSource(hierarchicalContainer);
        tree2.setItemCaptionPropertyId("name");
        for (Object id : tree2.rootItemIds()) {
            tree2.expandItemsRecursively(id);
        }
        hl.addComponent(tree2);

        addComponent(hl);
    }

    private static void sort(IndexedContainer container) {
        Object[] properties = new Object[1];
        properties[0] = "name";

        boolean[] ascending = new boolean[1];
        ascending[0] = true;

        container.sort(properties, ascending);
    }

    private static void populateContainer(IndexedContainer container) {
        container.addContainerProperty("name", String.class, null);

        addItem(container, "Games", null);
        addItem(container, "Call of Duty", "Games");
        addItem(container, "Might and Magic", "Games");
        addItem(container, "Fallout", "Games");
        addItem(container, "Red Alert", "Games");

        addItem(container, "Cars", null);
        addItem(container, "Toyota", "Cars");
        addItem(container, "Volvo", "Cars");
        addItem(container, "Audi", "Cars");
        addItem(container, "Ford", "Cars");

        addItem(container, "Natural languages", null);
        addItem(container, "Swedish", "Natural languages");
        addItem(container, "English", "Natural languages");
        addItem(container, "Finnish", "Natural languages");

        addItem(container, "Programming languages", null);
        addItem(container, "C++", "Programming languages");
        addItem(container, "PHP", "Programming languages");
        addItem(container, "Java", "Programming languages");
        addItem(container, "Python", "Programming languages");

    }

    private static int index = 0;
    private static Map<String, Integer> nameToId = new HashMap<>();

    public static void addItem(IndexedContainer container, String string,
            String parent) {
        nameToId.put(string, index);
        Item item = container.addItem(index);
        item.getItemProperty("name").setValue(string);

        if (parent != null && container instanceof HierarchicalContainer) {
            ((HierarchicalContainer) container).setParent(index,
                    nameToId.get(parent));
        }

        index++;
    }

    @Override
    protected String getDescription() {
        return "The two trees contain the same data, one uses IndexedContainer, one uses HierarchicalContainer. Both should be sorted, both the root nodes and the children.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3095;
    }

}
