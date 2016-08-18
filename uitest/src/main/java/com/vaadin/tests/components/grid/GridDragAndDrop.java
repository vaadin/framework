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
package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.Grid;

@SuppressWarnings("serial")
public class GridDragAndDrop extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        List<String> columnIds = Arrays.asList("Hello", "this", "are",
                "multiple", "columns", "plus", "these", "resemble", "a",
                "group", "here", "no", "more");

        Grid grid = new Grid();

        for (String columnId : columnIds) {
            grid.addColumn(columnId);
        }

        for (int i = 0; i < 100; i++) {
            grid.addRow(columnIds.toArray());
        }

        grid.setColumnReorderingAllowed(true);

        grid.setFrozenColumnCount(1);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        addComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Start dragging a column header and move left and right.<br> The drop indicator should appear exactly on the lines between column headers.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18925;
    }
}
