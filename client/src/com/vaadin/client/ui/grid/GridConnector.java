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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.data.RpcDataSourceConnector.RpcDataSource;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.grid.Grid.CellStyleGenerator;
import com.vaadin.client.ui.grid.GridHeader.HeaderRow;
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
import com.vaadin.shared.ui.grid.EditorRowClientRpc;
import com.vaadin.shared.ui.grid.EditorRowServerRpc;
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
@Connect(com.vaadin.ui.Grid.class)
public class GridConnector extends AbstractHasComponentsConnector implements
        SimpleManagedLayout {

    private static final class CustomCellStyleGenerator implements
            CellStyleGenerator<JSONObject> {
        @Override
        public String getStyle(Grid<JSONObject> grid, JSONObject row,
                int rowIndex, GridColumn<?, JSONObject> column, int columnIndex) {
            if (column == null) {
                JSONValue styleValue = row.get(GridState.JSONKEY_ROWSTYLE);
                if (styleValue != null) {
                    return styleValue.isString().stringValue();
                } else {
                    return null;
                }
            } else {
                JSONValue cellstyles = row.get(GridState.JSONKEY_CELLSTYLES);
                if (cellstyles == null) {
                    return null;
                }

                CustomGridColumn c = (CustomGridColumn) column;
                JSONValue styleValue = cellstyles.isObject().get(c.id);
                if (styleValue != null) {
                    return styleValue.isString().stringValue();
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Custom implementation of the custom grid column using a JSONObject to
     * represent the cell value and String as a column type.
     */
    private class CustomGridColumn extends GridColumn<Object, JSONObject> {

        private final String id;

        private AbstractRendererConnector<Object> rendererConnector;

        private AbstractFieldConnector editorConnector;

        public CustomGridColumn(String id,
                AbstractRendererConnector<Object> rendererConnector) {
            super(rendererConnector.getRenderer());
            this.rendererConnector = rendererConnector;
            this.id = id;
        }

        @Override
        public Object getValue(final JSONObject obj) {
            final JSONValue rowData = obj.get(GridState.JSONKEY_DATA);
            final JSONObject rowDataObject = rowData.isObject();
            assert rowDataObject != null : "Was unable to parse JSON into an array: "
                    + rowData;

            final JSONValue columnValue = rowDataObject.get(id);

            /*
             * note, Java "null" is different from JSONValue "null" (i.e.
             * JSONNull).
             */
            assert columnValue != null : "Could not find data for column with id "
                    + id;
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

        private AbstractFieldConnector getEditorConnector() {
            return editorConnector;
        }

        private void setEditorConnector(AbstractFieldConnector editorConnector) {
            this.editorConnector = editorConnector;
        }
    }

    /*
     * An editor row handler using Vaadin RPC to manage the editor row state.
     */
    private class CustomEditorRowHandler implements
            EditorRowHandler<JSONObject> {

        private EditorRowServerRpc rpc = getRpcProxy(EditorRowServerRpc.class);

        private EditorRowRequest<?> currentRequest = null;
        private boolean serverInitiated = false;

        public CustomEditorRowHandler() {
            registerRpc(EditorRowClientRpc.class, new EditorRowClientRpc() {

                @Override
                public void bind(int rowIndex) {
                    serverInitiated = true;
                    GridConnector.this.getWidget().getEditorRow()
                            .editRow(rowIndex);
                }

                @Override
                public void discard(int rowIndex) {
                    serverInitiated = true;
                    GridConnector.this.getWidget().getEditorRow().discard();
                }

                @Override
                public void cancel(int rowIndex) {
                    serverInitiated = true;
                    GridConnector.this.getWidget().getEditorRow().cancel();
                }

                @Override
                public void confirmBind() {
                    endRequest();
                }

                @Override
                public void confirmCommit() {
                    endRequest();
                }
            });
        }

        @Override
        public void bind(EditorRowRequest<JSONObject> request) {
            if (!handleServerInitiated(request)) {
                startRequest(request);
                rpc.bind(request.getRowIndex());
            }
        }

        @Override
        public void commit(EditorRowRequest<JSONObject> request) {
            if (!handleServerInitiated(request)) {
                startRequest(request);
                rpc.commit(request.getRowIndex());
            }
        }

        @Override
        public void discard(EditorRowRequest<JSONObject> request) {
            if (!handleServerInitiated(request)) {
                startRequest(request);
                rpc.discard(request.getRowIndex());
            }
        }

        @Override
        public void cancel(EditorRowRequest<JSONObject> request) {
            if (!handleServerInitiated(request)) {
                // No startRequest as we don't get (or need)
                // a confirmation from the server
                rpc.cancel(request.getRowIndex());
            }
        }

        @Override
        public Widget getWidget(GridColumn<?, JSONObject> column) {
            assert column != null;

            if (column instanceof CustomGridColumn) {
                AbstractFieldConnector c = ((CustomGridColumn) column)
                        .getEditorConnector();
                return c != null ? c.getWidget() : null;
            } else {
                throw new IllegalStateException("Unexpected column type: "
                        + column.getClass().getName());
            }
        }

        /**
         * Used to handle the case where EditorRow calls us because it was
         * invoked by the server via RPC and not by the client. In that case, we
         * simply synchronously complete the request.
         * 
         * @param request
         *            the request object
         * @return true if the request was originally triggered by the server,
         *         false otherwise
         */
        private boolean handleServerInitiated(EditorRowRequest<?> request) {
            assert request != null;
            assert currentRequest == null;

            if (serverInitiated) {
                serverInitiated = false;
                request.invokeCallback();
                return true;
            } else {
                return false;
            }
        }

        private void startRequest(EditorRowRequest<?> request) {
            currentRequest = request;
        }

        private void endRequest() {
            assert currentRequest != null;
            currentRequest.invokeCallback();
            currentRequest = null;
        }
    }

    /**
     * Maps a generated column id to a grid column instance
     */
    private Map<String, CustomGridColumn> columnIdToColumn = new HashMap<String, CustomGridColumn>();

    private AbstractRowHandleSelectionModel<JSONObject> selectionModel = createSelectionModel(SharedSelectionMode.NONE);
    private Set<String> selectedKeys = new LinkedHashSet<String>();
    private List<String> columnOrder = new ArrayList<String>();

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
            if (event.isBatchedSelection()) {
                return;
            }
            if (!updatedFromState) {
                for (JSONObject row : event.getRemoved()) {
                    selectedKeys.remove(dataSource.getRowKey(row));
                }

                for (JSONObject row : event.getAdded()) {
                    selectedKeys.add(dataSource.getRowKey(row));
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
                    getRpcProxy(GridServerRpc.class).sort(columnIds,
                            directions, event.getOriginator());
                }
            }
        });

        getWidget().getEditorRow().setHandler(new CustomEditorRowHandler());
        getLayoutManager().registerDependency(this, getWidget().getElement());
        layout();
    }

    @Override
    public void onStateChanged(final StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        /*
         * The operations in here have been made deferred.
         * 
         * The row data needed to react to column changes comes in the RPC
         * calls. Since state is always updated before RPCs are called, we need
         * to be sure that RPC is called before Grid reacts to state changes.
         * 
         * Note that there are still some methods annotated with @OnStateChange
         * that aren't deferred. That's okay, though.
         */

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                // Column updates
                if (stateChangeEvent.hasPropertyChanged("columns")) {

                    // Remove old columns
                    purgeRemovedColumns();

                    // Add new columns
                    for (GridColumnState state : getState().columns) {
                        if (!columnIdToColumn.containsKey(state.id)) {
                            addColumnFromStateChangeEvent(state);
                        }
                        updateColumnFromState(columnIdToColumn.get(state.id),
                                state);
                    }
                }

                if (stateChangeEvent.hasPropertyChanged("columnOrder")) {
                    if (orderNeedsUpdate(getState().columnOrder)) {
                        updateColumnOrderFromState(getState().columnOrder);
                    }
                }

                if (stateChangeEvent.hasPropertyChanged("header")) {
                    updateSectionFromState(getWidget().getHeader(),
                            getState().header);
                }

                if (stateChangeEvent.hasPropertyChanged("footer")) {
                    updateSectionFromState(getWidget().getFooter(),
                            getState().footer);
                }

                if (stateChangeEvent.hasPropertyChanged("lastFrozenColumnId")) {
                    String frozenColId = getState().lastFrozenColumnId;
                    if (frozenColId != null) {
                        CustomGridColumn column = columnIdToColumn
                                .get(frozenColId);
                        assert column != null : "Column to be frozen could not be found (id:"
                                + frozenColId + ")";
                        getWidget().setLastFrozenColumn(column);
                    } else {
                        getWidget().setLastFrozenColumn(null);
                    }
                }

                if (stateChangeEvent.hasPropertyChanged("editorRowEnabled")) {
                    getWidget().getEditorRow().setEnabled(
                            getState().editorRowEnabled);
                }

            }
        });

    }

    private void updateColumnOrderFromState(List<String> stateColumnOrder) {
        CustomGridColumn[] columns = new CustomGridColumn[stateColumnOrder
                .size()];
        int i = 0;
        for (String id : stateColumnOrder) {
            columns[i] = columnIdToColumn.get(id);
            i++;
        }
        getWidget().setColumnOrder(columns);
        columnOrder = stateColumnOrder;
    }

    private boolean orderNeedsUpdate(List<String> stateColumnOrder) {
        if (stateColumnOrder.size() == columnOrder.size()) {
            for (int i = 0; i < columnOrder.size(); ++i) {
                if (!stateColumnOrder.get(i).equals(columnOrder.get(i))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private void updateSectionFromState(GridStaticSection<?> section,
            GridStaticSectionState state) {

        while (section.getRowCount() != 0) {
            section.removeRow(0);
        }

        for (RowState rowState : state.rows) {
            GridStaticSection.StaticRow<?> row = section.appendRow();

            for (CellState cellState : rowState.cells) {
                CustomGridColumn column = columnIdToColumn
                        .get(cellState.columnId);
                GridStaticSection.StaticCell cell = row.getCell(column);
                updateStaticCellFromState(cell, cellState);
            }

            for (Set<String> group : rowState.cellGroups.keySet()) {
                GridColumn<?, ?>[] columns = new GridColumn<?, ?>[group.size()];
                CellState cellState = rowState.cellGroups.get(group);

                int i = 0;
                for (String columnId : group) {
                    columns[i] = columnIdToColumn.get(columnId);
                    i++;
                }

                // Set state to be the same as first in group.
                updateStaticCellFromState(row.join(columns), cellState);
            }

            if (section instanceof GridHeader && rowState.defaultRow) {
                ((GridHeader) section).setDefaultRow((HeaderRow) row);
            }

            row.setStyleName(rowState.styleName);
        }

        section.setVisible(state.visible);

        section.requestSectionRefresh();
    }

    private void updateStaticCellFromState(GridStaticSection.StaticCell cell,
            CellState cellState) {
        switch (cellState.type) {
        case TEXT:
            cell.setText(cellState.text);
            break;
        case HTML:
            cell.setHtml(cellState.html);
            break;
        case WIDGET:
            ComponentConnector connector = (ComponentConnector) cellState.connector;
            cell.setWidget(connector.getWidget());
            break;
        default:
            throw new IllegalStateException("unexpected cell type: "
                    + cellState.type);
        }
        cell.setStyleName(cellState.styleName);
    }

    /**
     * Updates a column from a state change event.
     * 
     * @param columnIndex
     *            The index of the column to update
     */
    private void updateColumnFromStateChangeEvent(GridColumnState columnState) {
        CustomGridColumn column = columnIdToColumn.get(columnState.id);

        updateColumnFromState(column, columnState);

        if (columnState.rendererConnector != column.getRendererConnector()) {
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
    private void addColumnFromStateChangeEvent(GridColumnState state) {
        @SuppressWarnings("unchecked")
        CustomGridColumn column = new CustomGridColumn(state.id,
                ((AbstractRendererConnector<Object>) state.rendererConnector));
        columnIdToColumn.put(state.id, column);

        /*
         * Add column to grid. Reordering is handled as a separate problem.
         */
        getWidget().addColumn(column);
        columnOrder.add(state.id);
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
    private static void updateColumnFromState(CustomGridColumn column,
            GridColumnState state) {
        column.setVisible(state.visible);
        column.setWidth(state.width);
        column.setSortable(state.sortable);
        column.setEditorConnector((AbstractFieldConnector) state.editorConnector);
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
                columnOrder.remove(id);
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

    @OnStateChange("hasCellStyleGenerator")
    private void onCellStyleGeneratorChange() {
        if (getState().hasCellStyleGenerator) {
            getWidget().setCellStyleGenerator(new CustomCellStyleGenerator());
        } else {
            getWidget().setCellStyleGenerator(null);
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
                            (List<JSONObject>) null, null, false));
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin.client
     * .ComponentConnector)
     */
    @Override
    public void updateCaption(ComponentConnector connector) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
    }

    @Override
    public void layout() {
        getWidget().onResize();
    }
}
