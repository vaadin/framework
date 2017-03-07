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

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.Tree;

public class TreeScrolling extends AbstractTestCase {

    @Override
    public void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        LegacyWindow w = new LegacyWindow("", layout);
        setMainWindow(w);

        TextField filler1 = new TextField();
        RichTextArea filler2 = new RichTextArea();
        Tree tree = new Tree();
        for (int i = 0; i < 20; i++) {
            String parentId = "Item " + i;
            // Item parentItem =
            tree.addItem(parentId);
            for (int j = 0; j < 20; j++) {
                String subId = "Item " + i + " - " + j;
                // Item subItem =
                tree.addItem(subId);
                tree.setParent(subId, parentId);
            }

        }

        for (Object id : tree.rootItemIds()) {
            tree.expandItemsRecursively(id);
        }

        layout.addComponent(filler1);
        layout.addComponent(filler2);
        layout.addComponent(tree);
    }

    @Override
    protected String getDescription() {
        return "Tests what happens when a tree is partly out of view when an item is selected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5400;
    }

}
