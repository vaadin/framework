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

import com.vaadin.client.ServerConnector;
import com.vaadin.client.connectors.AbstractListingConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.data.DataCommunicatorConstants;

import elemental.json.JsonObject;

/**
 * The client-side connector for selection extensions.
 *
 * @author Vaadin Ltd.
 * 
 * @since 8.0
 */
public abstract class AbstractSelectionConnector
        extends AbstractExtensionConnector {

    @Override
    @SuppressWarnings("unchecked")
    protected void extend(ServerConnector target) {
        if (!(target instanceof AbstractListingConnector)) {
            throw new IllegalArgumentException(
                    "Cannot extend a connector that is not an "
                            + AbstractListingConnector.class.getSimpleName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractListingConnector getParent() {
        return (AbstractListingConnector) super.getParent();
    }

    /**
     * Gets the selected state from a given json object. This is a helper method
     * for selection model connectors.
     *
     * @param item
     *            a json object
     * @return {@code true} if the json object is marked as selected;
     *         {@code false} if not
     */
    public static boolean isItemSelected(JsonObject item) {
        return item.hasKey(DataCommunicatorConstants.SELECTED)
                && item.getBoolean(DataCommunicatorConstants.SELECTED);
    }

    /**
     * Gets the item key from given json object. This is a helper method for
     * selection model connectors.
     *
     * @param item
     *            a json object
     * @return item key; {@code null} if there is no key
     */
    public static String getKey(JsonObject item) {
        if (item.hasKey(DataCommunicatorConstants.KEY)) {
            return item.getString(DataCommunicatorConstants.KEY);
        } else {
            return null;
        }
    }

}
