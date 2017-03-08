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

import com.vaadin.event.MouseEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.Tree;

/**
 * Test for #12618: Trying to select item with right click in Tree causes focus
 * issues.
 */
@SuppressWarnings("serial")
public class TreeScrollingOnRightClick extends AbstractReindeerTestUI {

    public static final String TREE_ID = "my-tree";

    @Override
    protected void setup(VaadinRequest request) {
        final Tree tree = new Tree();
        tree.setId(TREE_ID);
        tree.setSizeUndefined();

        // Add item click listener for right click selection
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getButton() == MouseEvents.ClickEvent.BUTTON_RIGHT) {
                    tree.select(event.getItemId());
                }
            }
        });

        // Add some items
        for (int i = 0; i < 200; i++) {
            tree.addItem(String.format("Node %s", i));
        }

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Right clicking on items should not scroll Tree.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12618;
    }

}
