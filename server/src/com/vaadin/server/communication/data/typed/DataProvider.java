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
import java.util.LinkedHashSet;

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
     * Creates the appropriate type of DataProvider based on the type of
     * Collection provided to the method.
     * <p>
     * <strong>Note:</strong> this method will also extend the given component
     * with the newly created DataProvider. The user should <strong>not</strong>
     * call the {@link #extend(com.vaadin.server.AbstractClientConnector)}
     * method explicitly.
     * 
     * @param data
     *            collection of data objects
     * @param component
     *            component to extend with the data provider
     * @return created data provider
     */
    public static <V> DataProvider<V> create(Collection<V> data,
            AbstractComponent component) {
        DataProvider<V> dataProvider = new DataProvider<V>(data);
        dataProvider.extend(component);
        return dataProvider;
    }

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
    private Collection<TypedDataGenerator<T>> generators = new LinkedHashSet<TypedDataGenerator<T>>();

    /**
     * Creates a new DataProvider with the given Collection.
     * 
     * @param data
     *            collection of data to use
     */
    protected DataProvider(Collection<T> data) {
        this.data = data;
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
     * Adds a TypedDataGenerator to this DataProvider.
     * 
     * @param generator
     *            typed data generator
     */
    public void addDataGenerator(TypedDataGenerator<T> generator) {
        generators.add(generator);
    }

    /**
     * Removes a TypedDataGenerator from this DataProvider.
     * 
     * @param generator
     *            typed data generator
     */
    public void removeDataGenerator(TypedDataGenerator<T> generator) {
        generators.add(generator);
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

        for (TypedDataGenerator<T> generator : generators) {
            generator.generateData(item, dataObject);
        }

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
