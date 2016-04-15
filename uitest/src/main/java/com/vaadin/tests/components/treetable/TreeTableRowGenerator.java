/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.components.treetable;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class TreeTableRowGenerator extends AbstractTestUI {
    public static final String COLUMN_A = "first";
    public static final String COLUMN_B = "second";

    @Override
    protected void setup(VaadinRequest request) {
        TreeTable treeTable = new TreeTable();

        final HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addContainerProperty(COLUMN_A, String.class, "");
        hierarchicalContainer.addContainerProperty(COLUMN_B, String.class, "");

        Item it = hierarchicalContainer.addItem(0);
        it.getItemProperty(COLUMN_A).setValue("row 1 column a");
        it.getItemProperty(COLUMN_B).setValue("row 1 column b");
        hierarchicalContainer.setChildrenAllowed(0, true);

        Item it2 = hierarchicalContainer.addItem(1);
        it2.getItemProperty(COLUMN_A).setValue("row 2 column a");
        it2.getItemProperty(COLUMN_B).setValue("row 2 column b");
        hierarchicalContainer.setChildrenAllowed(1, false);

        hierarchicalContainer.setParent(1, 0);

        treeTable.setRowGenerator(new Table.RowGenerator() {
            @Override
            public Table.GeneratedRow generateRow(Table table, Object itemId) {
                if (table instanceof TreeTable
                        && ((TreeTable) table).areChildrenAllowed(itemId)) {
                    return new Table.GeneratedRow("Spanned Row");
                } else {
                    return null;
                }
            }
        });

        treeTable.setContainerDataSource(hierarchicalContainer);
        treeTable.setSizeFull();
        addComponent(treeTable);
    }
}
