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
package com.vaadin.client.tokka.connectors.components.grid;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.tokka.data.DataProviderConstants;
import com.vaadin.shared.tokka.ui.components.grid.ColumnState;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

@Connect(com.vaadin.tokka.ui.components.grid.Column.class)
public class ColumnConnector extends AbstractExtensionConnector {

    private Column<JsonValue, JsonObject> column;
    private GridConnector parent;

    @Override
    protected void extend(ServerConnector target) {
        parent = getParent();
        column = new Column<JsonValue, JsonObject>() {

            @Override
            public JsonValue getValue(JsonObject row) {
                return row.getObject(DataProviderConstants.DATA).get(
                        getState().communicationId);
            }
        };
        getParent().addColumn(column, getState().communicationId);
    }

    @OnStateChange("caption")
    void updateCaption() {
        column.setHeaderCaption(getState().caption);
    }

    @OnStateChange("sortable")
    void updateSortable() {
        column.setSortable(getState().sortable);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        parent.removeColumn(column);
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    @Override
    public ColumnState getState() {
        return (ColumnState) super.getState();
    }

}
