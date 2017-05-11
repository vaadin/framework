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

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.extensions.DragSourceExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Range;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.GridDragSourceRpc;
import com.vaadin.shared.ui.grid.GridDragSourceState;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.GridDragSource;

import elemental.events.Event;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * Adds HTML5 drag and drop functionality to a
 * {@link com.vaadin.client.widgets.Grid Grid}'s rows. This is the client side
 * counterpart of {@link GridDragSource}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(GridDragSource.class)
public class GridDragSourceConnector extends DragSourceExtensionConnector {

    private static final String STYLE_SUFFIX_DRAG_BADGE = "-drag-badge";

    private GridConnector gridConnector;

    /**
     * List of dragged item keys.
     */
    private List<String> draggedItemKeys;

    @Override
    protected void extend(ServerConnector target) {
        gridConnector = (GridConnector) target;

        // HTML5 DnD is by default not enabled for mobile devices
        if (BrowserInfo.get().isTouchDevice() && !getConnection()
                .getUIConnector().isMobileHTML5DndEnabled()) {
            return;
        }

        // Set newly added rows draggable
        getGridBody().setNewEscalatorRowCallback(
                rows -> rows.forEach(this::addDraggable));

        // Add drag listeners to body element
        addDragListeners(getGridBody().getElement());
    }

    @Override
    protected void onDragStart(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        // Do not allow drag starts from native Android Chrome, since it doesn't
        // work properly (doesn't fire dragend reliably)
        if (isAndoidChrome() && isNativeDragEvent(nativeEvent)) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        // Collect the keys of dragged rows
        draggedItemKeys = getDraggedRows(nativeEvent).stream()
                .map(row -> row.getString(GridState.JSONKEY_ROWKEY))
                .collect(Collectors.toList());

        // Ignore event if there are no items dragged
        if (draggedItemKeys.size() == 0) {
            return;
        }

        super.onDragStart(event);
    }

    @Override
    protected void setDragImage(NativeEvent dragStartEvent) {
        // do not call super since need to handle specifically
        // 1. use resource if set (never needs safari hack)
        // 2. add number badge if necessary (with safari hack if needed)
        // 3. just use normal (with safari hack if needed)

        // Add badge showing the number of dragged columns
        String imageUrl = getResourceUrl(DragSourceState.RESOURCE_DRAG_IMAGE);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image dragImage = new Image(
                    getConnection().translateVaadinUri(imageUrl));
            dragStartEvent.getDataTransfer()
                    .setDragImage(dragImage.getElement(), 0, 0);
        } else {
            Element draggedRowElement = (Element) dragStartEvent
                    .getEventTarget().cast();
            if (draggedItemKeys.size() > 1) {

                Element badge = DOM.createSpan();
                badge.setClassName(
                        gridConnector.getWidget().getStylePrimaryName() + "-row"
                                + STYLE_SUFFIX_DRAG_BADGE);
                badge.setInnerHTML(draggedItemKeys.size() + "");

                badge.getStyle().setMarginLeft(
                        getRelativeX(draggedRowElement, dragStartEvent) + 10,
                        Style.Unit.PX);
                badge.getStyle().setMarginTop(
                        getRelativeY(draggedRowElement, dragStartEvent)
                                - draggedRowElement.getOffsetHeight() + 10,
                        Style.Unit.PX);

                draggedRowElement.appendChild(badge);

                // Remove badge on the next animation frame. Drag image will
                // still contain the badge.
                AnimationScheduler.get().requestAnimationFrame(timestamp -> {
                    badge.removeFromParent();
                }, (Element) dragStartEvent.getEventTarget().cast());
            }
            fixDragImageForSafari(draggedRowElement);
        }
    }

    private int getRelativeY(Element element, NativeEvent event) {
        int relativeTop = element.getAbsoluteTop() - Window.getScrollTop();
        return WidgetUtil.getTouchOrMouseClientY(event) - relativeTop;
    }

    private int getRelativeX(Element element, NativeEvent event) {
        int relativeLeft = element.getAbsoluteLeft() - Window.getScrollLeft();
        return WidgetUtil.getTouchOrMouseClientX(event) - relativeLeft;
    }

    @Override
    protected String createDataTransferText(NativeEvent dragStartEvent) {
        JsonArray dragData = toJsonArray(getDraggedRows(dragStartEvent).stream()
                .map(this::getDragData).collect(Collectors.toList()));
        return dragData.toJson();
    }

    @Override
    protected void sendDragStartEventToServer(NativeEvent dragStartEvent) {

        // Start server RPC with dragged item keys
        getRpcProxy(GridDragSourceRpc.class).dragStart(draggedItemKeys);
    }

    private List<JsonObject> getDraggedRows(NativeEvent dragStartEvent) {
        List<JsonObject> draggedRows = new ArrayList<>();

        if (TableRowElement.is(dragStartEvent.getEventTarget())) {
            TableRowElement row = (TableRowElement) dragStartEvent
                    .getEventTarget().cast();
            int rowIndex = ((Escalator.AbstractRowContainer) getGridBody())
                    .getLogicalRowIndex(row);

            JsonObject rowData = gridConnector.getDataSource().getRow(rowIndex);

            if (dragMultipleRows(rowData)) {
                getSelectedVisibleRows().forEach(draggedRows::add);
            } else {
                draggedRows.add(rowData);
            }
        }

        return draggedRows;
    }

    @Override
    protected void onDragEnd(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        // for android chrome we use the polyfill, in case browser fires a
        // native dragend event after the polyfill, we need to ignore that one
        if (isAndoidChrome() && isNativeDragEvent((nativeEvent))) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        // Ignore event if there are no items dragged
        if (draggedItemKeys != null && draggedItemKeys.size() > 0) {
            super.onDragEnd(event);
        }

        // Clear item key list
        draggedItemKeys = null;
    }

    @Override
    protected void sendDragEndEventToServer(NativeEvent dragEndEvent,
            DropEffect dropEffect) {

        // Send server RPC with dragged item keys
        getRpcProxy(GridDragSourceRpc.class).dragEnd(dropEffect,
                draggedItemKeys);
    }

    /**
     * Tells if multiple rows are dragged. Returns true if multiple selection is
     * allowed and a selected row is dragged.
     *
     * @param draggedRow
     *            Data of dragged row.
     * @return {@code true} if multiple rows are dragged, {@code false}
     *         otherwise.
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
     *            Range of indexes.
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
     *            List of json objects.
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
     *            Row data.
     * @return Drag data if present or row data otherwise.
     */
    private JsonObject getDragData(JsonObject row) {
        return row.hasKey(GridDragSourceState.JSONKEY_DRAG_DATA)
                ? row.getObject(GridDragSourceState.JSONKEY_DRAG_DATA) : row;
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
    public GridDragSourceState getState() {
        return (GridDragSourceState) super.getState();
    }
}
