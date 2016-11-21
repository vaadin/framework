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
import com.vaadin.client.widget.grid.selection.ClickSelectHandler;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * Client side connector for grid single selection model.
 *
 * @author Vaadin Ltd.
 *
 * @since 8.0
 */
@Connect(com.vaadin.ui.components.grid.SingleSelectionModelImpl.class)
public class SingleSelectionModelConnector extends AbstractExtensionConnector {

    private ClickSelectHandler clickSelectHandler;

    @Override
    protected void extend(ServerConnector target) {
        getParent().getWidget()
                .setSelectionModel(new SelectionModel<JsonObject>() {

                    @Override
                    public void select(JsonObject item) {
                        getRpcProxy(SelectionServerRpc.class).select(
                                item.getString(DataCommunicatorConstants.KEY));
                    }

                    @Override
                    public void deselect(JsonObject item) {
                        getRpcProxy(SelectionServerRpc.class).deselect(
                                item.getString(DataCommunicatorConstants.KEY));
                    }

                    @Override
                    public boolean isSelected(JsonObject item) {
                        return SelectionModel.isItemSelected(item);
                    }

                    @Override
                    public void deselectAll() {
                        getRpcProxy(SelectionServerRpc.class).select(null);
                    }

                });
        clickSelectHandler = new ClickSelectHandler<>(getParent().getWidget());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        if (clickSelectHandler != null) {
            clickSelectHandler.removeHandler();
        }
    }

    @Override
    public GridConnector getParent() {
        return (GridConnector) super.getParent();
    }

}
