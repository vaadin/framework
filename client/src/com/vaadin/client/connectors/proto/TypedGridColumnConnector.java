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

import com.vaadin.client.ServerConnector;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.proto.TypedGridColumnState;

import elemental.json.JsonObject;

/**
 * Connector implementation for TypedGrid Columns.
 * 
 * @since
 */
@Connect(com.vaadin.ui.proto.Column.class)
public class TypedGridColumnConnector extends
        AbstractTypedGridExtensionConnector {

    /* This is stored so it can be used in onUnregister */
    private TypedGridConnector parent;

    @Override
    protected void extend(ServerConnector target) {
        parent = getParent();
        parent.addColumn(getState().id, new Column<String, JsonObject>() {
            @Override
            public String getValue(JsonObject row) {
                return row.getObject(GridState.JSONKEY_DATA).getString(
                        getState().id);
            }
        });
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        parent.removeColumn(getState().id);
    }

    @Override
    public TypedGridColumnState getState() {
        return (TypedGridColumnState) super.getState();
    }

}
