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
package com.vaadin.tests.components.treetable;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table.RowHeaderMode;
import com.vaadin.v7.ui.TreeTable;

/**
 * Test UI for RowHeaderMode.ICON_ONLY in TreeTable.
 *
 * @author Vaadin Ltd
 */
public class TreeTableRowHeaderMode extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Resource icon = new ThemeResource("../runo/icons/16/ok.png");

        TreeTable tree = new TreeTable();
        tree.addContainerProperty("Name", String.class, "");
        tree.setRowHeaderMode(RowHeaderMode.ICON_ONLY);

        Object item = tree.addItem(new Object[] { "name" }, null);
        tree.setItemIcon(item, icon);

        tree.setCollapsed(item, false);

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "RowHeaderMode.ICON_ONLY shouldn't create an empty column in TreeTable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14799;
    }

}
