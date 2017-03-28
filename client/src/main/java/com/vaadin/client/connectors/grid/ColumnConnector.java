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

    static abstract class CustomColumn extends Column<Object, JsonObject> {

        private final String connectorId;

        CustomColumn(String connectorId) {
            this.connectorId = connectorId;
        }

        public String getConnectorId() {
            return connectorId;
        }
    }

    private CustomColumn column;

    /* This parent is needed because it's no longer available in onUnregister */
    private GridConnector parent;

    @Override
    protected void extend(ServerConnector target) {
        parent = getParent();
        column = new CustomColumn(getConnectorId()) {

            @Override
            public Object getValue(JsonObject row) {
                final JsonObject rowData = row
                        .getObject(DataCommunicatorConstants.DATA);

                if (rowData.hasKey(getConnectorId())) {
                    final JsonValue columnValue = rowData.get(getConnectorId());

                    return getRendererConnector().decode(columnValue);
                }

                return null;
            }
        };

        // Initially set a renderer
        updateRenderer();

        getParent().addColumn(column, getState().internalId);

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

    @OnStateChange("renderer")
    void updateRenderer() {
        column.setRenderer(getRendererConnector().getRenderer());
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

    @OnStateChange("resizable")
    void updateResizable() {
        column.setResizable(getState().resizable);
    }

    @OnStateChange("width")
    void updateWidth() {
        column.setWidth(getState().width);
    }

    @OnStateChange("minWidth")
    void updateMinWidth() {
        column.setMinimumWidth(getState().minWidth);
    }

    @OnStateChange("maxWidth")
    void updateMaxWidth() {
        column.setMaximumWidth(getState().maxWidth);
    }

    @OnStateChange("expandRatio")
    void updateExpandRatio() {
        column.setExpandRatio(getState().expandRatio);
    }

    @OnStateChange("editable")
    void updateEditable() {
        column.setEditable(getState().editable);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        if (parent.getParent() != null) {
            // If the grid itself was unregistered there is no point in spending
            // time to remove columns (and have problems with frozen columns)
            // before throwing everything away
            parent.removeColumn(column);
            parent = null;
        }
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
