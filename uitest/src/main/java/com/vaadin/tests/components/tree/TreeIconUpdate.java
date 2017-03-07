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
package com.vaadin.tests.components.tree;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Tree;

public class TreeIconUpdate extends TestBase {

    private static final Resource ICON1 = new ThemeResource(
            "../runo/icons/16/folder.png");
    private static final Resource ICON2 = new ThemeResource(
            "../runo/icons/16/ok.png");

    @Override
    protected void setup() {
        HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("icon", Resource.class, null);
        final Tree tree = new Tree();
        tree.setContainerDataSource(container);
        tree.setItemIconPropertyId("icon");
        tree.setItemCaptionPropertyId("name");

        for (int i = 0; i < 20; i++) {
            Item bar = container.addItem("bar" + i);
            bar.getItemProperty("name").setValue("Bar" + i);
            bar.getItemProperty("icon").setValue(ICON1);

            if (i > 3) {
                container.setParent("bar" + i, "bar" + (i - 1));
            }
        }

        addComponent(tree);

        Button button = new Button("Change icon", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tree.getItem("bar0").getItemProperty("icon").setValue(ICON2);
            }
        });

        addComponent(button);
        button = new Button("Change caption", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tree.getItem("bar0").getItemProperty("name").setValue("foo");
            }
        });

        addComponent(button);

    }

    @Override
    protected String getDescription() {
        return "Click the button to change the icon. The tree should be updated";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9663;
    }

}
