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
package com.vaadin.client.tokka.connectors.selection;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.tokka.connectors.components.AbstractListingConnector;
import com.vaadin.client.tokka.data.selection.SelectionModel;
import com.vaadin.client.tokka.data.selection.SelectionModel.Single;
import com.vaadin.shared.tokka.data.selection.SelectionServerRpc;
import com.vaadin.shared.ui.Connect;

import elemental.json.JsonObject;

@Connect(com.vaadin.tokka.server.communication.data.SingleSelection.class)
public class SingleSelectionConnector extends AbstractSelectionConnector {

    public static class SingleSelection implements SelectionModel, Single {

        private JsonObject value;
        private SelectionServerRpc rpc;

        public SingleSelection(SelectionServerRpc rpc) {
            this.rpc = rpc;
        }

        @Override
        public void select(JsonObject item) {
            if (item != null && !jsonEquals(value, item)) {
                rpc.select(getKey(item));
                value = item;
            }
        }

        @Override
        public void deselect(JsonObject item) {
            if (item != null && jsonEquals(value, item)) {
                rpc.deselect(getKey(item));
                value = null;
            }
        }

        @Override
        public boolean isSelected(JsonObject item) {
            if (hasSelectedKey(item)) {
                value = item;
                return true;
            }
            return false;
        }
    }

    private AbstractListingConnector parent;

    @Override
    protected void extend(ServerConnector target) {
        super.extend(target);

        parent = getParent();
    }

    @Override
    protected SelectionModel createSelectionModel() {
        return new SingleSelection(getRpcProxy(SelectionServerRpc.class));
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        if (parent.getSelectionModel() == getSelectionModel()) {
            // Remove from parent.
            parent.setSelectionModel(null);
        }
    }

}
