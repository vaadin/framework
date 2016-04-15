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
package com.vaadin.tests.components.grid;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

public class GridSingleColumn extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        IndexedContainer indexedContainer = new IndexedContainer();
        indexedContainer.addContainerProperty("column1", String.class, "");

        for (int i = 0; i < 100; i++) {
            Item addItem = indexedContainer.addItem(i);
            addItem.getItemProperty("column1").setValue("cell");
        }

        Grid grid = new Grid(indexedContainer);
        grid.setSelectionMode(SelectionMode.NONE);

        Column column = grid.getColumn("column1");

        column.setHeaderCaption("Header");

        addComponent(grid);
        grid.scrollTo(grid.getContainerDataSource().getIdByIndex(50));
    }

    @Override
    protected String getTestDescription() {
        return "Tests a single column grid";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
