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
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.Tree;

public class TreeItemClickListening extends AbstractTestUIWithLog {

    private int clickCounter = 0;

    @Override
    protected void setup(VaadinRequest request) {

        Tree tree = new Tree();
        tree.setImmediate(true);

        tree.addContainerProperty("caption", String.class, "");
        for (int i = 1; i <= 2; i++) {
            String item = "Node " + i;
            tree.addItem(item);
            tree.getContainerProperty(item, "caption").setValue("Caption " + i);
            tree.setChildrenAllowed(item, false);
        }
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_PROPERTY);
        tree.setItemCaptionPropertyId("caption");

        tree.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                clickCounter++;
                switch (event.getButton()) {
                case LEFT:
                    log.log("Left Click");
                    break;
                case RIGHT:
                    log.log("Right Click");
                    break;
                }
            }
        });

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Item click event should be triggered from all mouse button clicks";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6845;
    }
}
