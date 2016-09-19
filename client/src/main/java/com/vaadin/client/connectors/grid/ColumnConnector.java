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

import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.ColumnState;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * A connector class for columns of the Grid component.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(com.vaadin.ui.Grid.Column.class)
public class ColumnConnector extends AbstractExtensionConnector {

    private Column<Object, JsonObject> column;

    /* This parent is needed because it's no longer available in onUnregister */
    private GridConnector parent;

    @Override
    protected void extend(ServerConnector target) {
        parent = getParent();
        String columnId = getState().id;
        column = new Column<Object, JsonObject>() {

            @Override
            public Object getValue(JsonObject row) {
                final JsonObject rowData = row
                        .getObject(DataCommunicatorConstants.DATA);

                if (rowData.hasKey(columnId)) {
                    final JsonValue columnValue = rowData.get(columnId);

                    return getRendererConnector().decode(columnValue);
                }

                return null;
            }
        };
        column.setRenderer(getRendererConnector().getRenderer());
        getParent().addColumn(column, columnId);
    }

    @SuppressWarnings("unchecked")
    private AbstractRendererConnector<Object> getRendererConnector() {
        return (AbstractRendererConnector<Object>) getState().renderer;
    }

    @OnStateChange("caption")
    void updateCaption() {
        column.setHeaderCaption(getState().caption);
    }

    @OnStateChange("sortable")
    void updateSortable() {
        column.setSortable(getState().sortable);
    }

    @OnStateChange("hidingToggleCaption")
    void updateHidingToggleCaption() {
        column.setHidingToggleCaption(getState().hidingToggleCaption);
    }

    @OnStateChange("hidden")
    void updateHidden() {
        column.setHidden(getState().hidden);
    }

    @OnStateChange("hidable")
    void updateHidable() {
        column.setHidable(getState().hidable);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        parent.removeColumn(column);
        column = null;
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
