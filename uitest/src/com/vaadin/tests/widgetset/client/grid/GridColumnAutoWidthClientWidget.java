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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Grid.SelectionMode;
import com.vaadin.client.ui.grid.GridColumn;
import com.vaadin.client.ui.grid.datasources.ListDataSource;
import com.vaadin.client.ui.grid.renderers.HtmlRenderer;

public class GridColumnAutoWidthClientWidget extends
        PureGWTTestApplication<Grid<List<String>>> {

    private Grid<List<String>> grid;

    private class Col extends GridColumn<String, List<String>> {
        public Col(String header) {
            super(header, new HtmlRenderer());
        }

        @Override
        public String getValue(List<String> row) {
            int index = grid.getColumns().indexOf(this);
            return "<span>" + String.valueOf(row.get(index)) + "</span>";
        }
    }

    protected GridColumnAutoWidthClientWidget() {
        super(new Grid<List<String>>());
        grid = getTestedWidget();
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setWidth("700px");

        List<List<String>> list = new ArrayList<List<String>>();
        list.add(Arrays.asList("equal length", "a very long cell content",
                "short", "fixed width narrow", "fixed width wide"));
        grid.setDataSource(new ListDataSource<List<String>>(list));

        addColumn("equal length");
        addColumn("short");
        addColumn("a very long header content");
        addColumn("fixed width narrow").setWidth(50);
        addColumn("fixed width wide").setWidth(200);

        addNorth(grid, 400);
    }

    private Col addColumn(String header) {
        Col column = (Col) grid.addColumn(new Col(header));
        grid.getHeaderRow(0).getCell(column)
                .setHtml("<span>" + header + "</span>");
        return column;
    }
}
