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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.Tree;

public class TreeItemDoubleClick extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree("Immediate With ItemClickListener");
        tree.setImmediate(true);
        tree.setNullSelectionAllowed(false);

        for (int i = 1; i < 6; i++) {
            tree.addItem("Tree Item " + i);
        }

        ItemClickEvent.ItemClickListener listener = new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    log.log("Double Click " + event.getItemId());
                }
            }
        };

        tree.addItemClickListener(listener);

        addComponent(tree);

        addButton("Change immediate flag", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                // this wouldn't work if tree had a value change listener
                tree.setImmediate(!tree.isImmediate());
                log.log("tree.isImmediate() is now " + tree.isImmediate());
            }

        });

    }

    @Override
    protected String getTestDescription() {
        return "Tests that double click is fired";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14745;
    }

}
