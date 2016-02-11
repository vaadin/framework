/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.connectors.proto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.client.connectors.GridConnector;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.HasDataSource;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.widget.grid.selection.SelectionEvent;
import com.vaadin.client.widget.grid.selection.SelectionHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.data.DataProviderConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.proto.TypedGridClientRpc;
import com.vaadin.shared.ui.proto.TypedGridServerRpc;
import com.vaadin.shared.ui.proto.TypedGridState;
import com.vaadin.shared.util.SharedUtil;

import elemental.json.JsonObject;

/**
 * Connector implementation for TypedGrid. Uses {@link Grid} as its widget, just
 * like {@link GridConnector}.
 * 
 * @since
 */
@Connect(com.vaadin.ui.proto.TypedGrid.class)
public class TypedGridConnector extends AbstractComponentConnector implements
        SimpleManagedLayout, HasDataSource {

    private Map<String, Column<?, JsonObject>> columnMap = new HashMap<String, Column<?, JsonObject>>();

    @Override
    @SuppressWarnings("unchecked")
    public Grid<JsonObject> getWidget() {
        return (Grid<JsonObject>) super.getWidget();
    }

    @Override
    public TypedGridState getState() {
        return (TypedGridState) super.getState();
    }

    @Override
    protected void init() {
        super.init();

        registerRpc(TypedGridClientRpc.class, new TypedGridClientRpc() {

            @Override
            public void setColumnOrder(String[] columnOrder) {
                if (columnOrder.length == 0) {
                    return;
                }

                List<Column<?, JsonObject>> columns = new ArrayList<Column<?, JsonObject>>(
                        columnOrder.length);
                for (String key : columnOrder) {
                    if (columnMap.containsKey(key)) {
                        columns.add(columnMap.get(key));
                    }
                }
                Column<?, JsonObject>[] c = new Column[] {};
                getWidget().setColumnOrder(columns.toArray(c));
            }
        });

        getWidget().addSelectionHandler(new SelectionHandler<JsonObject>() {

            @Override
            public void onSelect(SelectionEvent<JsonObject> event) {
                String key = null;
                if (!event.getSelected().isEmpty()) {
                    key = event.getSelected().iterator().next()
                            .getString(DataProviderConstants.KEY);
                }
                getRpcProxy(TypedGridServerRpc.class).setSelected(key);
            }
        });
    }

    public void addColumn(String key, Column<?, JsonObject> column) {
        // TODO: Remove this when header support is implemented
        getWidget().addColumn(column).setHeaderCaption(
                SharedUtil.propertyIdToHumanFriendly(key));

        columnMap.put(key, column);
    }

    public void removeColumn(String key) {
        if (columnMap.containsKey(key)) {
            getWidget().removeColumn(columnMap.remove(key));
        }
    }

    @Override
    public void setDataSource(DataSource<JsonObject> ds) {
        getWidget().setDataSource(ds);
    }

    @Override
    public void layout() {
        getWidget().onResize();
    }
}
