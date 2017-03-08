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

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Tree;

@SuppressWarnings("serial")
public class PreselectedTreeVisible extends TestBase {

    @Override
    protected void setup() {

        String itemId1 = "Item 1";
        String itemId2 = "Item 2";

        Tree tree = new Tree();

        tree.addItem(itemId1);
        tree.addItem(itemId2);

        // Removing this line causes the tree to show normally in Firefox
        tree.select(itemId1);
        addComponent(tree);

    }

    @Override
    protected String getDescription() {
        return "Tree should be visible when a item has been selected.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5396;
    }

}
