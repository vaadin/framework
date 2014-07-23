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

package com.vaadin.client.ui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.data.RpcDataSourceConnector.RpcDataSource;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.grid.GridHeader.HeaderRow;
import com.vaadin.client.ui.grid.GridStaticSection.StaticRow;
import com.vaadin.client.ui.grid.renderers.AbstractRendererConnector;
import com.vaadin.client.ui.grid.selection.AbstractRowHandleSelectionModel;
import com.vaadin.client.ui.grid.selection.SelectionChangeEvent;
import com.vaadin.client.ui.grid.selection.SelectionChangeHandler;
import com.vaadin.client.ui.grid.selection.SelectionModelMulti;
import com.vaadin.client.ui.grid.selection.SelectionModelNone;
import com.vaadin.client.ui.grid.selection.SelectionModelSingle;
import com.vaadin.client.ui.grid.sort.SortEvent;
import com.vaadin.client.ui.grid.sort.SortEventHandler;
import com.vaadin.client.ui.grid.sort.SortOrder;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.GridState.SharedSelectionMode;
import com.vaadin.shared.ui.grid.GridStaticSectionState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.CellState;
import com.vaadin.shared.ui.grid.GridStaticSectionState.RowState;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.ui.grid.SortDirection;

/**
 * Connects the client side {@link Grid} widget with the server side
 * {@link com.vaadin.ui.components.grid.Grid} component.
 * <p>
 * The Grid is typed to JSONObject. The structure of the JSONObject is described
 * at {@link com.vaadin.shared.data.DataProviderRpc#setRowData(int, List)
 * DataProviderRpc.setRowData(int, List)}.
 * 
 * @since
 * @author Vaadin Ltd
 */
@Connect(com.vaadin.ui.components.grid.Grid.class)
public class GridConnector extends AbstractComponentConnector {

    /**
     * Custom implementation of the custom grid column using a JSONObject to
     * represent the cell value and String as a column type.
     */
    private class CustomGridColumn extends GridColumn<Object, JSONObject> {

        private final String id;

        private AbstractRendererConnector<Object> rendererConnector;

        public CustomGridColumn(String id,
                AbstractRendererConnector<Object> rendererConnector) {
            super(rendererConnector.getRenderer());
            this.rendererConnector = rendererConnector;
            this.id = id;
        }

        @Override
        public Object getValue(final JSONObject obj) {
            final JSONValue rowData = obj.get(GridState.JSONKEY_DATA);
            final JSONArray rowDataArray = rowData.isArray();
            assert rowDataArray != null : "Was unable to parse JSON into an array: "
                    + rowData;

            final int columnIndex = resolveCurrentIndexFromState();
            final JSONValue columnValue = rowDataArray.get(columnIndex);
            return rendererConnector.decode(columnValue);
        }

        /*
         * Only used to check that the renderer connector will not change during
         * the column lifetime.
         * 
         * TODO remove once support for changing renderers is implemented
         */
        private AbstractRendererConnector<Object> getRendererConnector() {
            return rendererConnector;
        }

        private int resolveCurrentIndexFromState() {
            List<GridColumnState> columns = getState().columns;
            int numColumns = columns.size();
            for (int index = 0; index < numColumns; index++) {
                if (columns.get(index).id.equals(id)) {
                    return index;
                }
            }
            return -1;
        }
    }

    /**
     * Maps a generated column id to a grid column instance
     */
    private Map<String, CustomGridColumn> columnIdToColumn = new HashMap<String, CustomGridColumn>();

    private AbstractRowHandleSelectionModel<JSONObject> selectionModel = createSelectionModel(SharedSelectionMode.NONE);
    private Set<String> selectedKeys = new LinkedHashSet<String>();

    /**
     * updateFromState is set to true when {@link #updateSelectionFromState()}
     * makes changes to selection. This flag tells the
     * {@code internalSelectionChangeHandler} to not send same data straight
     * back to server. Said listener sets it back to false when handling that
     * event.
     */
    private boolean updatedFromState = false;

    private RpcDataSource dataSource;

