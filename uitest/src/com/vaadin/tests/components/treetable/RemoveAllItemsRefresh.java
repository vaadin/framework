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
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

public class RemoveAllItemsRefresh extends TestBase {
    protected static final String NAME_PROPERTY = "Name";
    protected static final String TITLE_PROPERTY = "Title";

    private VerticalLayout treeLayout = new VerticalLayout();
    private Table treetable;

    private HierarchicalContainer treeContainer;

    @Override
    protected void setup() {
        treetable = new TreeTable();
        treeContainer = new HierarchicalContainer();
        // Create the treetable
        treetable.setSelectable(true);
        treetable.setSizeFull();

        treeContainer.addContainerProperty(NAME_PROPERTY, String.class, "");
        treeContainer.addContainerProperty(TITLE_PROPERTY, String.class, "");
        treetable.setContainerDataSource(treeContainer);

        treeLayout.addComponent(treetable);
        addComponent(treeLayout);

        Button cleanUp = new Button("clear");
        cleanUp.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                treeContainer.removeAllItems();
            }
        });
        addComponent(cleanUp);

        Button refresh = new Button("fill");
        refresh.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                fill();
            }
        });
        addComponent(refresh);

        fill();
    }

    private void fill() {
        Item containerItem;

        treeLayout.removeAllComponents();
        treeLayout.addComponent(treetable);

        treeContainer.removeAllItems();
        containerItem = treeContainer.addItem("first");
        containerItem.getItemProperty(NAME_PROPERTY)
                .setValue("1 NAME_PROPERTY");
        containerItem.getItemProperty(TITLE_PROPERTY).setValue(
                "1 TITLE_PROPERTY");

        containerItem = treeContainer.addItem("second");
        containerItem.getItemProperty(NAME_PROPERTY)
                .setValue("2 NAME_PROPERTY");
        containerItem.getItemProperty(TITLE_PROPERTY).setValue(
                "2 TITLE_PROPERTY");
        treetable.setContainerDataSource(treeContainer);
    }

    @Override
    protected String getDescription() {
        return "Removing all items from a treetable should refresh the component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7720;
    }
}
