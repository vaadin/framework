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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableRowElement;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.DragSourceExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.shared.Range;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridDragSourceExtensionState;
import com.vaadin.ui.GridDragSourceExtension;

import elemental.events.Event;
import elemental.json.JsonObject;

/**
 * Adds HTML5 drag and drop functionality to a {@link com.vaadin.client.widgets.Grid
 * Grid}'s rows. This is the client side counterpart of {@link
 * GridDragSourceExtension}.
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

            ((NativeEvent) event).getDataTransfer()
                    .setData(GridDragSourceExtensionState.DATA_TYPE_ROW_DATA,
                            rowData.toJson());
        }
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
    }

    private Escalator getEscalator() {
        return gridConnector.getWidget().getEscalator();
    }

    private RowContainer.BodyRowContainer getGridBody() {
        return getEscalator().getBody();
    }

    @Override
    public GridDragSourceExtensionState getState() {
        return (GridDragSourceExtensionState) super.getState();
    }
}
