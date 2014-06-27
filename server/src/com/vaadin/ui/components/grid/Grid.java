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

package com.vaadin.ui.components.grid;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.google.gwt.thirdparty.guava.common.collect.Sets.SetView;
import com.vaadin.data.Container;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.RpcDataProviderExtension.DataProviderKeyMapper;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.ui.grid.ColumnGroupRowState;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.components.grid.selection.MultiSelectionModel;
import com.vaadin.ui.components.grid.selection.NoSelectionModel;
import com.vaadin.ui.components.grid.selection.SelectionChangeEvent;
import com.vaadin.ui.components.grid.selection.SelectionChangeListener;
import com.vaadin.ui.components.grid.selection.SelectionChangeNotifier;
import com.vaadin.ui.components.grid.selection.SelectionModel;
import com.vaadin.ui.components.grid.selection.SingleSelectionModel;
import com.vaadin.util.ReflectTools;

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
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Grid extends AbstractComponent implements SelectionChangeNotifier {

    /**
     * Selection modes representing built-in {@link SelectionModel
     * SelectionModels} that come bundled with {@link Grid}.
     * <p>
     * Passing one of these enums into
     * {@link Grid#setSelectionMode(SelectionMode)} is equivalent to calling
     * {@link Grid#setSelectionModel(SelectionModel)} with one of the built-in
     * implementations of {@link SelectionModel}.
     * 
     * @see Grid#setSelectionMode(SelectionMode)
     * @see Grid#setSelectionModel(SelectionModel)
     */
    public enum SelectionMode {
        /** A SelectionMode that maps to {@link SingleSelectionModel} */
        SINGLE {
            @Override
            protected SelectionModel createModel() {
                return new SingleSelectionModel();
            }

        },

        /** A SelectionMode that maps to {@link MultiSelectionModel} */
        MULTI {
            @Override
            protected SelectionModel createModel() {
                return new MultiSelectionModel();
            }
        },

        /** A SelectionMode that maps to {@link NoSelectionModel} */
        NONE {
            @Override
            protected SelectionModel createModel() {
                return new NoSelectionModel();
            }
        };

        protected abstract SelectionModel createModel();
    }

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
                removeExtension(column.getRenderer());
            }
            datasourceExtension.propertiesRemoved(removedColumns);

            // Add new columns
            HashSet<Object> addedPropertyIds = new HashSet<Object>();
            for (Object propertyId : properties) {
                if (!columns.containsKey(propertyId)) {
                    appendColumn(propertyId);
                    addedPropertyIds.add(propertyId);
                }
            }
            datasourceExtension.propertiesAdded(addedPropertyIds);

            Object frozenPropertyId = columnKeys
                    .get(getState(false).lastFrozenColumnId);
            if (!columns.containsKey(frozenPropertyId)) {
                setLastFrozenPropertyId(null);
            }
        }
    };

    private RpcDataProviderExtension datasourceExtension;

    /**
     * The selection model that is currently in use. Never <code>null</code>
     * after the constructor has been run.
     */
    private SelectionModel selectionModel;

    /**
     * The number of times to ignore selection state sync to the client.
     * <p>
     * This usually means that the client side has modified the selection. We
     * still want to inform the listeners that the selection has changed, but we
     * don't want to send those changes "back to the client".
     */
    private int ignoreSelectionClientSync = 0;

    private static final Method SELECTION_CHANGE_METHOD = ReflectTools
            .findMethod(SelectionChangeListener.class, "selectionChange",
                    SelectionChangeEvent.class);

    /**
     * Creates a new Grid using the given datasource.
     * 
     * @param datasource
     *            the data source for the grid
     */
    public Grid(final Container.Indexed datasource) {
        setContainerDataSource(datasource);
        setSelectionMode(SelectionMode.MULTI);
        addSelectionChangeListener(new SelectionChangeListener() {
            @Override
            public void selectionChange(SelectionChangeEvent event) {
                for (Object removedItemId : event.getRemoved()) {
                    keyMapper().unpin(removedItemId);
                }

                for (Object addedItemId : event.getAdded()) {
                    keyMapper().pin(addedItemId);
                }

                List<String> keys = keyMapper().getKeys(getSelectedRows());

                boolean markAsDirty = true;

                /*
                 * If this clause is true, it means that the selection event
                 * originated from the client. This means that we don't want to
                 * send the changes back to the client (markAsDirty => false).
                 */
                if (ignoreSelectionClientSync > 0) {
                    ignoreSelectionClientSync--;
                    markAsDirty = false;

                    try {

                        /*
                         * Make sure that the diffstate is aware of the
                         * "undirty" modification, so that the diffs are
                         * calculated correctly the next time we actually want
                         * to send the selection state to the client.
                         */
                        getUI().getConnectorTracker().getDiffState(Grid.this)
                                .put("selectedKeys", new JSONArray(keys));
                    } catch (JSONException e) {
                        throw new RuntimeException("Internal error", e);
                    }
                }

                getState(markAsDirty).selectedKeys = keys;
            }
        });

        registerRpc(new GridServerRpc() {

            @Override
            public void selectionChange(List<String> selection) {
                final HashSet<Object> newSelection = new HashSet<Object>(
                        keyMapper().getItemIds(selection));
                final HashSet<Object> oldSelection = new HashSet<Object>(
                        getSelectedRows());

                SetView<Object> addedItemIds = Sets.difference(newSelection,
                        oldSelection);
                SetView<Object> removedItemIds = Sets.difference(oldSelection,
                        newSelection);

                if (!addedItemIds.isEmpty()) {
                    /*
                     * Since these changes come from the client, we want to
                     * modify the selection model and get that event fired to
                     * all the listeners. One of the listeners is our internal
                     * selection listener, and this tells it not to send the
                     * selection event back to the client.
                     */
                    ignoreSelectionClientSync++;

                    if (addedItemIds.size() == 1) {
                        select(addedItemIds.iterator().next());
                    } else {
                        assert getSelectionModel() instanceof SelectionModel.Multi : "Got multiple selections, but the selection model is not a SelectionModel.Multi";
                        ((SelectionModel.Multi) getSelectionModel())
                                .select(addedItemIds);
                    }
                }

                if (!removedItemIds.isEmpty()) {
                    /*
                     * Since these changes come from the client, we want to
                     * modify the selection model and get that event fired to
                     * all the listeners. One of the listeners is our internal
                     * selection listener, and this tells it not to send the
                     * selection event back to the client.
                     */
                    ignoreSelectionClientSync++;

                    if (removedItemIds.size() == 1) {
                        deselect(removedItemIds.iterator().next());
                    } else {
                        assert getSelectionModel() instanceof SelectionModel.Multi : "Got multiple deselections, but the selection model is not a SelectionModel.Multi";
                        ((SelectionModel.Multi) getSelectionModel())
                                .deselect(removedItemIds);
                    }
                }
            }
        });
    }

    /**
     * Sets the grid data source.
     * 
     * @param container
     *            The container data source. Cannot be null.
     * @throws IllegalArgumentException
     *             if the data source is null
     */
    public void setContainerDataSource(Container.Indexed container) {

        if (container == null) {
            throw new IllegalArgumentException(
                    "Cannot set the datasource to null");
        }
        if (datasource == container) {
            return;
        }

        // Remove old listeners
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

        /*
         * selectionModel == null when the invocation comes from the
         * constructor.
         */
        if (selectionModel != null) {
            selectionModel.reset();
        }

        // Listen to changes in properties and remove columns if needed
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .addPropertySetChangeListener(propertyListener);
        }
        /*
         * activeRowHandler will be updated by the client-side request that
         * occurs on container change - no need to actively re-insert any
         * ValueChangeListeners at this point.
         */

        getState().columns.clear();
        setLastFrozenPropertyId(null);

        // Add columns
        for (Object propertyId : datasource.getContainerPropertyIds()) {
            if (!columns.containsKey(propertyId)) {
                GridColumn column = appendColumn(propertyId);

                // Initial sorting is defined by container
                if (datasource instanceof Sortable) {
                    column.setSortable(((Sortable) datasource)
                            .getSortableContainerPropertyIds().contains(
                                    propertyId));
                }

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

    /**
     * Sets (or unsets) the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the given column will be frozen in place
     * when the grid is scrolled sideways.
     * 
     * @param lastFrozenColumn
     *            the rightmost column to freeze, or <code>null</code> to not
     *            have any columns frozen
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    void setLastFrozenColumn(GridColumn lastFrozenColumn) {
        /*
         * TODO: If and when Grid supports column reordering or insertion of
         * columns before other columns, make sure to mention that adding
         * columns before lastFrozenColumn will change the frozen column count
         */

        if (lastFrozenColumn == null) {
            getState().lastFrozenColumnId = null;
        } else if (columns.containsValue(lastFrozenColumn)) {
            getState().lastFrozenColumnId = lastFrozenColumn.getState().id;
        } else {
            throw new IllegalArgumentException(
                    "The given column isn't attached to this grid");
        }
    }

    /**
     * Sets (or unsets) the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the indicated property will be frozen in
     * place when the grid is scrolled sideways.
     * <p>
     * <em>Note:</em> If the container used by this grid supports a propertyId
     * <code>null</code>, it can never be defined as the last frozen column, as
     * a <code>null</code> parameter will always reset the frozen columns in
     * Grid.
     * 
     * @param propertyId
     *            the property id corresponding to the column that should be the
     *            last frozen column, or <code>null</code> to not have any
     *            columns frozen.
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    public void setLastFrozenPropertyId(Object propertyId) {
        final GridColumn column;
        if (propertyId == null) {
            column = null;
        } else {
            column = getColumn(propertyId);
            if (column == null) {
                throw new IllegalArgumentException(
                        "property id does not exist.");
            }
        }
        setLastFrozenColumn(column);
    }

    /**
     * Gets the rightmost frozen column in the grid.
     * <p>
     * <em>Note:</em> Most often, this method returns the very value set with
     * {@link #setLastFrozenPropertyId(Object)}. This value, however, can be
     * reset to <code>null</code> if the column is detached from this grid.
     * 
     * @return the rightmost frozen column in the grid, or <code>null</code> if
     *         no columns are frozen.
     */
    public Object getLastFrozenPropertyId() {
        return columnKeys.get(getState().lastFrozenColumnId);
    }

    /**
     * Scrolls to a certain item, using {@link ScrollDestination#ANY}.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollToItem(Object itemId) throws IllegalArgumentException {
        scrollToItem(itemId, ScrollDestination.ANY);
    }

    /**
     * Scrolls to a certain item, using user-specified scroll destination.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @param destination
     *            value specifying desired position of scrolled-to row.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollToItem(Object itemId, ScrollDestination destination)
            throws IllegalArgumentException {

        int row = datasource.indexOfId(itemId);

        if (row == -1) {
            throw new IllegalArgumentException(
                    "Item with specified ID does not exist in data source");
        }

        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToRow(row, destination);
    }

    /**
     * Scrolls to the beginning of the first data row.
     */
    public void scrollToStart() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToStart();
    }

    /**
     * Scrolls to the end of the last data row.
     */
    public void scrollToEnd() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToEnd();
    }

    /**
     * Sets the number of rows that should be visible in Grid's body, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * If Grid is currently not in {@link HeightMode#ROW}, the given value is
     * remembered, and applied once the mode is applied.
     * 
     * @param rows
     *            The height in terms of number of rows displayed in Grid's
     *            body. If Grid doesn't contain enough rows, white space is
     *            displayed instead. If <code>null</code> is given, then Grid's
     *            height is undefined
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInifinite(double)
     *             infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     */
    public void setHeightByRows(double rows) {
        if (rows <= 0.0d) {
            throw new IllegalArgumentException(
                    "More than zero rows must be shown.");
        } else if (Double.isInfinite(rows)) {
            throw new IllegalArgumentException(
                    "Grid doesn't support infinite heights");
        } else if (Double.isNaN(rows)) {
            throw new IllegalArgumentException("NaN is not a valid row count");
        }

        getState().heightByRows = rows;
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * 
     * @return the amount of rows that are being shown in Grid's body
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return getState(false).heightByRows;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em>Note:</em> This method will change the widget's size in the browser
     * only if {@link #getHeightMode()} returns {@link HeightMode#CSS}.
     * 
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit);
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via a {@code setHeight}-method, and behave as a traditional Component.
     * <p>
     * If {@link HeightMode#ROW} is given, Grid will make sure that the body
     * will display as many rows as {@link #getHeightByRows()} defines.
     * <em>Note:</em> If headers/footers are inserted or removed, the widget
     * will resize itself to still display the required amount of rows in its
     * body. It also takes the horizontal scrollbar into account.
     * 
     * @param heightMode
     *            the mode in to which Grid should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight an setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         * 
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        getState().heightMode = heightMode;
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     * 
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return getState(false).heightMode;
    }

    /* Selection related methods: */

    /**
     * Takes a new {@link SelectionModel} into use.
     * <p>
     * The SelectionModel that is previously in use will have all its items
     * deselected.
     * <p>
     * If the given SelectionModel is already in use, this method does nothing.
     * 
     * @param selectionModel
     *            the new SelectionModel to use
     * @throws IllegalArgumentException
     *             if {@code selectionModel} is <code>null</code>
     */
    public void setSelectionModel(SelectionModel selectionModel)
            throws IllegalArgumentException {
        if (selectionModel == null) {
            throw new IllegalArgumentException(
                    "Selection model may not be null");
        }

        if (this.selectionModel != selectionModel) {
            // this.selectionModel is null on init
            if (this.selectionModel != null) {
                this.selectionModel.reset();
                this.selectionModel.setGrid(null);
            }

            this.selectionModel = selectionModel;
            this.selectionModel.setGrid(this);
            this.selectionModel.reset();
        }
    }

    /**
     * Returns the currently used {@link SelectionModel}.
     * 
     * @return the currently used SelectionModel
     */
    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Changes the Grid's selection mode.
     * <p>
     * Grid supports three selection modes: multiselect, single select and no
     * selection, and this is a conveniency method for choosing between one of
     * them.
     * <P>
     * Technically, this method is a shortcut that can be used instead of
     * calling {@code setSelectionModel} with a specific SelectionModel
     * instance. Grid comes with three built-in SelectionModel classes, and the
     * {@link SelectionMode} enum represents each of them.
     * <p>
     * Essentially, the two following method calls are equivalent:
     * <p>
     * <code><pre>
     * grid.setSelectionMode(SelectionMode.MULTI);
     * grid.setSelectionModel(new MultiSelectionMode());
     * </pre></code>
     * 
     * 
     * @param selectionMode
     *            the selection mode to switch to
     * @return The {@link SelectionModel} instance that was taken into use
     * @throws IllegalArgumentException
     *             if {@code selectionMode} is <code>null</code>
     * @see SelectionModel
     */
    public SelectionModel setSelectionMode(final SelectionMode selectionMode)
            throws IllegalArgumentException {
        if (selectionMode == null) {
            throw new IllegalArgumentException("selection mode may not be null");
        }
        final SelectionModel newSelectionModel = selectionMode.createModel();
        setSelectionModel(newSelectionModel);
        return newSelectionModel;
    }

    /**
     * Checks whether an item is selected or not.
     * 
     * @param itemId
     *            the item id to check for
     * @return <code>true</code> iff the item is selected
     */
    // keep this javadoc in sync with SelectionModel.isSelected
    public boolean isSelected(Object itemId) {
        return selectionModel.isSelected(itemId);
    }

    /**
     * Returns a collection of all the currently selected itemIds.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}.
     * 
     * @return a collection of all the currently selected itemIds
     */
    // keep this javadoc in sync with SelectionModel.getSelectedRows
    public Collection<Object> getSelectedRows() {
        return getSelectionModel().getSelectedRows();
    }

    /**
     * Gets the item id of the currently selected item.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}. Only
     * {@link SelectionModel.Single} is supported.
     * 
     * @return the item id of the currently selected item, or <code>null</code>
     *         if nothing is selected
     * @throws IllegalStateException
     *             if the object that is returned by
     *             {@link #getSelectionModel()} is not an instance of
     *             {@link SelectionModel.Single}
     */
    // keep this javadoc in sync with SelectionModel.Single.getSelectedRow
    public Object getSelectedRow() throws IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).getSelectedRow();
        } else {
            throw new IllegalStateException(Grid.class.getSimpleName()
                    + " does not support the 'getSelectedRow' shortcut method "
                    + "unless the selection model implements "
                    + SelectionModel.Single.class.getName()
                    + ". The current one does not ("
                    + selectionModel.getClass().getName() + ")");
        }
    }

    /**
     * Marks an item as selected.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}. Only
     * {@link SelectionModel.Single} or {@link SelectionModel.Multi} are
     * supported.
     * 
     * 
     * @param itemIds
     *            the itemId to mark as selected
     * @return <code>true</code> if the selection state changed.
     *         <code>false</code> if the itemId already was selected
     * @throws IllegalArgumentException
     *             if the {@code itemId} doesn't exist in the currently active
     *             Container
     * @throws IllegalStateException
     *             if the selection was illegal. One such reason might be that
     *             the implementation already had an item selected, and that
     *             needs to be explicitly deselected before re-selecting
     *             something
     * @throws IllegalStateException
     *             if the object that is returned by
     *             {@link #getSelectionModel()} does not implement
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    // keep this javadoc in sync with SelectionModel.Single.select
    public boolean select(Object itemId) throws IllegalArgumentException,
            IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).select(itemId);
        } else if (selectionModel instanceof SelectionModel.Multi) {
            return ((SelectionModel.Multi) selectionModel).select(itemId);
        } else {
            throw new IllegalStateException(Grid.class.getSimpleName()
                    + " does not support the 'select' shortcut method "
                    + "unless the selection model implements "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + ". The current one does not ("
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Marks an item as deselected.
     * <p>
     * This method is a shorthand that is forwarded to the object that is
     * returned by {@link #getSelectionModel()}. Only
     * {@link SelectionModel.Single} and {@link SelectionModel.Multi} are
     * supported.
     * 
     * @param itemId
     *            the itemId to remove from being selected
     * @return <code>true</code> if the selection state changed.
     *         <code>false</code> if the itemId already was selected
     * @throws IllegalArgumentException
     *             if the {@code itemId} doesn't exist in the currently active
     *             Container
     * @throws IllegalStateException
     *             if the deselection was illegal. One such reason might be that
     *             the implementation already had an item selected, and that
     *             needs to be explicitly deselected before re-selecting
     *             something
     * @throws IllegalStateException
     *             if the object that is returned by
     *             {@link #getSelectionModel()} does not implement
     *             {@link SelectionModel.Single} or {@link SelectionModel.Multi}
     */
    // keep this javadoc in sync with SelectionModel.Single.deselect
    public boolean deselect(Object itemId) throws IllegalStateException {
        if (selectionModel instanceof SelectionModel.Single) {
            return ((SelectionModel.Single) selectionModel).deselect(itemId);
        } else if (selectionModel instanceof SelectionModel.Multi) {
            return ((SelectionModel.Multi) selectionModel).deselect(itemId);
        } else {
            throw new IllegalStateException(Grid.class.getSimpleName()
                    + " does not support the 'deselect' shortcut method "
                    + "unless the selection model implements "
                    + SelectionModel.Single.class.getName() + " or "
                    + SelectionModel.Multi.class.getName()
                    + ". The current one does not ("
                    + selectionModel.getClass().getName() + ").");
        }
    }

    /**
     * Fires a selection change event.
     * <p>
     * <strong>Note:</strong> This is not a method that should be called by
     * application logic. This method is publicly accessible only so that
     * {@link SelectionModel SelectionModels} would be able to inform Grid of
     * these events.
     * 
     * @param addedSelections
     *            the selections that were added by this event
     * @param removedSelections
     *            the selections that were removed by this event
     */
    public void fireSelectionChangeEvent(Collection<Object> oldSelection,
            Collection<Object> newSelection) {
        fireEvent(new SelectionChangeEvent(this, oldSelection, newSelection));
    }

    @Override
    public void addSelectionChangeListener(SelectionChangeListener listener) {
        addListener(SelectionChangeEvent.class, listener,
                SELECTION_CHANGE_METHOD);
    }

    @Override
    public void removeSelectionChangeListener(SelectionChangeListener listener) {
        removeListener(SelectionChangeEvent.class, listener,
                SELECTION_CHANGE_METHOD);
    }

    /**
     * A shortcut for
     * <code>{@link #datasourceExtension}.{@link com.vaadin.data.RpcDataProviderExtension#getKeyMapper() getKeyMapper()}</code>
     */
    private DataProviderKeyMapper keyMapper() {
        return datasourceExtension.getKeyMapper();
    }

    /**
     * Adds a renderer to this grid's connector hierarchy.
     * 
     * @param renderer
     *            the renderer to add
     */
    void addRenderer(Renderer<?> renderer) {
        addExtension(renderer);
    }
}
