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
package com.vaadin.client.connectors.selection;

import java.util.Optional;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

/**
 * A connector for single selection extensions.
 *
 * @author Vaadin Ltd.
 */
@Connect(com.vaadin.data.selection.SingleSelection.class)
public class SingleSelectionConnector extends AbstractSelectionConnector {

    private static class SingleSelection
            implements SelectionModel.Single<JsonObject> {

        private SelectionServerRpc rpc;

        SingleSelection(SelectionServerRpc rpc) {
            this.rpc = rpc;
        }

        @Override
        public void select(JsonObject item) {
            if (!isSelected(item)) {
                rpc.select(getKey(item));
            }
        }

        @Override
        public void deselect(JsonObject item) {
            if (isSelected(item)) {
                rpc.deselect(getKey(item));
            }
        }

        @Override
        public boolean isSelected(JsonObject item) {
            return isItemSelected(item);
        }

        @Override
        public Optional<JsonObject> getSelectedItem() {
            throw new UnsupportedOperationException(
                    "A client-side selection model does not know the full selection");
        }
    }

    private AbstractListingConnector parent;

    @Override
    public void onUnregister() {
        super.onUnregister();
        if (parent.getSelectionModel() == getSelectionModel()) {
            parent.setSelectionModel(null);
        }
    }

    @Override
    protected void extend(ServerConnector target) {
        super.extend(target);
        parent = getParent();
    }

    @Override
    protected SelectionModel<JsonObject> createSelectionModel() {
        return new SingleSelection(getRpcProxy(SelectionServerRpc.class));
    }
}
