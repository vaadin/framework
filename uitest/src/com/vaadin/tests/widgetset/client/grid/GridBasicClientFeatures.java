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
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.Grid.SelectionMode;
import com.vaadin.client.ui.grid.GridColumn;
import com.vaadin.client.ui.grid.Renderer;
import com.vaadin.client.ui.grid.datasources.ListDataSource;
import com.vaadin.client.ui.grid.renderers.DateRenderer;
import com.vaadin.client.ui.grid.renderers.HtmlRenderer;
import com.vaadin.client.ui.grid.renderers.NumberRenderer;
import com.vaadin.client.ui.grid.renderers.TextRenderer;
import com.vaadin.tests.widgetset.client.grid.GridBasicClientFeatures.Data;

/**
 * Grid basic client features test application.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridBasicClientFeatures extends
        PureGWTTestApplication<Grid<List<Data>>> {

    public static enum Renderers {
        TEXT_RENDERER, HTML_RENDERER, NUMBER_RENDERER, DATE_RENDERER;
    }

    private static final int MANUALLY_FORMATTED_COLUMNS = 4;
    public static final int COLUMNS = 11;
    public static final int ROWS = 1000;

    private final Grid<List<Data>> grid;
    private final List<List<Data>> data;
    private final ListDataSource<List<Data>> ds;

    /**
     * Our basic data object
     */
    public final static class Data {
        Object value;
    }

    /**
     * Convenience method for creating a list of Data objects to be used as a
     * Row in the data source
     * 
     * @param cols
     *            number of columns (items) to include in the row
     * @return
     */
    private List<Data> createDataRow(int cols) {
        List<Data> list = new ArrayList<Data>(cols);
        for (int i = 0; i < cols; ++i) {
            list.add(new Data());
        }
        data.add(list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public GridBasicClientFeatures() {
        super(new Grid<List<Data>>());

        // Initialize data source
        data = new ArrayList<List<Data>>();
        {
            Random rand = new Random();
            rand.setSeed(13334);
            long timestamp = 0;
            for (int row = 0; row < ROWS; row++) {

                List<Data> datarow = createDataRow(COLUMNS);
                Data d;

                int col = 0;
                for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; ++col) {
                    d = datarow.get(col);
                    d.value = "(" + row + ", " + col + ")";
                }

                d = datarow.get(col++);
                d.value = Integer.valueOf(row);

                d = datarow.get(col++);
                d.value = new Date(timestamp);
                timestamp += 91250000; // a bit over a day, just to get
                                       // variation

                d = datarow.get(col++);
                d.value = "<b>" + row + "</b>";

                d = datarow.get(col++);
                d.value = Integer.valueOf(rand.nextInt());
            }
        }

        ds = new ListDataSource<List<Data>>(data);
        grid = getTestedWidget();
        grid.setDataSource(ds);
        grid.setSelectionMode(SelectionMode.NONE);

        // Create a bunch of grid columns

        // Data source layout:
        // text (String) * (COLUMNS - MANUALLY_FORMATTED_COLUMNS + 1) |
        // rownumber (Integer) | some date (Date) | row number as HTML (String)
        // | random value (Integer)

        int col = 0;

        // Text times COLUMNS - MANUALLY_FORMATTED_COLUMNS
        for (col = 0; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; ++col) {

            final int c = col;

            grid.addColumn(new GridColumn<String, List<Data>>(
                    createRenderer(Renderers.TEXT_RENDERER)) {
                @Override
                public String getValue(List<Data> row) {
                    return (String) row.get(c).value;
                }
            });

        }

        // Integer row number
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            });
        }

        // Some date
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Date, List<Data>>(
                    createRenderer(Renderers.DATE_RENDERER)) {
                @Override
                public Date getValue(List<Data> row) {
                    return (Date) row.get(c).value;
                }
            });
        }

        // Row number as a HTML string
        {
            final int c = col++;
            grid.addColumn(new GridColumn<String, List<Data>>(
                    createRenderer(Renderers.HTML_RENDERER)) {
                @Override
                public String getValue(List<Data> row) {
                    return (String) row.get(c).value;
                }
            });
        }

        // Random integer value
        {
            final int c = col++;
            grid.addColumn(new GridColumn<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            });
        }

        //
        // Populate the menu
        //

        addMenuCommand("multi", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.MULTI);
            }
        }, "Component", "State", "Selection mode");

        addMenuCommand("single", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.SINGLE);
            }
        }, "Component", "State", "Selection mode");

        addMenuCommand("none", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.NONE);
            }
        }, "Component", "State", "Selection mode");

        grid.getElement().getStyle().setZIndex(0);
        add(grid);
    }

    /**
     * Creates a a renderer for a {@link Renderers}
     */
    @SuppressWarnings("rawtypes")
    private final Renderer createRenderer(Renderers renderer) {
        switch (renderer) {
        case TEXT_RENDERER:
            return new TextRenderer();

        case HTML_RENDERER:
            return new HtmlRenderer() {

                @Override
                public void render(FlyweightCell cell, String htmlString) {
                    super.render(cell, "<b>" + htmlString + "</b>");
                }
            };

        case NUMBER_RENDERER:
            return new NumberRenderer<Integer>();

        case DATE_RENDERER:
            return new DateRenderer();

        default:
            return new TextRenderer();
        }
    }
}
