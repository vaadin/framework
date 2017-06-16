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
package com.vaadin.client.connectors.grid;

import java.util.Optional;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.events.GridSelectionAllowedEvent;
import com.vaadin.client.widget.grid.events.SelectAllEvent;
import com.vaadin.client.widget.grid.selection.MultiSelectionRenderer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SelectionModelWithSelectionColumn;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.SelectionColumn;
import com.vaadin.shared.Range;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.GridMultiSelectServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.MultiSelectionModelState;

import elemental.json.JsonObject;

/**
 * Connector for server side multiselection model implementation.
 * <p>
 * This selection model displays a selection column {@link SelectionColumn} as
 * the first column of the grid.
 * <p>
 * Implementation detail: The Grid selection is updated immediately on client
 * side, without waiting for the server response.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 */
@Connect(com.vaadin.ui.components.grid.MultiSelectionModelImpl.class)
public class MultiSelectionModelConnector
        extends AbstractSelectionModelConnector {

    private HandlerRegistration selectAllHandler;
    private HandlerRegistration dataAvailable;
    private Range availableRows;

    /**
     * Client side multiselection model implementation.
     */
    protected class MultiSelectionModel implements SelectionModel<JsonObject>,
            SelectionModelWithSelectionColumn {

        private boolean isSelectionAllowed = true;

        @Override
        public Renderer<Boolean> getRenderer() {
            // this method is only called once when the selection model is set
            // to grid
            return new MultiSelectionRenderer<>(getGrid());
        }

        @Override
        public void select(JsonObject item) {
            updateRowSelected(getGrid().getDataSource().getHandle(item), true);

            getRpcProxy(GridMultiSelectServerRpc.class)
                    .select(item.getString(DataCommunicatorConstants.KEY));
        }

        @Override
        public void deselect(JsonObject item) {
            if (isAllSelected()) {
                // handled by diffstate
                getState().allSelected = false;
                getGrid().getSelectionColumn().get().getSelectAllCheckBox()
                        .ifPresent(cb -> cb.setValue(false, false));
            }
            updateRowSelected(getGrid().getDataSource().getHandle(item), false);

            getRpcProxy(GridMultiSelectServerRpc.class)
                    .deselect(item.getString(DataCommunicatorConstants.KEY));
        }

        @Override
        public void deselectAll() {
            // handled by diffstate
            updateAllRowsSelected(false);
            getState().allSelected = false;
            getRpcProxy(GridMultiSelectServerRpc.class).deselectAll();
        }

        @Override
        public boolean isSelected(JsonObject item) {
            return MultiSelectionModelConnector.this.isSelected(item);
        }

        @Override
        public void setSelectionAllowed(boolean selectionAllowed) {
            isSelectionAllowed = selectionAllowed;
            getGrid()
                    .fireEvent(new GridSelectionAllowedEvent(selectionAllowed));
        }

        @Override
        public boolean isSelectionAllowed() {
            return isSelectionAllowed;
        }

    }

    @Override
    protected void initSelectionModel() {
        getGrid().setSelectionModel(createSelectionModel());
        // capture current rows so that can show selection update immediately
        dataAvailable = getGrid().addDataAvailableHandler(
                event -> availableRows = event.getAvailableRows());
    }

    /**
     * Creates an instance of MultiSelectionModel. Method provided overriding
     * features of the selection model without copying all logic.
     *
     * @since 8.1
     *
     * @return selection model instance, not {@code null}
     */
    protected MultiSelectionModel createSelectionModel() {
        return new MultiSelectionModel();
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        dataAvailable.removeHandler();
        if (selectAllHandler != null) {
            selectAllHandler.removeHandler();
        }
    }

    @Override
    public MultiSelectionModelState getState() {
        return (MultiSelectionModelState) super.getState();
    }

    @OnStateChange({ "selectAllCheckBoxVisible", "allSelected" })
    void onSelectAllCheckboxStateUpdates() {
        // in case someone wants to override this, moved the actual updating to
        // a protected method
        updateSelectAllCheckBox();
    }

    /**
     * Called whenever there has been a state update for select all checkbox
     * visibility or all have been selected or deselected.
     */
    protected void updateSelectAllCheckBox() {
        final boolean selectAllCheckBoxVisible = getState().selectAllCheckBoxVisible;
        if (selectAllCheckBoxVisible && selectAllHandler == null) {
            selectAllHandler = getGrid()
                    .addSelectAllHandler(this::onSelectAllEvent);
        } else if (!selectAllCheckBoxVisible && selectAllHandler != null) {
            selectAllHandler.removeHandler();
            selectAllHandler = null;
        }
        Optional<Grid<JsonObject>.SelectionColumn> selectionColumn = getGrid()
                .getSelectionColumn();

        // if someone extends this selection model and doesn't use the selection
        // column, the select all checkbox is not there either
        if (selectionColumn.isPresent()) {
            selectionColumn.get()
                    .setSelectAllCheckBoxVisible(selectAllCheckBoxVisible);
            selectionColumn.get().getSelectAllCheckBox()
                    .ifPresent(checkbox -> checkbox
                            .setValue(getState().allSelected, false));
        }
    }

    /**
     * Returns whether all items are selected or not.
     *
     * @return {@code true} if all items are selected, {@code false} if not
     */
    protected boolean isAllSelected() {
        return getState().selectAllCheckBoxVisible && getState().allSelected;
    }

    /**
     * Handler for selecting / deselecting all grid rows.
     *
     * @param event
     *            the select all event from grid
     */
    protected void onSelectAllEvent(SelectAllEvent<JsonObject> event) {
        final boolean allSelected = event.isAllSelected();
        final boolean wasAllSelected = isAllSelected();
        assert allSelected != wasAllSelected : "Grid Select All CheckBox had invalid state";

        if (allSelected && !wasAllSelected) {
            getState().allSelected = true;
            updateAllRowsSelected(true);
            getRpcProxy(GridMultiSelectServerRpc.class).selectAll();
        } else if (!allSelected && wasAllSelected) {
            getState().allSelected = false;
            updateAllRowsSelected(false);
            getRpcProxy(GridMultiSelectServerRpc.class).deselectAll();
        }
    }

    /**
     * Update selection for all grid rows.
     *
     * @param selected
     *            {@code true} for marking all rows selected, {@code false} for
     *            not selected
     */
    protected void updateAllRowsSelected(boolean selected) {
        if (availableRows != null) {
            DataSource<JsonObject> dataSource = getGrid().getDataSource();
            for (int i = availableRows.getStart(); i < availableRows
                    .getEnd(); ++i) {
                final JsonObject row = dataSource.getRow(i);
                if (row != null) {
                    RowHandle<JsonObject> handle = dataSource.getHandle(row);
                    updateRowSelected(handle, selected);
                }
            }
        }
    }

    @Override
    protected boolean isSelected(JsonObject item) {
        return getState().allSelected || super.isSelected(item);
    }

    /**
     * Marks the given row to be selected or deselected. Returns true if the
     * value actually changed.
     * <p>
     * Note: If selection model is in batch select state, the row will be pinned
     * on select.
     *
     * @param row
     *            row handle
     * @param selected
     *            {@code true} if row should be selected; {@code false} if not
     */
    protected void updateRowSelected(RowHandle<JsonObject> row,
            boolean selected) {
        boolean itemWasMarkedSelected = SelectionModel
                .isItemSelected(row.getRow());
        if (selected && !itemWasMarkedSelected) {
            row.getRow().put(DataCommunicatorConstants.SELECTED, true);
        } else if (!selected && itemWasMarkedSelected) {
            row.getRow().remove(DataCommunicatorConstants.SELECTED);
        }

        row.updateRow();
    }

}
