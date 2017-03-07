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
package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableModifyAndSetCollapsed extends TestBase {

    int counter = 1;

    @Override
    protected void setup() {
        final HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("value", String.class, "");

        final TreeTable tt = new TreeTable(null, container);
        tt.setSizeFull();
        int parentId = counter++;
        Item parent = container.addItem(parentId);
        tt.setCollapsed(parentId, false);
        parent.getItemProperty("value").setValue("parent " + (counter++) + "");
        addComponent(tt);
        Button repopulate = new Button("Repopulate", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                // repopulate the container and expand root item
                container.removeAllItems();
                int parentId = counter++;
                Item parent = container.addItem(parentId);
                tt.setCollapsed(parentId, false);
                parent.getItemProperty("value")
                        .setValue("parent " + (counter++) + "");
                for (int i = 0; i < 4; i++) {
                    int childId = counter++;
                    Item child = container.addItem(childId);
                    child.getItemProperty("value").setValue(childId + "");
                    container.setParent(childId, parentId);
                }
            }
        });
        addComponent(repopulate);
    }

    @Override
    protected String getDescription() {
        return "Modifying a container and using setCollapsed on the same server round-trip should not cause any problems";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7785);
    }
}
