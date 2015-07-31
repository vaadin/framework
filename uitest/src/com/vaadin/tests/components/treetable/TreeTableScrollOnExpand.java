/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TreeTable;

public class TreeTableScrollOnExpand extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeTable t = new TreeTable();
        t.setSelectable(true);
        t.setImmediate(true);
        t.setSizeFull();
        t.addContainerProperty("Name", String.class, "null");
        for (int i = 1; i <= 100; i++) {
            String parentID = "Item " + i;
            Object parent = t.addItem(new Object[] { parentID }, parentID);
            String childID = "Item " + (100 + i);
            Object child = t.addItem(new Object[] { childID }, childID);
            t.getContainerDataSource().setParent(childID, parentID);
        }
        addComponent(t);
    }

    @Override
    public Integer getTicketNumber() {
        return 18247;
    }

    @Override
    public String getTestDescription() {
        return "After selecting an item and scrolling it out of view, TreeTable should not scroll to the "
                + "selected item when expanding an item.";
    }
}