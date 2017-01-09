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

package com.vaadin.shared.ui.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.AbstractSingleSelectState;
import elemental.json.JsonArray;

/**
 * The shared state for the {@link com.vaadin.ui.Grid} component.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class GridState extends AbstractSingleSelectState {

    /**
     * The default value for height-by-rows for both GWT widgets
     * {@link com.vaadin.client.widgets.Grid} and
     * {@link com.vaadin.client.widgets.Escalator Escalator}.
     */
    public static final double DEFAULT_HEIGHT_BY_ROWS = 10.0d;

    /**
     * The key in which a row's data can be found.
     *
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, JsonArray)
     */
    public static final String JSONKEY_DATA = "d";

    /**
     * The key in which a row's own key can be found.
     *
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, JsonArray)
     */
    public static final String JSONKEY_ROWKEY = "k";

    /**
     * The key in which a row's generated style can be found.
     *
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, JsonArray)
     */
    public static final String JSONKEY_ROWSTYLE = "rs";

    /**
     * The key in which a generated styles for a row's cells can be found.
     *
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, JsonArray)
     */
    public static final String JSONKEY_CELLSTYLES = "cs";

    /**
     * The key in which a row's description can be found.
     *
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, JsonArray)
     */
    public static final String JSONKEY_ROWDESCRIPTION = "rd";

    /**
     * The key in which a cell's description can be found.
     *
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int, JsonArray)
     */
    public static final String JSONKEY_CELLDESCRIPTION = "cd";

    /**
     * The key that tells whether details are visible for the row.
     *
     * @see com.vaadin.ui.Grid#setDetailsGenerator(com.vaadin.ui.components.grid.DetailsGenerator)
     * @see com.vaadin.ui.Grid#setDetailsVisible(Object, boolean)
     * @see com.vaadin.shared.data.DataProviderRpc#setRowData(int,
     *      elemental.json.JsonArray)
     */
    public static final String JSONKEY_DETAILS_VISIBLE = "dv";

    /**
     * The key that tells whether row is selected or not.
     */
    public static final String JSONKEY_SELECTED = "s";

    {
        primaryStyleName = "v-grid";
    }

    /**
     * Column resize mode in grid.
     * 
     * @since 7.7.5
     */
    public ColumnResizeMode columnResizeMode = ColumnResizeMode.ANIMATED;

    /** The state of the header section. */
    public SectionState header = new SectionState();

    /** The state of the footer section. */
    public SectionState footer = new SectionState();

    /**
     * Column order in grid.
     */
    public List<String> columnOrder = new ArrayList<>();

    /** The number of frozen columns. */
    @DelegateToWidget
    public int frozenColumnCount = 0;

    /** The height of the Grid in terms of body rows. */
    @DelegateToWidget
    public double heightByRows = DEFAULT_HEIGHT_BY_ROWS;

    /** The mode by which Grid defines its height. */
    @DelegateToWidget
    public HeightMode heightMode = HeightMode.CSS;

    /** Keys of the currently sorted columns. */
    public String[] sortColumns = new String[0];

    /** Directions for each sorted column. */
    public SortDirection[] sortDirs = new SortDirection[0];

    /**
     * Whether rows and/or cells have generated descriptions (tooltips).
     */
    public boolean hasDescriptions;

    /** Whether the columns can be reordered. */
    @DelegateToWidget
    public boolean columnReorderingAllowed;

}
