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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.client.widget.grid.sort.SortEvent;
import com.vaadin.client.widget.grid.sort.SortOrder;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridServerRpc;

import elemental.json.JsonObject;

/**
 * A connector class for the typed Grid component.
 *
 * @author Vaadin Ltd
 * @since
 */
@Connect(com.vaadin.ui.Grid.class)
public class GridConnector extends AbstractListingConnector
        implements HasComponentsConnector, SimpleManagedLayout, DeferredWorker {

    /* Map to keep track of all added columns */
    private Map<Column<?, JsonObject>, String> columnToIdMap = new HashMap<>();
    /* Child component list for HasComponentsConnector */
    private List<ComponentConnector> childComponents;

    @Override
    public Grid<JsonObject> getWidget() {
        return (Grid<JsonObject>) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();

        new ClickSelectHandler<JsonObject>(getWidget());
        getWidget().addSortHandler(this::handleSortEvent);

        layout();
    }

    @Override
    public void setDataSource(DataSource<JsonObject> dataSource) {
        getWidget().setDataSource(dataSource);
    }

    /**
     * Adds a column to the Grid widget. For each column a communication id
     * stored for client to server communication.
     *
     * @param column
     *            column to add
     * @param id
     *            communication id
     */
    public void addColumn(Column<?, JsonObject> column, String id) {
        assert !columnToIdMap.containsKey(column) && !columnToIdMap
                .containsValue(id) : "Column with given id already exists.";
        getWidget().addColumn(column);
        columnToIdMap.put(column, id);
    }

    /**
     * Removes a column from Grid widget. This method also removes communication
     * id mapping for the column.
     *
     * @param column
     *            column to remove
     */
    public void removeColumn(Column<?, JsonObject> column) {
        assert columnToIdMap
                .containsKey(column) : "Given Column does not exist.";
        getWidget().removeColumn(column);
        columnToIdMap.remove(column);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        columnToIdMap.clear();
    }

    @Override
    public boolean isWorkPending() {
        return getWidget().isWorkPending();
    }

    @Override
    public void layout() {
        getWidget().onResize();
    }

    /**
     * Sends sort information from an event to the server-side of the Grid.
     *
     * @param event
     *            the sort event
     */
    private void handleSortEvent(SortEvent<JsonObject> event) {
        List<String> columnIds = new ArrayList<>();
        List<SortDirection> sortDirections = new ArrayList<>();
        for (SortOrder so : event.getOrder()) {
            if (columnToIdMap.containsKey(so.getColumn())) {
                columnIds.add(columnToIdMap.get(so.getColumn()));
                sortDirections.add(so.getDirection());
            }
        }
        getRpcProxy(GridServerRpc.class).sort(columnIds.toArray(new String[0]),
                sortDirections.toArray(new SortDirection[0]),
                event.isUserOriginated());
    }

    /* HasComponentsConnector */

    @Override
    public void updateCaption(ComponentConnector connector) {
        // Details components don't support captions.
    }

    @Override
    public List<ComponentConnector> getChildComponents() {
        if (childComponents == null) {
            return Collections.emptyList();
        }

        return childComponents;
    }

    @Override
    public void setChildComponents(List<ComponentConnector> children) {
        this.childComponents = children;

    }

    @Override
    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler) {
        return ensureHandlerManager()
                .addHandler(ConnectorHierarchyChangeEvent.TYPE, handler);
    }
}
