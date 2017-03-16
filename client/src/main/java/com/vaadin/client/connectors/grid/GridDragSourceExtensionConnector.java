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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.DragSourceExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Range;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.grid.GridDragSourceExtensionState;
import com.vaadin.ui.GridDragSourceExtension;

import elemental.events.Event;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Adds HTML5 drag and drop functionality to a {@link com.vaadin.client.widgets.Grid
 * Grid}'s rows. This is the client side counterpart of {@link
 * GridDragSourceExtension}.
 *
 * @author Vaadin Ltd
 * @since
 */
@Connect(GridDragSourceExtension.class)
public class GridDragSourceExtensionConnector extends
        DragSourceExtensionConnector {

    private GridConnector gridConnector;

    @Override
    protected void extend(ServerConnector target) {
        this.gridConnector = (GridConnector) target;

        // Set newly added rows draggable
        getGridBody().setNewEscalatorRowCallback(
                rows -> rows.forEach(this::setDraggable));

        // Add drag listeners to body element
        addDragListeners(getGridBody().getElement());
    }

    @Override
    protected void onDragStart(Event event) {
        super.onDragStart(event);

        if (event.getTarget() instanceof TableRowElement) {
            TableRowElement row = (TableRowElement) event.getTarget();
            int rowIndex = ((Escalator.AbstractRowContainer) getGridBody())
                    .getLogicalRowIndex(row);

            JsonObject rowData = gridConnector.getDataSource().getRow(rowIndex);

            // Generate drag data. Dragged row or all the selected rows
            JsonValue dragData = dragMultipleRows(rowData) ? toJsonArray(
                    getSelectedVisibleRows().stream().map(this::getDragData)
                            .collect(Collectors.toList()))
                    : getDragData(rowData);

            // Set drag data in DataTransfer object
            ((NativeEvent) event).getDataTransfer()
                    .setData(DragSourceState.DATA_TYPE_TEXT, dragData.toJson());
        }
    }

    /**
     * Tells if multiple rows are dragged. Returns true if multiple selection is
     * allowed and a selected row is dragged.
     *
     * @param draggedRow
     *         Data of dragged row.
     * @return {@code true} if multiple rows are dragged, {@code false}
     * otherwise.
     */
    private boolean dragMultipleRows(JsonObject draggedRow) {
        SelectionModel<JsonObject> selectionModel = getGrid()
                .getSelectionModel();
        return selectionModel.isSelectionAllowed()
                && selectionModel instanceof MultiSelectionModelConnector.MultiSelectionModel
                && selectionModel.isSelected(draggedRow);
    }

    /**
     * Collects the data of all selected visible rows.
     *
     * @return List of data of all selected visible rows.
     */
    private List<JsonObject> getSelectedVisibleRows() {
        return getSelectedRowsInRange(getEscalator().getVisibleRowRange());
    }

    /**
     * Get all selected rows from a subset of rows defined by {@code range}.
     *
     * @param range
     *         Range of indexes.
     * @return List of data of all selected rows in the given range.
     */
    private List<JsonObject> getSelectedRowsInRange(Range range) {
        List<JsonObject> selectedRows = new ArrayList<>();

        for (int i = range.getStart(); i < range.getEnd(); i++) {
            JsonObject row = gridConnector.getDataSource().getRow(i);
            if (SelectionModel.isItemSelected(row)) {
                selectedRows.add(row);
            }
        }

        return selectedRows;
    }

    /**
     * Converts a list of {@link JsonObject}s to a {@link JsonArray}.
     *
     * @param objects
     *         List of json objects.
     * @return Json array containing all json objects.
     */
    private JsonArray toJsonArray(List<JsonObject> objects) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < objects.size(); i++) {
            array.set(i, objects.get(i));
        }
        return array;
    }

    /**
     * Gets drag data from the row data if exists or returns complete row data
     * otherwise.
     *
     * @param row
     *         Row data.
     * @return Drag data if present or row data otherwise.
     */
    private JsonObject getDragData(JsonObject row) {
        return row.hasKey(GridDragSourceExtensionState.JSONKEY_DRAG_DATA)
                ? row.getObject(GridDragSourceExtensionState.JSONKEY_DRAG_DATA)
                : row;
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        // Remove draggable from all row elements in the escalator
        Range visibleRange = getEscalator().getVisibleRowRange();
        for (int i = visibleRange.getStart(); i < visibleRange.getEnd(); i++) {
            removeDraggable(getGridBody().getRowElement(i));
        }

        // Remove drag listeners from body element
        removeDragListeners(getGridBody().getElement());

        // Remove callback for newly added rows
        getGridBody().setNewEscalatorRowCallback(null);
    }

    private Grid<JsonObject> getGrid() {
        return gridConnector.getWidget();
    }

    private Escalator getEscalator() {
        return getGrid().getEscalator();
    }

    private RowContainer.BodyRowContainer getGridBody() {
        return getEscalator().getBody();
    }

    @Override
    public GridDragSourceExtensionState getState() {
        return (GridDragSourceExtensionState) super.getState();
    }
}
