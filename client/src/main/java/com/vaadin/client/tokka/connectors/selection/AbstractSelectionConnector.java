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
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.tokka.connectors.components.AbstractListingConnector;
import com.vaadin.client.tokka.connectors.data.HasSelection;
import com.vaadin.client.tokka.data.selection.SelectionModel;
import com.vaadin.shared.tokka.data.DataProviderConstants;

import elemental.json.JsonObject;

public abstract class AbstractSelectionConnector extends
        AbstractExtensionConnector {

    private SelectionModel model = null;

    @Override
    protected void extend(ServerConnector target) {
        if (!(target instanceof HasSelection)) {
            throw new UnsupportedOperationException(
                    "SelectionModel extending a Connector without HasDataSource");
        }
        // TODO: Provide SelectionModel API
        // TODO: Should this use "Registration" approach for easy and safe
        // removal?
        model = createSelectionModel();
        ((HasSelection) target).setSelectionModel(getSelectionModel());
    }

    /**
     * Creates a selection model object to be used by the Connector.
     * 
     * @return created selection model
     */
    protected abstract SelectionModel createSelectionModel();

    protected static String getKey(JsonObject item) {
        return item.getString(DataProviderConstants.KEY);
    }

    protected static boolean jsonEquals(JsonObject a, JsonObject b) {
        final String key = DataProviderConstants.KEY;
        if (a != null && b != null) {
            if (a.hasKey(key) && b.hasKey(key)) {
                return a.getString(key).equals(b.getString(key));
            }
        }
        return a == b;
    }

    protected static boolean hasSelectedKey(JsonObject item) {
        String key = DataProviderConstants.SELECTED;
        return item.hasKey(key) && item.getBoolean(key);
    }

    @Override
    public AbstractListingConnector getParent() {
        return (AbstractListingConnector) super.getParent();
    }

    protected SelectionModel getSelectionModel() {
        return model;
    }
}
