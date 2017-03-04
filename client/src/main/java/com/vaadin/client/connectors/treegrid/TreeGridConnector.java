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
import java.util.logging.Logger;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.connectors.grid.GridConnector;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.renderers.HierarchyRenderer;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.GridEventHandler;
import com.vaadin.client.widget.grid.events.GridClickEvent;
import com.vaadin.client.widget.treegrid.TreeGrid;
import com.vaadin.client.widget.treegrid.events.TreeGridClickEvent;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.treegrid.NodeCollapseRpc;
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

    private String hierarchyColumnId;

    private HierarchyRenderer hierarchyRenderer;

    // Expander click event handling
    private HandlerRegistration expanderClickHandlerRegistration;

    @Override
    public TreeGrid getWidget() {
        return (TreeGrid) super.getWidget();
    }

    @Override
    public TreeGridState getState() {
        return (TreeGridState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("hierarchyColumnId")
                || stateChangeEvent.hasPropertyChanged("columns")) {

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
        }
    }

    private HierarchyRenderer getHierarchyRenderer() {
        if (hierarchyRenderer == null) {
            hierarchyRenderer = new HierarchyRenderer();
        }
        return hierarchyRenderer;
    }

    @Override
    protected void init() {
        super.init();

        expanderClickHandlerRegistration = getHierarchyRenderer()
                .addClickHandler(
                        new ClickableRenderer.RendererClickHandler<JsonObject>() {
                            @Override
                            public void onClick(
                                    ClickableRenderer.RendererClickEvent<JsonObject> event) {
                                toggleCollapse(getRowKey(event.getRow()),
                                        event.getCell().getRowIndex(),
                                        !isCollapsed(event.getRow()));
                                event.stopPropagation();
                                event.preventDefault();
                            }
                        });

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
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        expanderClickHandlerRegistration.removeHandler();
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

    private void toggleCollapse(String rowKey, int rowIndex, boolean collapse) {
        getRpcProxy(NodeCollapseRpc.class).toggleCollapse(rowKey, rowIndex,
                collapse);
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

            // Navigate within hierarchy with ALT/OPTION + ARROW KEY when
            // hierarchy column is selected
            if (isHierarchyColumn(event.getCell()) && domEvent.getAltKey()
                    && (domEvent.getKeyCode() == KeyCodes.KEY_LEFT
                            || domEvent.getKeyCode() == KeyCodes.KEY_RIGHT)) {

                // Hierarchy metadata
                boolean collapsed, leaf;
                JsonObject rowData = event.getCell().getRow();
                if (rowData.hasKey(
                        TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION)) {
                    collapsed = isCollapsed(rowData);
                    JsonObject rowDescription = rowData.getObject(
                            TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION);
                    leaf = rowDescription.getBoolean(
                            TreeGridCommunicationConstants.ROW_LEAF);
                    switch (domEvent.getKeyCode()) {
                    case KeyCodes.KEY_RIGHT:
                        if (!leaf && collapsed) {
                            toggleCollapse(getRowKey(rowData),
                                    event.getCell().getRowIndex(), true);
                        }
                        break;
                    case KeyCodes.KEY_LEFT:
                        if (!collapsed) {
                            // collapse node
                            toggleCollapse(getRowKey(rowData),
                                    event.getCell().getRowIndex(), false);
                        }
                        break;
                    }
                }
                event.setHandled(true);
                return;
            }
        }
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

    private static int getDepth(JsonObject rowData) {
        assert rowData
                .hasKey(TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION) : "missing hierarchy data for row "
                        + rowData.asString();
        return (int) rowData
                .getObject(
                        TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION)
                .getNumber(TreeGridCommunicationConstants.ROW_DEPTH);
    }
}
