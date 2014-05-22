/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

/**
 * The shared state for the {@link com.vaadin.ui.components.grid.Grid} component
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class GridState extends AbstractComponentState {

    /**
     * The default value for height-by-rows for both GWT widgets
     * {@link com.vaadin.ui.components.grid Grid} and
     * {@link com.vaadin.client.ui.grid.Escalator Escalator}
     */
    public static final double DEFAULT_HEIGHT_BY_ROWS = 10.0d;

    {
        // FIXME Grid currently does not support undefined size
        width = "400px";
        height = "400px";
    }

    /**
     * Columns in grid. Column order implicitly deferred from list order.
     */
    public List<GridColumnState> columns = new ArrayList<GridColumnState>();

    /**
     * Is the column header row visible
     */
    public boolean columnHeadersVisible = true;

    /**
     * Is the column footer row visible
     */
    public boolean columnFootersVisible = false;

    /**
     * The column groups added to the grid
     */
    public List<ColumnGroupRowState> columnGroupRows = new ArrayList<ColumnGroupRowState>();

    /**
     * The id for the last frozen column.
     * 
     * @see GridColumnState#id
     */
    public String lastFrozenColumnId = null;

    /** The height of the Grid in terms of body rows. */
    // @DelegateToWidget
    /*
     * Annotation doesn't work because of http://dev.vaadin.com/ticket/12900.
     * Remove manual code from Connector once fixed
     */
    public double heightByRows = DEFAULT_HEIGHT_BY_ROWS;

    /** The mode by which Grid defines its height. */
    // @DelegateToWidget
    /*
     * Annotation doesn't work because of http://dev.vaadin.com/ticket/12900.
     * Remove manual code from Connector once fixed
     */
    public HeightMode heightMode = HeightMode.CSS;

    /** FIXME remove once selection mode communcation is done. only for testing. */
    @DelegateToWidget
    public boolean selectionCheckboxes;

}
