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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.Tree;

public class SelectItemAfterRemove extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree();

        tree.setImmediate(true);
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {

                tree.removeItem(tree.getValue());
                tree.select(event.getItemId());
            }
        });

        tree.addItem("first");
        tree.addItem("second");
        tree.addItem("third");

        tree.select("first");

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on an item should select the clicked item and remove "
                + "the previously selected item.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15181;
    }
}
