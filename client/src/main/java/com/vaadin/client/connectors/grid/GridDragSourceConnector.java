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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.extensions.DragSourceExtensionConnector;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widgets.Escalator;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Range;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.grid.GridDragSourceRpc;
import com.vaadin.shared.ui.grid.GridDragSourceState;
import com.vaadin.ui.components.grid.GridDragSource;

import elemental.events.Event;
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

    /**
     * Delay used to distinct between scroll and drag start in grid: if the user
     * doens't move the finger before this "timeout", it should be considered as
     * a drag start.
     * <p>
     * This default value originates from VScrollTable which uses it to
     * distinguish between scroll and context click (long tap).
     *
     * @see Escalator#setDelayToCancelTouchScroll(double)
     */
    private static final int TOUCH_SCROLL_TIMEOUT_DELAY = 500;

    private static final String STYLE_SUFFIX_DRAG_BADGE = "-drag-badge";

    private GridConnector gridConnector;

    /**
     * List of dragged items.
     */
    private List<JsonObject> draggedItems;

    private boolean touchScrollDelayUsed;

    private String draggedStyleName;

    @Override
    protected void extend(ServerConnector target) {
        gridConnector = (GridConnector) target;

        // HTML5 DnD is by default not enabled for mobile devices
        if (BrowserInfo.get().isTouchDevice()) {
            if (getConnection().getUIConnector().isMobileHTML5DndEnabled()) {
                // distinct between scroll and drag start
                gridConnector.getWidget().getEscalator()
                        .setDelayToCancelTouchScroll(
                                TOUCH_SCROLL_TIMEOUT_DELAY);
                touchScrollDelayUsed = true;
            } else {
                return;
            }
        }

        // Set newly added rows draggable
        getGridBody()
                .setNewRowCallback(rows -> rows.forEach(this::addDraggable));

        // Add drag listeners to body element
        addDragListeners(getGridBody().getElement());

        gridConnector.onDragSourceAttached();
    }

    @Override
    protected void onDragStart(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;

        // Make sure user is not actually scrolling
        if (touchScrollDelayUsed && gridConnector.getWidget().getEscalator()
                .isTouchScrolling()) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        // Do not allow drag starts from native Android Chrome, since it doesn't
        // work properly (doesn't fire dragend reliably)
        if (isAndoidChrome() && isNativeDragEvent(nativeEvent)) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        // Collect the keys of dragged rows
        draggedItems = getDraggedRows(nativeEvent);

        // Ignore event if there are no items dragged
        if (draggedItems.size() == 0) {
            return;
        }

        // Construct style name to be added to dragged rows
        draggedStyleName = gridConnector.getWidget().getStylePrimaryName()
                + "-row" + STYLE_SUFFIX_DRAGGED;

        super.onDragStart(event);
    }

    @Override
    protected void setDragImage(NativeEvent dragStartEvent) {
        // do not call super since need to handle specifically
        // 1. use resource if set (never needs safari hack)
        // 2. add row count badge if necessary
        // 3. apply hacks for safari/mobile drag image if needed

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
            Element badge;
            if (draggedItems.size() > 1) {
                badge = DOM.createSpan();
                badge.setClassName(
                        gridConnector.getWidget().getStylePrimaryName() + "-row"
                                + STYLE_SUFFIX_DRAG_BADGE);
                badge.setInnerHTML(draggedItems.size() + "");

                BrowserInfo browserInfo = BrowserInfo.get();
                if (browserInfo.isTouchDevice()) {
                    // the drag image is centered on the touch coordinates
                    // -> show the badge on the right edge of the row
                    badge.getStyle().setFloat(Float.RIGHT);
                    badge.getStyle().setMarginRight(20, Unit.PX);
                    badge.getStyle().setMarginTop(-20, Unit.PX);
                } else if (browserInfo.isSafari()) {
                    // On Safari, only the part of the row visible inside grid
                    // is shown, and also the badge needs to be totally on top
                    // of the row.
                    Element tableWrapperDiv = getGridBody().getElement()
                            .getParentElement().getParentElement();
                    int mouseXRelativeToGrid = WidgetUtil
                            .getRelativeX(tableWrapperDiv, dragStartEvent);
                    if (mouseXRelativeToGrid < (tableWrapperDiv.getClientWidth()
                            - 60)) {
                        badge.getStyle().setMarginLeft(
                                mouseXRelativeToGrid + 10, Unit.PX);
                    } else {
                        badge.getStyle().setMarginLeft(
                                mouseXRelativeToGrid - 60, Unit.PX);
                    }
                    badge.getStyle().setMarginTop(-32, Unit.PX);
                } else {
                    badge.getStyle().setMarginLeft(WidgetUtil.getRelativeX(
                            draggedRowElement, dragStartEvent) + 10, Unit.PX);
                    badge.getStyle().setMarginTop(-20, Unit.PX);
                }
            } else {
                badge = null;
            }

            final int frozenColumnCount = getGrid().getFrozenColumnCount();
            final Element selectionColumnCell = getGrid().getSelectionColumn()
                    .isPresent()
                    // -1 is used when even selection column is not frozen
                    && frozenColumnCount != -1 ? draggedRowElement
                            .removeChild(draggedRowElement.getFirstChild())
                            .cast() : null;

            final List<String> frozenCellsTransforms = new ArrayList<>();
            for (int i = 0; i < getGrid().getColumnCount(); i++) {
                if (i >= frozenColumnCount) {
                    break;
                }
                if (getGrid().getColumn(i).isHidden()) {
                    frozenCellsTransforms.add(null);
                    continue;
                }
                Style style = ((Element) draggedRowElement.getChild(i).cast())
                        .getStyle();
                frozenCellsTransforms.add(style.getProperty("transform"));
                style.clearProperty("transform");
            }

            if (badge != null) {
                draggedRowElement.appendChild(badge);
            }

            // The following hack is used since IE11 doesn't support custom drag
            // image.
            // 1. Remove multiple rows drag badge, if used
            // 2. add selection column cell back, if was removed
            // 3. reset frozen column transitions, if were cleared
            AnimationScheduler.get().requestAnimationFrame(timestamp -> {
                if (badge != null) {
                    badge.removeFromParent();
                }
                for (int i = 0; i < frozenCellsTransforms.size(); i++) {
                    String transform = frozenCellsTransforms.get(i);
                    if (transform != null) {
                        ((Element) draggedRowElement.getChild(i).cast())
                                .getStyle().setProperty("transform", transform);
                    }
                }
                if (selectionColumnCell != null) {
                    draggedRowElement.insertFirst(selectionColumnCell);
                }
            }, (Element) dragStartEvent.getEventTarget().cast());

            fixDragImageOffsetsForDesktop(dragStartEvent, draggedRowElement);
            fixDragImageTransformForMobile(draggedRowElement);
        }

    }

    @Override
    protected Map<String, String> createDataTransferData(
            NativeEvent dragStartEvent) {
        Map<String, String> dataMap = super.createDataTransferData(
                dragStartEvent);

        // Add data provided by the generator functions
        getDraggedRows(dragStartEvent).forEach(row -> {
            Map<String, String> rowDragData = getRowDragData(row);
            rowDragData.forEach((type, data) -> {
                if (!(data == null || data.isEmpty())) {
                    if (!dataMap.containsKey(type)) {
                        dataMap.put(type, data);
                    } else {
                        // Separate data with new line character when multiple
                        // rows are dragged
                        dataMap.put(type, dataMap.get(type) + "\n" + data);
                    }
                }
            });
        });

        return dataMap;
    }

    @Override
    protected void sendDragStartEventToServer(NativeEvent dragStartEvent) {

        // Start server RPC with dragged item keys
        getRpcProxy(GridDragSourceRpc.class).dragStart(draggedItems.stream()
                .map(row -> row.getString(DataCommunicatorConstants.KEY))
                .collect(Collectors.toList()));
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
        if (draggedItems != null && draggedItems.size() > 0) {
            super.onDragEnd(event);
        }

        // Clear item list
        draggedItems = null;
    }

    @Override
    protected void sendDragEndEventToServer(NativeEvent dragEndEvent,
            DropEffect dropEffect) {

        // Send server RPC with dragged item keys
        getRpcProxy(GridDragSourceRpc.class).dragEnd(dropEffect,
                draggedItems.stream().map(
                        row -> row.getString(DataCommunicatorConstants.KEY))
                        .collect(Collectors.toList()));
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
     * Gets drag data provided by the generator functions.
     *
     * @param row
     *            The row data.
     * @return The generated drag data type mapped to the corresponding drag
     *         data. If there are no generator functions, returns an empty map.
     */
    private Map<String, String> getRowDragData(JsonObject row) {
        // Collect a map of data types and data that is provided by the
        // generator functions set for this drag source
        if (row.hasKey(GridDragSourceState.JSONKEY_DRAG_DATA)) {
            JsonObject dragData = row
                    .getObject(GridDragSourceState.JSONKEY_DRAG_DATA);
            return Arrays.stream(dragData.keys()).collect(
                    Collectors.toMap(Function.identity(), dragData::get));
        }

        // Otherwise return empty map
        return Collections.emptyMap();
    }

    /**
     * Add {@code v-grid-row-dragged} class name to each row being dragged.
     *
     * @param event
     *            The dragstart event.
     */
    @Override
    protected void addDraggedStyle(NativeEvent event) {
        getDraggedRowElementStream().forEach(
                rowElement -> rowElement.addClassName(draggedStyleName));
    }

    /**
     * Remove {@code v-grid-row-dragged} class name from dragged rows.
     *
     * @param event
     *            The dragend event.
     */
    @Override
    protected void removeDraggedStyle(NativeEvent event) {
        getDraggedRowElementStream().forEach(
                rowElement -> rowElement.removeClassName(draggedStyleName));
    }

    /**
     * Get the dragged table row elements as a stream.
     *
     * @return Stream of dragged table row elements.
     */
    private Stream<TableRowElement> getDraggedRowElementStream() {
        return draggedItems.stream()
                .map(row -> ((AbstractRemoteDataSource<JsonObject>) gridConnector
                        .getDataSource()).indexOf(row))
                .map(getGridBody()::getRowElement);
    }

    @Override
    public void onUnregister() {
        // Remove draggable from all row elements in the escalator
        Range visibleRange = getEscalator().getVisibleRowRange();
        for (int i = visibleRange.getStart(); i < visibleRange.getEnd(); i++) {
            removeDraggable(getGridBody().getRowElement(i));
        }

        // Remove drag listeners from body element
        removeDragListeners(getGridBody().getElement());

        // Remove callback for newly added rows
        getGridBody().setNewRowCallback(null);

        if (touchScrollDelayUsed) {
            gridConnector.getWidget().getEscalator()
                    .setDelayToCancelTouchScroll(-1);
            touchScrollDelayUsed = false;
        }

        super.onUnregister();
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
