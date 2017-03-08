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

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Tree;

public class TreeNodeCaptionWrapping extends TestBase {

    @Override
    protected String getDescription() {
        return "The text should not wrap to the following line but instead be cut off when there is too little horizontal space.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3098;
    }

    @Override
    protected void setup() {
        setTheme("runo");
        Tree tree = new Tree();
        tree.setWidth("100px");

        tree.addItem("1");
        tree.setItemIcon("1", new ThemeResource("../runo/icons/16/ok.png"));

        String mainItem = "A very long item that should not wrap";
        String subItem = "Subitem - also long";

        tree.addItem(mainItem);
        tree.setItemIcon(mainItem,
                new ThemeResource("../runo/icons/16/error.png"));

        tree.addItem(subItem);
        tree.setParent(subItem, mainItem);

        tree.expandItem("1");
        tree.expandItem(mainItem);
        tree.expandItem(subItem);

        addComponent(tree);
    }
}
