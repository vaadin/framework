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

@SuppressWarnings("serial")
public class TreeItemSelectionWithoutImmediate extends AbstractTestUIWithLog {

    protected static final String TREE_ID = "TreeId";

    protected static final String MENU_ITEM_TEMPLATE = "Menu Item %d";

    @Override
    protected void setup(VaadinRequest request) {
        Tree tree = new Tree("With ItemClickListener not Immediate");
        tree.setId(TREE_ID);
        tree.setImmediate(false);

        for (int i = 1; i <= 4; i++) {
            tree.addItem(String.format(MENU_ITEM_TEMPLATE, i));
        }

        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                log("ItemClickEvent = " + event.getItemId());
            }
        });

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "Test for ensuring that selection of tree items works correctly if immediate == false "
                + "and ItemClickListener is added to Tree";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14388;
    }
}
