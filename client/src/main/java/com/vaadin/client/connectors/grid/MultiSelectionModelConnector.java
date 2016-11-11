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
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.selection.MultiSelectionRenderer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SelectionModelWithSelectionColumn;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.GridMultiSelectServerRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * Connector for server side multiselection model implementation.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 */
@Connect(com.vaadin.ui.components.grid.MultiSelectionModelImpl.class)
public class MultiSelectionModelConnector extends AbstractExtensionConnector {

    /**
     * Client side multiselection model implementation.
     */
    protected class MultiSelectionModel implements SelectionModel<JsonObject>,
            SelectionModelWithSelectionColumn {

        @Override
        public Renderer<Boolean> getRenderer() {
            // this method is only called once when the selection model is set
            // to grid
            return new MultiSelectionRenderer<>(getGrid());
        }

        @Override
        public void select(JsonObject item) {
            getRpcProxy(GridMultiSelectServerRpc.class)
                    .select(item.getString(DataCommunicatorConstants.KEY));
        }

        @Override
        public void deselect(JsonObject item) {
            // handled by diffstate
            getRpcProxy(GridMultiSelectServerRpc.class)
                    .deselect(item.getString(DataCommunicatorConstants.KEY));
        }

        @Override
        public void deselectAll() {
            // TODO Will be added in a separate patch
            throw new UnsupportedOperationException(
                    "Deselect all not supported.");
        }

        @Override
        public boolean isSelected(JsonObject item) {
            return SelectionModel.isItemSelected(item);
        }

    }

    @Override
    protected void extend(ServerConnector target) {
        getGrid().setSelectionModel(new MultiSelectionModel());
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

    /**
     * Shorthand for fetching the grid this selection model is bound to.
     *
     * @return the grid
     */
    protected Grid<JsonObject> getGrid() {
        return getParent().getWidget();
    }

}
