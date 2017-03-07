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
package com.vaadin.tests.widgetset.client.v7.grid;

import java.util.Arrays;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.v7.client.widget.grid.datasources.ListDataSource;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.Column;
import com.vaadin.v7.shared.ui.grid.HeightMode;

public class GridHeightByRowOnInitWidget extends Composite {
    private final SimplePanel panel = new SimplePanel();
    private final Grid<String> grid = new Grid<>();

    public GridHeightByRowOnInitWidget() {
        initWidget(panel);

        panel.setWidget(grid);
        grid.setDataSource(
                new ListDataSource<>(Arrays.asList("A", "B", "C", "D", "E")));
        grid.addColumn(new Column<String, String>("letter") {
            @Override
            public String getValue(String row) {
                return row;
            }
        });

        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(5.0d);
    }
}
