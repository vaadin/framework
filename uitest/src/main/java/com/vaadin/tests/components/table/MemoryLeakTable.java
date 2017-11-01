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
package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

/**
 * Test UI Class for testing memory leak in table (#14159).
 *
 * @since
 * @author Vaadin Ltd
 */
public class MemoryLeakTable extends AbstractReindeerTestUI {
    Button btnAdd = new Button("Add rows");
    Button btnRemove = new Button("Remove rows");
    Button btnTenTimes = new Button("Do ten times");
    Table tbl = new Table();
    static final int COLS = 15;
    static final int ROWS = 2000;

    private void addRows() {
        IndexedContainer idx = new IndexedContainer();
        for (int i = 0; i < COLS; i++) {
            idx.addContainerProperty("name " + i, String.class, "value");
        }
        for (int i = 0; i < ROWS; i++) {
            idx.addItem("item" + i);
        }
        tbl.setContainerDataSource(idx);
        addComponent(tbl);
    }

    private void removeRows() {
        tbl.removeAllItems();
        removeComponent(tbl);
    }

    @Override
    protected void setup(VaadinRequest request) {
        btnAdd.addClickListener(event -> addRows());
        btnRemove.addClickListener(event -> removeRows());
        addComponent(btnAdd);
        addComponent(btnRemove);
    }

    @Override
    protected String getTestDescription() {
        return "Generates table for memory leaking test";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14159;
    }

}