    private SelectionChangeHandler<JSONObject> internalSelectionChangeHandler = new SelectionChangeHandler<JSONObject>() {
        @Override
        public void onSelectionChange(SelectionChangeEvent<JSONObject> event) {
            if (!updatedFromState) {
                for (JSONObject row : event.getRemoved()) {
                    selectedKeys.remove(dataSource.getRowKey(row));
                }

                for (JSONObject row : event.getAdded()) {
                    selectedKeys.add((String) dataSource.getRowKey(row));
                }

                getRpcProxy(GridServerRpc.class).selectionChange(
                        new ArrayList<String>(selectedKeys));
            } else {
                updatedFromState = false;
            }
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public Grid<JSONObject> getWidget() {
        return (Grid<JSONObject>) super.getWidget();
    }

    @Override
    public GridState getState() {
        return (GridState) super.getState();
    }

    @Override
    protected void init() {
        super.init();

        registerRpc(GridClientRpc.class, new GridClientRpc() {
            @Override
            public void scrollToStart() {
                getWidget().scrollToStart();
            }

            @Override
            public void scrollToEnd() {
                getWidget().scrollToEnd();
            }

            @Override
            public void scrollToRow(int row, ScrollDestination destination) {
                getWidget().scrollToRow(row, destination);
            }
        });

        getWidget().setSelectionModel(selectionModel);

        getWidget().addSelectionChangeHandler(internalSelectionChangeHandler);

        getWidget().addSortHandler(new SortEventHandler<JSONObject>() {
            @Override
            public void sort(SortEvent<JSONObject> event) {
                List<SortOrder> order = event.getOrder();
                String[] columnIds = new String[order.size()];
                SortDirection[] directions = new SortDirection[order.size()];
                for (int i = 0; i < order.size(); i++) {
                    SortOrder sortOrder = order.get(i);
                    CustomGridColumn column = (CustomGridColumn) sortOrder
                            .getColumn();
                    columnIds[i] = column.id;

                    directions[i] = sortOrder.getDirection();
                }

                if (!Arrays.equals(columnIds, getState().sortColumns)
                        || !Arrays.equals(directions, getState().sortDirs)) {
                    // Report back to server if changed
                    getRpcProxy(GridServerRpc.class)
                            .sort(columnIds, directions);
                }
            }
        });
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Column updates
        if (stateChangeEvent.hasPropertyChanged("columns")) {

            int totalColumns = getState().columns.size();

            // Remove old columns
            purgeRemovedColumns();

            int currentColumns = getWidget().getColumnCount();
            if (getWidget().getSelectionModel().getSelectionColumnRenderer() != null) {
                currentColumns--;
            }

            // Add new columns
            for (int columnIndex = currentColumns; columnIndex < totalColumns; columnIndex++) {
                addColumnFromStateChangeEvent(columnIndex);
            }

            // Update old columns
            for (int columnIndex = 0; columnIndex < currentColumns; columnIndex++) {
                // FIXME Currently updating all column header / footers when a
                // change in made in one column. When the framework supports
                // quering a specific item in a list then it should do so here.
                updateColumnFromStateChangeEvent(columnIndex);
            }
        }

        if (stateChangeEvent.hasPropertyChanged("header")) {
            updateSectionFromState(getWidget().getHeader(), getState().header);
        }

        if (stateChangeEvent.hasPropertyChanged("footer")) {
            updateSectionFromState(getWidget().getFooter(), getState().footer);
        }

        if (stateChangeEvent.hasPropertyChanged("lastFrozenColumnId")) {
            String frozenColId = getState().lastFrozenColumnId;
            if (frozenColId != null) {
                CustomGridColumn column = columnIdToColumn.get(frozenColId);
                assert column != null : "Column to be frozen could not be found (id:"
                        + frozenColId + ")";
                getWidget().setLastFrozenColumn(column);
            } else {
                getWidget().setLastFrozenColumn(null);
            }
        }
    }

    private void updateSectionFromState(GridStaticSection<?> section,
            GridStaticSectionState state) {

        while (section.getRowCount() != 0) {
            section.removeRow(0);
        }

        for (RowState rowState : state.rows) {
            StaticRow<?> row = section.appendRow();

            assert rowState.cells.size() == getWidget().getColumnCount();

            int i = 0;
            for (CellState cellState : rowState.cells) {
                row.getCell(i++).setText(cellState.text);
            }

            if (section instanceof GridHeader && rowState.defaultRow) {
                ((GridHeader) section).setDefaultRow((HeaderRow) row);
            }
        }

        section.setVisible(state.visible);

        section.refreshSection();
    }

    /**
     * Updates a column from a state change event.
     * 
     * @param columnIndex
     *            The index of the column to update
     */
    private void updateColumnFromStateChangeEvent(final int columnIndex) {
        /*
         * We use the widget column index here instead of the given column
         * index. SharedState contains information only about the explicitly
         * defined columns, while the widget counts the selection column as an
         * explicit one.
         */
        GridColumn<?, JSONObject> column = getWidget().getColumn(
                getWidgetColumnIndex(columnIndex));

        GridColumnState columnState = getState().columns.get(columnIndex);
        updateColumnFromState(column, columnState);

        assert column instanceof CustomGridColumn : "column at index "
                + columnIndex + " is not a "
                + CustomGridColumn.class.getSimpleName() + ", but a "
                + column.getClass().getSimpleName();

        if (columnState.rendererConnector != ((CustomGridColumn) column)
                .getRendererConnector()) {
            throw new UnsupportedOperationException(
                    "Changing column renderer after initialization is currently unsupported");
        }
    }

    /**
     * Adds a new column to the grid widget from a state change event
     * 
     * @param columnIndex
     *            The index of the column, according to how it
     */
    private void addColumnFromStateChangeEvent(int columnIndex) {
        GridColumnState state = getState().columns.get(columnIndex);
        @SuppressWarnings("unchecked")
        CustomGridColumn column = new CustomGridColumn(state.id,
                ((AbstractRendererConnector<Object>) state.rendererConnector));
        columnIdToColumn.put(state.id, column);

        /*
         * Adds a column to grid, and registers Grid with the column.
         * 
         * We use the widget column index here instead of the given column
         * index. SharedState contains information only about the explicitly
         * defined columns, while the widget counts the selection column as an
         * explicit one.
         */
        getWidget().addColumn(column, getWidgetColumnIndex(columnIndex));

        /*
         * Have to update state _after_ the column has been added to the grid as
         * then, and only then, the column will call the grid which in turn will
         * call the escalator's refreshRow methods on header/footer/body and
         * visually refresh the row. If this is done in the reverse order the
         * first column state update will be lost as no grid instance is
         * present.
         */
        updateColumnFromState(column, state);
    }

    /**
     * If we have a selection column renderer, we need to offset the index by
     * one when referring to the column index in the widget.
     */
    private int getWidgetColumnIndex(final int columnIndex) {
        Renderer<Boolean> selectionColumnRenderer = getWidget()
                .getSelectionModel().getSelectionColumnRenderer();
        int widgetColumnIndex = columnIndex;
        if (selectionColumnRenderer != null) {
            widgetColumnIndex++;
        }
        return widgetColumnIndex;
    }

    /**
     * Updates the column values from a state
     * 
     * @param column
     *            The column to update
     * @param state
     *            The state to get the data from
     */
    private static void updateColumnFromState(GridColumn<?, JSONObject> column,
            GridColumnState state) {
        column.setVisible(state.visible);
        column.setHeaderCaption(state.header);
        column.setFooterCaption(state.footer);
        column.setWidth(state.width);
        column.setSortable(state.sortable);
    }

    /**
     * Removes any orphan columns that has been removed from the state from the
     * grid
     */
    private void purgeRemovedColumns() {

        // Get columns still registered in the state
        Set<String> columnsInState = new HashSet<String>();
        for (GridColumnState columnState : getState().columns) {
            columnsInState.add(columnState.id);
        }

        // Remove column no longer in state
        Iterator<String> columnIdIterator = columnIdToColumn.keySet()
                .iterator();
        while (columnIdIterator.hasNext()) {
            String id = columnIdIterator.next();
            if (!columnsInState.contains(id)) {
                CustomGridColumn column = columnIdToColumn.get(id);
                columnIdIterator.remove();
                getWidget().removeColumn(column);
            }
        }
    }

    public void setDataSource(RpcDataSource dataSource) {
        this.dataSource = dataSource;
        getWidget().setDataSource(this.dataSource);
    }

    @OnStateChange("selectionMode")
    private void onSelectionModeChange() {
        SharedSelectionMode mode = getState().selectionMode;
        if (mode == null) {
            getLogger().fine("ignored mode change");
            return;
        }

        AbstractRowHandleSelectionModel<JSONObject> model = createSelectionModel(mode);
        if (!model.getClass().equals(selectionModel.getClass())) {
            selectionModel = model;
            getWidget().setSelectionModel(model);
            selectedKeys.clear();
        }
    }

    @OnStateChange("selectedKeys")
    private void updateSelectionFromState() {
        boolean changed = false;

        List<String> stateKeys = getState().selectedKeys;

        // find new deselections
        for (String key : selectedKeys) {
            if (!stateKeys.contains(key)) {
                changed = true;
                deselectByHandle(dataSource.getHandleByKey(key));
            }
        }

        // find new selections
        for (String key : stateKeys) {
            if (!selectedKeys.contains(key)) {
                changed = true;
                selectByHandle(dataSource.getHandleByKey(key));
            }
        }

        /*
         * A defensive copy in case the collection in the state is mutated
         * instead of re-assigned.
         */
        selectedKeys = new LinkedHashSet<String>(stateKeys);

        /*
         * We need to fire this event so that Grid is able to re-render the
         * selection changes (if applicable).
         */
        if (changed) {
            // At least for now there's no way to send the selected and/or
            // deselected row data. Some data is only stored as keys
            updatedFromState = true;
            getWidget().fireEvent(
                    new SelectionChangeEvent<JSONObject>(getWidget(),
                            (List<JSONObject>) null, null));
        }
    }

    @OnStateChange({ "sortColumns", "sortDirs" })
    private void onSortStateChange() {
        List<SortOrder> sortOrder = new ArrayList<SortOrder>();

        String[] sortColumns = getState().sortColumns;
        SortDirection[] sortDirs = getState().sortDirs;

        for (int i = 0; i < sortColumns.length; i++) {
            sortOrder.add(new SortOrder(columnIdToColumn.get(sortColumns[i]),
                    sortDirs[i]));
        }

        getWidget().setSortOrder(sortOrder);
    }

    private Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }

    @SuppressWarnings("static-method")
    private AbstractRowHandleSelectionModel<JSONObject> createSelectionModel(
            SharedSelectionMode mode) {
        switch (mode) {
        case SINGLE:
            return new SelectionModelSingle<JSONObject>();
        case MULTI:
            return new SelectionModelMulti<JSONObject>();
        case NONE:
            return new SelectionModelNone<JSONObject>();
        default:
            throw new IllegalStateException("unexpected mode value: " + mode);
        }
    }

    /**
     * A workaround method for accessing the protected method
     * {@code AbstractRowHandleSelectionModel.selectByHandle}
     */
    private native void selectByHandle(RowHandle<JSONObject> handle)
    /*-{
        var model = this.@com.vaadin.client.ui.grid.GridConnector::selectionModel;
        model.@com.vaadin.client.ui.grid.selection.AbstractRowHandleSelectionModel::selectByHandle(*)(handle);
    }-*/;

    /**
     * A workaround method for accessing the protected method
     * {@code AbstractRowHandleSelectionModel.deselectByHandle}
     */
    private native void deselectByHandle(RowHandle<JSONObject> handle)
    /*-{
        var model = this.@com.vaadin.client.ui.grid.GridConnector::selectionModel;
        model.@com.vaadin.client.ui.grid.selection.AbstractRowHandleSelectionModel::deselectByHandle(*)(handle);
    }-*/;

    /**
     * Gets the row key for a row by index.
     * 
     * @param index
     *            the index of the row for which to get the key
     * @return the key for the row at {@code index}
     */
    public String getRowKey(int index) {
        final JSONObject row = dataSource.getRow(index);
        final Object key = dataSource.getRowKey(row);
        assert key instanceof String : "Internal key was not a String but a "
                + key.getClass().getSimpleName() + " (" + key + ")";
        return (String) key;
    }
}
