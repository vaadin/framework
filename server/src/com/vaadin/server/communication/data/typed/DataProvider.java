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
package com.vaadin.server.communication.data.typed;

import java.util.Collection;

import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.data.DataProviderClientRpc;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.ui.AbstractComponent;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * DataProvider for Collection "container".
 * 
 * @since
 */
public class DataProvider<T> extends AbstractExtension {

    /**
     * Simple implementation of collection data provider communication. All data
     * is sent by server automatically and no data is requested by client.
     */
    protected class DataRequestRpcImpl implements DataRequestRpc {

        @Override
        public void requestRows(int firstRowIndex, int numberOfRows,
                int firstCachedRowIndex, int cacheSize) {
            throw new UnsupportedOperationException(
                    "Collection data provider sends all data from server."
                            + " It does not expect client to request anything.");
        }

        @Override
        public void dropRows(JsonArray rowKeys) {
            // FIXME: What should I do with these?
        }

    }

    private Collection<T> data;

    /**
     * Creates a new DataProvider, connecting it to given Collection and
     * Component
     * 
     * @param data
     *            collection of data to use
     * @param component
     *            component to extend
     */
    public DataProvider(Collection<T> data, AbstractComponent component) {
        this.data = data;
        extend(component);

        registerRpc(createRpc());
    }

    /**
     * Initially we need to push all the data to the client.
     * 
     * TODO: The same is true for unknown size changes.
     */
    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        if (initial) {
            getRpcProxy(DataProviderClientRpc.class).resetSize(data.size());
            pushRows(0, data);
        }
    }

    /**
     * Sends given row range to the client.
     * 
     * @param firstIndex
     *            first index
     * @param items
     *            items to send as an iterable
     */
    protected void pushRows(long firstIndex, Iterable<T> items) {
        JsonArray data = Json.createArray();

        int i = 0;
        for (T item : items) {
            data.set(i++, getDataObject(item));
        }

        getRpcProxy(DataProviderClientRpc.class).setData(firstIndex, data);
    }

    /**
     * Creates the JsonObject for given item. This method calls all data
     * generators for this item.
     * 
     * @param item
     *            item to be made into a json object
     * @return json object representing the item
     */
    protected JsonObject getDataObject(T item) {
        JsonObject dataObject = Json.createObject();

        dataObject.put("k", item.toString());

        // TODO: Add data generator stuff..

        return dataObject;
    }

    /**
     * Creates an instance of DataRequestRpc. By default it is
     * {@link DataRequestRpcImpl}.
     * 
     * @return data request rpc implementation
     */
    protected DataRequestRpc createRpc() {
        return new DataRequestRpcImpl();
    }

}
