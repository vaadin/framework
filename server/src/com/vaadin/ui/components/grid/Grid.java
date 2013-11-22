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

package com.vaadin.ui.components.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.ui.grid.ColumnGroupRowState;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.AbstractComponent;

/**
 * Data grid component
 * 
 * <h3>Lazy loading</h3> TODO To be revised when the data data source
 * implementation has been don.
 * 
 * <h3>Columns</h3> The grid columns are based on the property ids of the
 * underlying data source. Each property id represents one column in the grid.
 * To retrive a column in the grid you can use {@link Grid#getColumn(Object)}
 * with the property id of the column. A grid column contains properties like
 * the width, the footer and header captions of the column.
 * 
 * <h3>Auxiliary headers and footers</h3> TODO To be revised when column
 * grouping is implemented.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class Grid extends AbstractComponent {

    /**
     * The data source attached to the grid
     */
    private Container.Indexed datasource;

    /**
     * Property id to column instance mapping
     */
    private final Map<Object, GridColumn> columns = new HashMap<Object, GridColumn>();

    /**
     * Key generator for column server-to-client communication
     */
    private final KeyMapper<Object> columnKeys = new KeyMapper<Object>();

    /**
     * The column groups added to the grid
     */
    private final List<ColumnGroupRow> columnGroupRows = new ArrayList<ColumnGroupRow>();

    /**
     * Property listener for listening to changes in data source properties.
     */
    private final PropertySetChangeListener propertyListener = new PropertySetChangeListener() {

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            Collection<?> properties = new HashSet<Object>(event.getContainer()
                    .getContainerPropertyIds());

            // Cleanup columns that are no longer in grid
            List<Object> removedColumns = new LinkedList<Object>();
            for (Object columnId : columns.keySet()) {
                if (!properties.contains(columnId)) {
                    removedColumns.add(columnId);
                }
            }
            for (Object columnId : removedColumns) {
                GridColumn column = columns.remove(columnId);
                columnKeys.remove(columnId);
                getState().columns.remove(column.getState());
            }

            // Add new columns
            for (Object propertyId : properties) {
                if (!columns.containsKey(propertyId)) {
                    appendColumn(propertyId);
                }
            }
        }
    };

    private RpcDataProviderExtension datasourceExtension;

    /**
     * Creates a new Grid using the given datasource.
     * 
     * @param datasource
     *            the data source for the grid
     */
    public Grid(Container.Indexed datasource) {
        setContainerDatasource(datasource);
    }

    /**
     * Sets the grid data source.
     * 
     * @param container
     *            The container data source. Cannot be null.
     * @throws IllegalArgumentException
     *             if the data source is null
     */
    public void setContainerDatasource(Container.Indexed container) {
        if (container == null) {
            throw new IllegalArgumentException(
                    "Cannot set the datasource to null");
        }
        if (datasource == container) {
            return;
        }

        // Remove old listener
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .removePropertySetChangeListener(propertyListener);
        }

        if (datasourceExtension != null) {
            removeExtension(datasourceExtension);
        }

        datasource = container;
        datasourceExtension = new RpcDataProviderExtension(container);
        datasourceExtension.extend(this);

        // Listen to changes in properties and remove columns if needed
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .addPropertySetChangeListener(propertyListener);
        }

        getState().columns.clear();

        // Add columns
        for (Object propertyId : datasource.getContainerPropertyIds()) {
            if (!columns.containsKey(propertyId)) {
                GridColumn column = appendColumn(propertyId);

                // Add by default property id as column header
                column.setHeaderCaption(String.valueOf(propertyId));
            }
        }

    }

    /**
     * Returns the grid data source.
     * 
     * @return the container data source of the grid
     */
    public Container.Indexed getContainerDatasource() {
        return datasource;
    }

    /**
     * Returns a column based on the property id
     * 
     * @param propertyId
     *            the property id of the column
     * @return the column or <code>null</code> if not found
     */
    public GridColumn getColumn(Object propertyId) {
        return columns.get(propertyId);
    }

    /**
     * Sets the header rows visible.
     * 
     * @param visible
     *            <code>true</code> if the header rows should be visible
     */
    public void setColumnHeadersVisible(boolean visible) {
        getState().columnHeadersVisible = visible;
    }

    /**
     * Are the header rows visible?
     * 
     * @return <code>true</code> if the headers of the columns are visible
     */
    public boolean isColumnHeadersVisible() {
        return getState(false).columnHeadersVisible;
    }

    /**
     * Sets the footer rows visible.
     * 
     * @param visible
     *            <code>true</code> if the footer rows should be visible
     */
    public void setColumnFootersVisible(boolean visible) {
        getState().columnFootersVisible = visible;
    }

    /**
     * Are the footer rows visible.
     * 
     * @return <code>true</code> if the footer rows should be visible
     */
    public boolean isColumnFootersVisible() {
        return getState(false).columnFootersVisible;
    }

    /**
     * <p>
     * Adds a new column group to the grid.
     * 
     * <p>
     * Column group rows are rendered in the header and footer of the grid.
     * Column group rows are made up of column groups which groups together
     * columns for adding a common auxiliary header or footer for the columns.
     * </p>
     * </p>
     * 
     * <p>
     * Example usage:
     * 
     * <pre>
     * // Add a new column group row to the grid
     * ColumnGroupRow row = grid.addColumnGroupRow();
     * 
     * // Group &quot;Column1&quot; and &quot;Column2&quot; together to form a header in the row
     * ColumnGroup column12 = row.addGroup(&quot;Column1&quot;, &quot;Column2&quot;);
     * 
     * // Set a common header for &quot;Column1&quot; and &quot;Column2&quot;
     * column12.setHeader(&quot;Column 1&amp;2&quot;);
     * </pre>
     * 
     * </p>
     * 
     * @return a column group instance you can use to add column groups
     */
    public ColumnGroupRow addColumnGroupRow() {
        ColumnGroupRowState state = new ColumnGroupRowState();
        ColumnGroupRow row = new ColumnGroupRow(this, state, columnKeys);
        columnGroupRows.add(row);
        getState().columnGroupRows.add(state);
        return row;
    }

    /**
     * Adds a new column group to the grid at a specific index
     * 
     * @param rowIndex
     *            the index of the row
     * @return a column group instance you can use to add column groups
     */
    public ColumnGroupRow addColumnGroupRow(int rowIndex) {
        ColumnGroupRowState state = new ColumnGroupRowState();
        ColumnGroupRow row = new ColumnGroupRow(this, state, columnKeys);
        columnGroupRows.add(rowIndex, row);
        getState().columnGroupRows.add(rowIndex, state);
        return row;
    }

    /**
     * Removes a column group.
     * 
     * @param row
     *            the row to remove
     */
    public void removeColumnGroupRow(ColumnGroupRow row) {
        columnGroupRows.remove(row);
        getState().columnGroupRows.remove(row.getState());
    }

    /**
     * Gets the column group rows.
     * 
     * @return an unmodifiable list of column group rows
     */
    public List<ColumnGroupRow> getColumnGroupRows() {
        return Collections.unmodifiableList(new ArrayList<ColumnGroupRow>(
                columnGroupRows));
    }

    /**
     * Used internally by the {@link Grid} to get a {@link GridColumn} by
     * referencing its generated state id. Also used by {@link GridColumn} to
     * verify if it has been detached from the {@link Grid}.
     * 
     * @param columnId
     *            the client id generated for the column when the column is
     *            added to the grid
     * @return the column with the id or <code>null</code> if not found
     */
    GridColumn getColumnByColumnId(String columnId) {
        Object propertyId = getPropertyIdByColumnId(columnId);
        return getColumn(propertyId);
    }

    /**
     * Used internally by the {@link Grid} to get a property id by referencing
     * the columns generated state id.
     * 
     * @param columnId
     *            The state id of the column
     * @return The column instance or null if not found
     */
    Object getPropertyIdByColumnId(String columnId) {
        return columnKeys.get(columnId);
    }

    @Override
    protected GridState getState() {
        return (GridState) super.getState();
    }

    @Override
    protected GridState getState(boolean markAsDirty) {
        return (GridState) super.getState(markAsDirty);
    }

    /**
     * Creates a new column based on a property id and appends it as the last
     * column.
     * 
     * @param datasourcePropertyId
     *            The property id of a property in the datasource
     */
    private GridColumn appendColumn(Object datasourcePropertyId) {
        if (datasourcePropertyId == null) {
            throw new IllegalArgumentException("Property id cannot be null");
        }
        assert datasource.getContainerPropertyIds().contains(
                datasourcePropertyId) : "Datasource should contain the property id";

        GridColumnState columnState = new GridColumnState();
        columnState.id = columnKeys.key(datasourcePropertyId);
        getState().columns.add(columnState);

        GridColumn column = new GridColumn(this, columnState);
        columns.put(datasourcePropertyId, column);

        return column;
    }
}
