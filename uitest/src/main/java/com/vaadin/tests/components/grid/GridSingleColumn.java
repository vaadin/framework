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

import java.util.stream.IntStream;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

public class GridSingleColumn extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.NONE);

        grid.setItems(IntStream.range(0, 100).mapToObj(indx -> "cell"));

        Column<String, String> column = grid.addColumn(ValueProvider.identity())
                .setCaption("Header");

        addComponent(grid);
        grid.scrollTo(50);
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
