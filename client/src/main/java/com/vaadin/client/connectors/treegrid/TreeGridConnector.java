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

import com.google.gwt.dom.client.Element;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.ServerConnector;
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
import com.vaadin.shared.ui.treegrid.TreeGridState;

import elemental.json.JsonObject;

/**
 * 
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(com.vaadin.ui.TreeGrid.class)
public class TreeGridConnector extends GridConnector {

    private String hierarchyColumnId;

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
            String oldHierarchyColumnId = this.hierarchyColumnId;

            // Id of new hierarchy column. Choose first when nothing explicitly
            // set
            String newHierarchyColumnId = null; // getState().hierarchyColumnId;
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

                this.hierarchyColumnId = newHierarchyColumnId;
            } else {
                Logger.getLogger(TreeGridConnector.class.getName()).warning(
                        "Couldn't find column: " + newHierarchyColumnId);
            }
        }
    }

    private HierarchyRenderer hierarchyRenderer;

    private HierarchyRenderer getHierarchyRenderer() {
        if (hierarchyRenderer == null) {
            hierarchyRenderer = new HierarchyRenderer();
        }
        return hierarchyRenderer;
    }

    // Expander click event handling

    private HandlerRegistration expanderClickHandlerRegistration;

    @Override
    protected void init() {
        super.init();

        expanderClickHandlerRegistration = getHierarchyRenderer()
                .addClickHandler(
                        new ClickableRenderer.RendererClickHandler<JsonObject>() {
                            @Override
                            public void onClick(
                                    ClickableRenderer.RendererClickEvent<JsonObject> event) {
                                TreeGridNavigationExtensionConnector navigation = getNavigationExtensionConnector();
                                if (navigation != null) {
                                    navigation.toggleCollapse(
                                            getRowKey(event.getRow()));
                                }

                                event.stopPropagation();
                                event.preventDefault();
                            }
                        });

        replaceMemberFields();
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        expanderClickHandlerRegistration.removeHandler();
    }

    private TreeGridNavigationExtensionConnector getNavigationExtensionConnector() {
        for (ServerConnector c : getChildren()) {
            if (c instanceof TreeGridNavigationExtensionConnector) {
                return (TreeGridNavigationExtensionConnector) c;
            }
        }
        return null;
    }

    /**
     * Replaces the following members
     * <ul>
     * <li>{@link com.vaadin.client.widgets.Grid.CellFocusEventHandler} as an
     * element of the {@link Grid#browserEventHandlers} list ->
     * {@link CellFocusEventHandler}</li>
     * <li>{@link Grid#clickEvent} field -> {@link TreeGridClickEvent}</li>
     * </ul>
     */
    private void replaceMemberFields() {

        // Swap Grid's CellFocusEventHandler to this custom one
        // The handler is identical to the original one except for the child
        // widget check
        replaceCellFocusEventHandler(getWidget(), new CellFocusEventHandler());

        // Swap Grid#clickEvent field
        // The event is identical to the original one except for the child
        // widget check
        replaceClickEvent(getWidget(),
                new TreeGridClickEvent(getWidget(), getEventCell(getWidget())));
    }

    private native void replaceCellFocusEventHandler(Grid<?> grid,
            GridEventHandler<?> eventHandler)/*-{
        var browserEventHandlers = grid.@com.vaadin.client.widgets.Grid::browserEventHandlers;
         
        // FocusEventHandler is initially 5th in the list of browser event handlers
        browserEventHandlers.@java.util.List::set(*)(5, eventHandler);
    }-*/;

    private native void replaceClickEvent(Grid<?> grid, GridClickEvent event)/*-{
        grid.@com.vaadin.client.widgets.Grid::clickEvent = event;
    }-*/;

    private native EventCellReference<?> getEventCell(Grid<?> grid)/*-{
        return grid.@com.vaadin.client.widgets.Grid::eventCell;
    }-*/;

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

        private native Collection<String> getNavigationEvents(Grid<?> grid)/*-{
           return grid.@com.vaadin.client.widgets.Grid::cellFocusHandler
           .@com.vaadin.client.widgets.Grid.CellFocusHandler::getNavigationEvents()();
        }-*/;

        private native void handleNavigationEvent(Grid<?> grid,
                Grid.GridEvent<JsonObject> event)/*-{
            grid.@com.vaadin.client.widgets.Grid::cellFocusHandler
            .@com.vaadin.client.widgets.Grid.CellFocusHandler::handleNavigationEvent(*)(
            event.@com.vaadin.client.widgets.Grid.GridEvent::getDomEvent()(),
            event.@com.vaadin.client.widgets.Grid.GridEvent::getCell()())
        }-*/;
    }
}