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
package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.client.widgets.Grid.SelectionMode;
import com.vaadin.shared.ui.grid.HeightMode;

public class GridDefaultTextRendererWidget extends
        PureGWTTestApplication<Grid<String>> {
    /*
     * This can't be null, because grid thinks that a row object of null means
     * "data is still being fetched".
     */
    private static final String NULL_STRING = "";

    private Grid<String> grid;

    public GridDefaultTextRendererWidget() {
        super(new Grid<String>());
        grid = getTestedWidget();

        grid.setDataSource(new ListDataSource<String>(NULL_STRING, "string"));
        grid.addColumn(new Column<String, String>() {
            @Override
            public String getValue(String row) {
                if (!NULL_STRING.equals(row)) {
                    return row;
                } else {
                    return null;
                }
            }
        });

        grid.addColumn(new Column<String, String>() {

            @Override
            public String getValue(String row) {
                return "foo";
            }

        });

        grid.setHeightByRows(2);
        grid.setHeightMode(HeightMode.ROW);
        grid.setSelectionMode(SelectionMode.NONE);
        addNorth(grid, 500);
    }
}
