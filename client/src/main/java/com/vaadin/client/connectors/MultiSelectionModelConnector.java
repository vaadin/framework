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
package com.vaadin.client.connectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.DataAvailableEvent;
import com.vaadin.client.widget.grid.DataAvailableHandler;
import com.vaadin.client.widget.grid.events.SelectAllEvent;
import com.vaadin.client.widget.grid.events.SelectAllHandler;
import com.vaadin.client.widget.grid.selection.HasUserSelectionAllowed;
import com.vaadin.client.widget.grid.selection.MultiSelectionRenderer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SelectionModel.Multi;
import com.vaadin.client.widget.grid.selection.SpaceSelectHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.client.widgets.Grid.HeaderCell;
import com.vaadin.client.widgets.Grid.SelectionColumn;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.shared.ui.grid.selection.MultiSelectionModelServerRpc;
import com.vaadin.shared.ui.grid.selection.MultiSelectionModelState;
import com.vaadin.ui.Grid.MultiSelectionModel;

import elemental.json.JsonObject;

/**
 * Connector for server-side {@link MultiSelectionModel}.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
@Connect(MultiSelectionModel.class)
public class MultiSelectionModelConnector extends
        AbstractSelectionModelConnector<SelectionModel.Multi<JsonObject>> {

    private Multi<JsonObject> selectionModel = createSelectionModel();
    private SpaceSelectHandler<JsonObject> spaceHandler;

    @Override
    protected void extend(ServerConnector target) {
        getGrid().setSelectionModel(selectionModel);
        spaceHandler = new SpaceSelectHandler<JsonObject>(getGrid());
    }

    @Override
    public void onUnregister() {
        spaceHandler.removeHandler();
    }

    @Override
    protected Multi<JsonObject> createSelectionModel() {
        return new MultiSelectionModel();
    }

    @Override
    public MultiSelectionModelState getState() {
        return (MultiSelectionModelState) super.getState();
    }

    @OnStateChange("allSelected")
    void updateSelectAllCheckbox() {
        if (selectionModel.getSelectionColumnRenderer() != null) {
            HeaderCell cell = getGrid().getDefaultHeaderRow()
                    .getCell(getGrid().getColumn(0));
            CheckBox widget = (CheckBox) cell.getWidget();
            widget.setValue(getState().allSelected, false);
        }
    }

    @OnStateChange("userSelectionAllowed")
    void updateUserSelectionAllowed() {
        if (selectionModel instanceof HasUserSelectionAllowed) {
            ((HasUserSelectionAllowed) selectionModel)
                    .setUserSelectionAllowed(getState().userSelectionAllowed);
        } else {
            getLogger().warning("userSelectionAllowed set to "
                    + getState().userSelectionAllowed
                    + " but the selection model does not implement "
                    + HasUserSelectionAllowed.class.getSimpleName());
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(MultiSelectionModelConnector.class.getName());
    }

    /**
     * The default multi selection model used for this connector.
     *
     */
    protected class MultiSelectionModel extends AbstractSelectionModel
            implements SelectionModel.Multi.Batched<JsonObject>,
            HasUserSelectionAllowed<JsonObject> {

        private ComplexRenderer<Boolean> renderer = null;
        private Set<RowHandle<JsonObject>> selected = new HashSet<RowHandle<JsonObject>>();
        private Set<RowHandle<JsonObject>> deselected = new HashSet<RowHandle<JsonObject>>();
        private HandlerRegistration selectAll;
        private HandlerRegistration dataAvailable;
        private Range availableRows;
        private boolean batchSelect = false;
        private boolean userSelectionAllowed = true;

        @Override
        public void setGrid(Grid<JsonObject> grid) {
            super.setGrid(grid);
            if (grid != null) {
                renderer = createSelectionColumnRenderer(grid);
                selectAll = getGrid().addSelectAllHandler(
                        new SelectAllHandler<JsonObject>() {

                            @Override
                            public void onSelectAll(
                                    SelectAllEvent<JsonObject> event) {
                                selectAll();
                            }
                        });
                dataAvailable = getGrid()
                        .addDataAvailableHandler(new DataAvailableHandler() {

                            @Override
                            public void onDataAvailable(
                                    DataAvailableEvent event) {
                                availableRows = event.getAvailableRows();
                            }
                        });
            } else if (renderer != null) {
                selectAll.removeHandler();
                dataAvailable.removeHandler();
                renderer = null;
            }
        }

        /**
         * Creates a selection column renderer. This method can be overridden to
         * use a custom renderer or use {@code null} to disable the selection
         * column.
         *
         * @param grid
         *            the grid for this selection model
         * @return selection column renderer or {@code null} if not needed
         */
        protected ComplexRenderer<Boolean> createSelectionColumnRenderer(
                Grid<JsonObject> grid) {
            return new MultiSelectionRenderer<JsonObject>(grid);
        }

        /**
         * Selects all available rows, sends request to server to select
         * everything.
         */
        public void selectAll() {
            assert !isBeingBatchSelected() : "Can't select all in middle of a batch selection.";

            DataSource<JsonObject> dataSource = getGrid().getDataSource();
            for (int i = availableRows.getStart(); i < availableRows
                    .getEnd(); ++i) {
                final JsonObject row = dataSource.getRow(i);
                if (row != null) {
                    RowHandle<JsonObject> handle = dataSource.getHandle(row);
                    markAsSelected(handle, true);
                }
            }

            getRpcProxy(MultiSelectionModelServerRpc.class).selectAll();
        }

        @Override
        public Renderer<Boolean> getSelectionColumnRenderer() {
            return renderer;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@code false} if rows is empty, else {@code true}
         */
        @Override
        public boolean select(JsonObject... rows) {
            return select(Arrays.asList(rows));
        }

        /**
         * {@inheritDoc}
         *
         * @return {@code false} if rows is empty, else {@code true}
         */
        @Override
        public boolean deselect(JsonObject... rows) {
            return deselect(Arrays.asList(rows));
        }

        /**
         * {@inheritDoc}
         *
         * @return always {@code true}
         */
        @Override
        public boolean deselectAll() {
            assert !isBeingBatchSelected() : "Can't select all in middle of a batch selection.";

            DataSource<JsonObject> dataSource = getGrid().getDataSource();
            for (int i = availableRows.getStart(); i < availableRows
                    .getEnd(); ++i) {
                final JsonObject row = dataSource.getRow(i);
                if (row != null) {
                    RowHandle<JsonObject> handle = dataSource.getHandle(row);
                    markAsSelected(handle, false);
                }
            }

            getRpcProxy(MultiSelectionModelServerRpc.class).deselectAll();

            return true;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@code false} if rows is empty, else {@code true}
         */
        @Override
        public boolean select(Collection<JsonObject> rows) {
            if (rows.isEmpty()) {
                return false;
            }

            for (JsonObject row : rows) {
                RowHandle<JsonObject> rowHandle = getRowHandle(row);
                if (markAsSelected(rowHandle, true)) {
                    selected.add(rowHandle);
                }
            }

            if (!isBeingBatchSelected()) {
                sendSelected();
            }
            return true;
        }

        /**
         * Marks the given row to be selected or deselected. Returns true if the
         * value actually changed.
         * <p>
         * Note: If selection model is in batch select state, the row will be
         * pinned on select.
         *
         * @param row
         *            row handle
         * @param selected
         *            {@code true} if row should be selected; {@code false} if
         *            not
         * @return {@code true} if selected status changed; {@code false} if not
         */
        protected boolean markAsSelected(RowHandle<JsonObject> row,
                boolean selected) {
            if (selected && !isSelected(row.getRow())) {
                row.getRow().put(GridState.JSONKEY_SELECTED, true);
            } else if (!selected && isSelected(row.getRow())) {
                row.getRow().remove(GridState.JSONKEY_SELECTED);
            } else {
                return false;
            }

            row.updateRow();

            if (isBeingBatchSelected()) {
                row.pin();
            }
            return true;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@code false} if rows is empty, else {@code true}
         */
        @Override
        public boolean deselect(Collection<JsonObject> rows) {
            if (rows.isEmpty()) {
                return false;
            }

            for (JsonObject row : rows) {
                RowHandle<JsonObject> rowHandle = getRowHandle(row);
                if (markAsSelected(rowHandle, false)) {
                    deselected.add(rowHandle);
                }
            }

            if (!isBeingBatchSelected()) {
                sendDeselected();
            }
            return true;
        }

        /**
         * Sends a deselect RPC call to server-side containing all deselected
         * rows. Unpins any pinned rows.
         */
        private void sendDeselected() {
            getRpcProxy(MultiSelectionModelServerRpc.class)
                    .deselect(getRowKeys(deselected));

            if (isBeingBatchSelected()) {
                for (RowHandle<JsonObject> row : deselected) {
                    row.unpin();
                }
            }

            deselected.clear();
        }

        /**
         * Sends a select RPC call to server-side containing all selected rows.
         * Unpins any pinned rows.
         */
        private void sendSelected() {
            getRpcProxy(MultiSelectionModelServerRpc.class)
                    .select(getRowKeys(selected));

            if (isBeingBatchSelected()) {
                for (RowHandle<JsonObject> row : selected) {
                    row.unpin();
                }
            }

            selected.clear();
        }

        private List<String> getRowKeys(Set<RowHandle<JsonObject>> handles) {
            List<String> keys = new ArrayList<String>();
            for (RowHandle<JsonObject> handle : handles) {
                keys.add(getRowKey(handle.getRow()));
            }
            return keys;
        }

        private Set<JsonObject> getRows(Set<RowHandle<JsonObject>> handles) {
            Set<JsonObject> rows = new HashSet<JsonObject>();
            for (RowHandle<JsonObject> handle : handles) {
                rows.add(handle.getRow());
            }
            return rows;
        }

        @Override
        public void startBatchSelect() {
            assert selected.isEmpty()
                    && deselected.isEmpty() : "Row caches were not clear.";
            batchSelect = true;
        }

        @Override
        public void commitBatchSelect() {
            assert batchSelect : "Not batch selecting.";
            if (!selected.isEmpty()) {
                sendSelected();
            }

            if (!deselected.isEmpty()) {
                sendDeselected();
            }
            batchSelect = false;
        }

        @Override
        public boolean isBeingBatchSelected() {
            return batchSelect;
        }

        @Override
        public Collection<JsonObject> getSelectedRowsBatch() {
            return Collections.unmodifiableSet(getRows(selected));
        }

        @Override
        public Collection<JsonObject> getDeselectedRowsBatch() {
            return Collections.unmodifiableSet(getRows(deselected));
        }

        @Override
        public boolean isUserSelectionAllowed() {
            return userSelectionAllowed;
        }

        @Override
        public void setUserSelectionAllowed(boolean userSelectionAllowed) {
            this.userSelectionAllowed = userSelectionAllowed;
            for (Column<?, ?> c : getGrid().getColumns()) {
                if (c instanceof SelectionColumn) {
                    ((SelectionColumn) c)
                            .setUserSelectionAllowed(userSelectionAllowed);
                }
            }
        }
    }
}
