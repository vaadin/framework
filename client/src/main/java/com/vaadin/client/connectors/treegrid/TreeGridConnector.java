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
package com.vaadin.client.connectors.treegrid;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.IntStream;

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
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widget.treegrid.TreeGrid;
import com.vaadin.client.widget.treegrid.events.TreeGridClickEvent;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Range;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.treegrid.FocusParentRpc;
import com.vaadin.shared.ui.treegrid.FocusRpc;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
import com.vaadin.shared.ui.treegrid.TreeGridClientRpc;
import com.vaadin.shared.ui.treegrid.TreeGridCommunicationConstants;
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

    public TreeGridConnector() {
        registerRpc(FocusRpc.class, (rowIndex, cellIndex) -> {
            getWidget().focusCell(rowIndex, cellIndex);
        });
    }

    private String hierarchyColumnId;

    private HierarchyRenderer hierarchyRenderer;

    private Set<String> pendingExpansion = new HashSet<>();

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
            if (newHierarchyColumnId == null) {
                newHierarchyColumnId = getState().columnOrder.get(0);
            }

            // Columns
            Grid.Column<?, ?> newColumn = getColumn(newHierarchyColumnId);
            Grid.Column<?, ?> oldColumn = getColumn(oldHierarchyColumnId);

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
            hierarchyRenderer = new HierarchyRenderer(this::setCollapsed);
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

        // Swap Grid#clickEvent field
        // The event is identical to the original one except for the child
        // widget check
        replaceClickEvent(getWidget(),
                new TreeGridClickEvent(getWidget(), getEventCell(getWidget())));

        registerRpc(TreeGridClientRpc.class, new TreeGridClientRpc() {

            @Override
            public void setExpanded(String key) {
                pendingExpansion.add(key);
                Range cache = ((AbstractRemoteDataSource) getDataSource())
                        .getCachedRange();
                checkExpand(cache.getStart(), cache.length());
            }

            @Override
            public void setCollapsed(String key) {
                pendingExpansion.remove(key);
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
                // NO-OP
            }

            @Override
            public void dataAdded(int firstRowIndex, int numberOfRows) {
                // NO-OP
            }

            @Override
            public void dataAvailable(int firstRowIndex, int numberOfRows) {
                // NO-OP
            }

            @Override
            public void resetDataAndSize(int estimatedNewDataSize) {
                // NO-OP
            }
        });
    }

    private native void replaceCellFocusEventHandler(Grid<?> grid,
            GridEventHandler<?> eventHandler)
    /*-{
        var browserEventHandlers = grid.@com.vaadin.client.widgets.Grid::browserEventHandlers;

        // FocusEventHandler is initially 5th in the list of browser event handlers
        browserEventHandlers.@java.util.List::set(*)(5, eventHandler);
    }-*/;

    private native void replaceClickEvent(Grid<?> grid, GridClickEvent event)
    /*-{
        grid.@com.vaadin.client.widgets.Grid::clickEvent = event;
    }-*/;

    private native EventCellReference<?> getEventCell(Grid<?> grid)
    /*-{
        return grid.@com.vaadin.client.widgets.Grid::eventCell;
    }-*/;

    private boolean isHierarchyColumn(EventCellReference<JsonObject> cell) {
        return cell.getColumn().getRenderer() instanceof HierarchyRenderer;
    }

    private void setCollapsed(int rowIndex, boolean collapsed) {
        String rowKey = getRowKey(getDataSource().getRow(rowIndex));
        getRpcProxy(NodeCollapseRpc.class).setNodeCollapsed(rowKey, rowIndex,
                collapsed, true);
    }

    private void setCollapsedServerInitiated(int rowIndex, boolean collapsed) {
        String rowKey = getRowKey(getDataSource().getRow(rowIndex));
        getRpcProxy(NodeCollapseRpc.class).setNodeCollapsed(rowKey, rowIndex,
                collapsed, false);
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
                        TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION)) {
                    JsonObject rowDescription = rowData.getObject(
                            TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION);
                    boolean leaf = rowDescription.getBoolean(
                            TreeGridCommunicationConstants.ROW_LEAF);
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

    private void checkExpand(int firstRowIndex, int numberOfRows) {
        if (pendingExpansion.isEmpty()) {
            return;
        }
        IntStream.range(firstRowIndex, firstRowIndex + numberOfRows)
                .forEach(rowIndex -> {
                    String rowKey = getDataSource().getRow(rowIndex)
                            .getString(DataCommunicatorConstants.KEY);
                    if (pendingExpansion.remove(rowKey)) {
                        setCollapsedServerInitiated(rowIndex, false);
                    }
                });
    }

    private static boolean isCollapsed(JsonObject rowData) {
        assert rowData
                .hasKey(TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION) : "missing hierarchy data for row "
                        + rowData.asString();
        return rowData
                .getObject(
                        TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION)
                .getBoolean(TreeGridCommunicationConstants.ROW_COLLAPSED);
    }

    /**
     * Checks if the item can be collapsed
     *
     * @param row the item row
     * @return {@code true} if the item is allowed to be collapsed, {@code false} otherwise.
     */
    public static boolean isCollapseAllowed(JsonObject row) {
        return row.getBoolean(
                TreeGridCommunicationConstants.ROW_COLLAPSE_ALLOWED);
    }
}
