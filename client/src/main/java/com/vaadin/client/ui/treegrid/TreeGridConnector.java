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
package com.vaadin.client.ui.treegrid;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.connectors.grid.GridConnector;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.renderers.HierarchyRenderer;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.GridEventHandler;
import com.vaadin.client.widget.treegrid.TreeGrid;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Range;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.HierarchicalDataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.treegrid.FocusParentRpc;
import com.vaadin.shared.ui.treegrid.FocusRpc;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
import com.vaadin.shared.ui.treegrid.TreeGridClientRpc;
import com.vaadin.shared.ui.treegrid.TreeGridState;

import elemental.json.JsonObject;

/**
 * A connector class for the TreeGrid component.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(com.vaadin.ui.TreeGrid.class)
public class TreeGridConnector extends GridConnector {

    private static enum AwaitingRowsState {
        NONE, COLLAPSE, EXPAND
    }

    public TreeGridConnector() {
        registerRpc(FocusRpc.class, (rowIndex, cellIndex) -> {
            getWidget().focusCell(rowIndex, cellIndex);
        });
    }

    private String hierarchyColumnId;

    private HierarchyRenderer hierarchyRenderer;

    private Set<String> rowKeysPendingExpand = new HashSet<>();

    private AwaitingRowsState awaitingRowsState = AwaitingRowsState.NONE;

    @Override
    public TreeGrid getWidget() {
        return (TreeGrid) super.getWidget();
    }

    @Override
    public TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    /**
     * This method has been scheduled finally to avoid possible race conditions
     * between state change handling for the Grid and its columns. The renderer
     * of the column is set in a state change handler, and might not be
     * available when this method is executed.
     * <p>
     * TODO: This might need some clean up if we decide to allow setting a new
     * renderer for hierarchy columns.
     */
    @OnStateChange("hierarchyColumnId")
    void updateHierarchyColumn() {
        Scheduler.get().scheduleFinally(() -> {
            // Id of old hierarchy column
            String oldHierarchyColumnId = hierarchyColumnId;

            // Id of new hierarchy column. Choose first when nothing explicitly
            // set
            String newHierarchyColumnId = getState().hierarchyColumnId;
            if (newHierarchyColumnId == null
                    && !getState().columnOrder.isEmpty()) {
                newHierarchyColumnId = getState().columnOrder.get(0);
            }

            // Columns
            Grid.Column<?, ?> newColumn = getColumn(newHierarchyColumnId);
            Grid.Column<?, ?> oldColumn = getColumn(oldHierarchyColumnId);

            if (newColumn == null && oldColumn == null) {
                // No hierarchy column defined
                return;
            }

            // Unwrap renderer of old column
            if (oldColumn != null
                    && oldColumn.getRenderer() instanceof HierarchyRenderer) {
                oldColumn.setRenderer(
                        ((HierarchyRenderer) oldColumn.getRenderer())
                                .getInnerRenderer());
            }

            // Wrap renderer of new column
            if (newColumn != null) {
                HierarchyRenderer wrapperRenderer = getHierarchyRenderer();
                wrapperRenderer.setInnerRenderer(newColumn.getRenderer());
                newColumn.setRenderer(wrapperRenderer);

                // Set frozen columns again after setting hierarchy column as
                // setRenderer() replaces DOM elements
                getWidget().setFrozenColumnCount(getState().frozenColumnCount);

                hierarchyColumnId = newHierarchyColumnId;
            } else {
                Logger.getLogger(TreeGridConnector.class.getName()).warning(
                        "Couldn't find column: " + newHierarchyColumnId);
            }
        });
    }

    private HierarchyRenderer getHierarchyRenderer() {
        if (hierarchyRenderer == null) {
            hierarchyRenderer = new HierarchyRenderer(this::setCollapsed,
                    getState().primaryStyleName);
        }
        return hierarchyRenderer;
    }

    @Override
    protected void init() {
        super.init();

        // Swap Grid's CellFocusEventHandler to this custom one
        // The handler is identical to the original one except for the child
        // widget check
        replaceCellFocusEventHandler(getWidget(), new CellFocusEventHandler());

        getWidget().addBrowserEventHandler(5, new NavigationEventHandler());

        registerRpc(TreeGridClientRpc.class, new TreeGridClientRpc() {

            @Override
            public void setExpanded(List<String> keys) {
                rowKeysPendingExpand.addAll(keys);
                checkExpand();
            }

            @Override
            public void setCollapsed(List<String> keys) {
                rowKeysPendingExpand.removeAll(keys);
            }

            @Override
            public void clearPendingExpands() {
                rowKeysPendingExpand.clear();
            }
        });
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        super.setDataSource(dataSource);
        dataSource.addDataChangeHandler(new DataChangeHandler() {

            @Override
            public void dataUpdated(int firstRowIndex, int numberOfRows) {
                checkExpand(firstRowIndex, numberOfRows);
            }

            @Override
            public void dataRemoved(int firstRowIndex, int numberOfRows) {
                if (awaitingRowsState == AwaitingRowsState.COLLAPSE) {
                    awaitingRowsState = AwaitingRowsState.NONE;
                }
                checkExpand();
            }

            @Override
            public void dataAdded(int firstRowIndex, int numberOfRows) {
                if (awaitingRowsState == AwaitingRowsState.EXPAND) {
                    awaitingRowsState = AwaitingRowsState.NONE;
                }
                checkExpand();
            }

            @Override
            public void dataAvailable(int firstRowIndex, int numberOfRows) {
                // NO-OP
            }

            @Override
            public void resetDataAndSize(int estimatedNewDataSize) {
                awaitingRowsState = AwaitingRowsState.NONE;
            }
        });
    }

    @OnStateChange("primaryStyleName")
    private void updateHierarchyRendererStyleName() {
        getHierarchyRenderer().setStyleNames(getState().primaryStyleName);
    }

    private native void replaceCellFocusEventHandler(Grid<?> grid,
            GridEventHandler<?> eventHandler)
    /*-{
        var browserEventHandlers = grid.@com.vaadin.client.widgets.Grid::browserEventHandlers;

        // FocusEventHandler is initially 5th in the list of browser event handlers
        browserEventHandlers.@java.util.List::set(*)(5, eventHandler);
    }-*/;

    private native EventCellReference<?> getEventCell(Grid<?> grid)
    /*-{
        return grid.@com.vaadin.client.widgets.Grid::eventCell;
    }-*/;

    private boolean isHierarchyColumn(EventCellReference<JsonObject> cell) {
        return cell.getColumn().getRenderer() instanceof HierarchyRenderer;
    }

    /**
     * Delegates to {@link #setCollapsed(int, boolean, boolean)}, with
     * {@code userOriginated} as {@code true}.
     *
     * @see #setCollapsed(int, boolean, boolean)
     */
    private void setCollapsed(int rowIndex, boolean collapsed) {
        setCollapsed(rowIndex, collapsed, true);
    }

    /**
     * Set the collapse state for the row in the given index.
     * <p>
     * Calling this method will have no effect if a response has not yet been
     * received for a previous call to this method.
     *
     * @param rowIndex
     *            index of the row to set the state for
     * @param collapsed
     *            {@code true} to collapse the row, {@code false} to expand the
     *            row
     * @param userOriginated
     *            whether this method was originated from a user interaction
     */
    private void setCollapsed(int rowIndex, boolean collapsed,
            boolean userOriginated) {
        if (isAwaitingRowChange()) {
            return;
        }
        if (collapsed) {
            awaitingRowsState = AwaitingRowsState.COLLAPSE;
        } else {
            awaitingRowsState = AwaitingRowsState.EXPAND;
        }
        String rowKey = getRowKey(getDataSource().getRow(rowIndex));
        getRpcProxy(NodeCollapseRpc.class).setNodeCollapsed(rowKey, rowIndex,
                collapsed, userOriginated);
    }

    /**
     * Class to replace
     * {@link com.vaadin.client.widgets.Grid.CellFocusEventHandler}. The only
     * difference is that it handles events originated from widgets in hierarchy
     * cells.
     */
    private class CellFocusEventHandler
            implements GridEventHandler<JsonObject> {
        @Override
        public void onEvent(Grid.GridEvent<JsonObject> event) {
            Element target = Element.as(event.getDomEvent().getEventTarget());
            boolean elementInChildWidget = getWidget()
                    .isElementInChildWidget(target);

            // Ignore if event was handled by keyboard navigation handler
            if (event.isHandled() && !elementInChildWidget) {
                return;
            }

            // Ignore target in child widget but handle hierarchy widget
            if (elementInChildWidget
                    && !HierarchyRenderer.isElementInHierarchyWidget(target)) {
                return;
            }

            Collection<String> navigation = getNavigationEvents(getWidget());
            if (navigation.contains(event.getDomEvent().getType())) {
                handleNavigationEvent(getWidget(), event);
            }
        }

        private native Collection<String> getNavigationEvents(Grid<?> grid)
        /*-{
            return grid.@com.vaadin.client.widgets.Grid::cellFocusHandler
            .@com.vaadin.client.widgets.Grid.CellFocusHandler::getNavigationEvents()();
        }-*/;

        private native void handleNavigationEvent(Grid<?> grid,
                Grid.GridEvent<JsonObject> event)
        /*-{
            grid.@com.vaadin.client.widgets.Grid::cellFocusHandler
            .@com.vaadin.client.widgets.Grid.CellFocusHandler::handleNavigationEvent(*)(
            event.@com.vaadin.client.widgets.Grid.GridEvent::getDomEvent()(),
            event.@com.vaadin.client.widgets.Grid.GridEvent::getCell()())
        }-*/;
    }

    private class NavigationEventHandler
            implements GridEventHandler<JsonObject> {

        @Override
        public void onEvent(Grid.GridEvent<JsonObject> event) {
            if (event.isHandled()) {
                return;
            }

            Event domEvent = event.getDomEvent();
            if (!domEvent.getType().equals(BrowserEvents.KEYDOWN)) {
                return;
            }

            // Navigate within hierarchy with ARROW KEYs
            if (domEvent.getKeyCode() == KeyCodes.KEY_LEFT
                    || domEvent.getKeyCode() == KeyCodes.KEY_RIGHT) {

                event.setHandled(true);
                EventCellReference<JsonObject> cell = event.getCell();
                // Hierarchy metadata
                JsonObject rowData = cell.getRow();
                if (rowData == null) {
                    // Row data is lost from the cache, i.e. the row is at least outside the visual area,
                    // let's scroll the row into the view
                    getWidget().scrollToRow(cell.getRowIndex());
                } else if (rowData.hasKey(
                        HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION)) {
                    JsonObject rowDescription = rowData.getObject(
                            HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION);
                    boolean leaf = rowDescription.getBoolean(
                            HierarchicalDataCommunicatorConstants.ROW_LEAF);
                    boolean collapsed = isCollapsed(rowData);
                    switch (domEvent.getKeyCode()) {
                        case KeyCodes.KEY_RIGHT:
                            if (collapsed && !leaf) {
                                setCollapsed(cell.getRowIndex(), false);
                            }
                            break;
                        case KeyCodes.KEY_LEFT:
                            if (collapsed || leaf) {
                                // navigate up
                                int columnIndex = cell.getColumnIndex();
                                getRpcProxy(FocusParentRpc.class).focusParent(
                                        cell.getRowIndex(), columnIndex);
                            } else if (isCollapseAllowed(rowDescription)) {
                                setCollapsed(cell.getRowIndex(), true);
                            }
                            break;
                    }

                }
            }
        }
    }

    private boolean isAwaitingRowChange() {
        return awaitingRowsState != AwaitingRowsState.NONE;
    }

    private void checkExpand() {
        Range cache = ((AbstractRemoteDataSource) getDataSource())
                .getCachedRange();
        checkExpand(cache.getStart(), cache.length());
    }

    private void checkExpand(int firstRowIndex, int numberOfRows) {
        if (rowKeysPendingExpand.isEmpty() || isAwaitingRowChange()) {
            // will not perform the check if an expand or collapse action is
            // already pending or there are no rows pending expand
            return;
        }
        for (int rowIndex = firstRowIndex; rowIndex < firstRowIndex
                + numberOfRows; rowIndex++) {
            String rowKey = getDataSource().getRow(rowIndex)
                    .getString(DataCommunicatorConstants.KEY);
            if (rowKeysPendingExpand.remove(rowKey)) {
                setCollapsed(rowIndex, false, false);
                return;
            }
        }
    }

    private static boolean isCollapsed(JsonObject rowData) {
        assert rowData
                .hasKey(HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION) : "missing hierarchy data for row "
                        + rowData.asString();
        return rowData
                .getObject(
                        HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION)
                .getBoolean(HierarchicalDataCommunicatorConstants.ROW_COLLAPSED);
    }

    /**
     * Checks if the item can be collapsed
     *
     * @param row the item row
     * @return {@code true} if the item is allowed to be collapsed, {@code false} otherwise.
     */
    public static boolean isCollapseAllowed(JsonObject row) {
        return row.getBoolean(
                HierarchicalDataCommunicatorConstants.ROW_COLLAPSE_ALLOWED);
    }
}
