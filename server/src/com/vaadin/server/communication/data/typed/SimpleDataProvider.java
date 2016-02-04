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

import java.util.HashSet;
import java.util.Set;

import com.vaadin.server.communication.data.typed.DataSource.DataChangeHandler;
import com.vaadin.shared.data.DataProviderClientRpc;
import com.vaadin.shared.data.DataRequestRpc;

import elemental.json.Json;
import elemental.json.JsonArray;

/**
 * DataProvider for Collections. This class takes care of sending data objects
 * stored in a Collection from the server-side to the client-side.
 * <p>
 * This is an implementation that does not provide any kind of lazy loading. All
 * data is sent to the client-side on the initial client response.
 * 
 * @since
 */
public class SimpleDataProvider<T> extends DataProvider<T> {

    /**
     * Simple implementation of collection data provider communication. All data
     * is sent by server automatically and no data is requested by client.
     */
    protected class SimpleDataRequestRpc implements DataRequestRpc {

        @Override
        public void requestRows(int firstRowIndex, int numberOfRows,
                int firstCachedRowIndex, int cacheSize) {
            throw new UnsupportedOperationException(
                    "Collection data provider sends all data from server."
                            + " It does not expect client to request anything.");
        }

        @Override
        public void dropRows(JsonArray keys) {
            for (int i = 0; i < keys.length(); ++i) {
                handler.dropActiveData(keys.getString(i));
            }

            // Use the whole data as the ones sent to the client.
            handler.cleanUp(data);
        }
    }

    private boolean reset = false;
    private final Set<T> updatedData = new HashSet<T>();

    private DataSource<T> data;
    // TODO: Allow customizing the used key mapper
    private DataKeyMapper<T> keyMapper = new KeyMapper<T>();

    /**
     * Creates a new DataProvider with the given Collection.
     * 
     * @param data
     *            collection of data to use
     */
    protected SimpleDataProvider(DataSource<T> data) {
        this.data = data;
        this.data.addDataChangeHandler(new DataChangeHandler<T>() {

            @Override
            public void onDataChange() {
                reset();
            }

            @Override
            public void onDataAdd(T data) {
                add(data);
            }

            @Override
            public void onDataRemove(T data) {
                remove(data);
            }

            @Override
            public void onDataUpdate(T data) {
                refresh(data);
            }
        });
    }

    /**
     * Initially and in the case of a reset all data should be pushed to the
     * client.
     */
    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        if (initial || reset) {
            getRpcProxy(DataProviderClientRpc.class).resetSize(data.size());
            pushData(0, data);
        } else if (!updatedData.isEmpty()) {
            JsonArray dataArray = Json.createArray();
            int i = 0;
            for (T data : updatedData) {
                dataArray.set(i++, getDataObject(data));
            }
            rpc.updateData(dataArray);
        }

        reset = false;
        updatedData.clear();
    }

    @Override
    protected DataKeyMapper<T> getKeyMapper() {
        return keyMapper;
    }

    /**
     * Informs the DataProvider that a data object has been added. It is assumed
     * to be the last object in the collection.
     * 
     * @param data
     *            data object added to collection
     */
    protected void add(T data) {
        rpc.add(getDataObject(data));
    }

    /**
     * Informs the DataProvider that a data object has been removed.
     * 
     * @param data
     *            data object removed from collection
     */
    protected void remove(T data) {
        if (handler.getActiveData().contains(data)) {
            rpc.drop(getKeyMapper().key(data));
        }
    }

    /**
     * Informs the DataProvider that the collection has changed.
     */
    protected void reset() {
        if (reset) {
            return;
        }

        reset = true;
        markAsDirty();
    }

    /**
     * Informs the DataProvider that a data object has been updated.
     * 
     * @param data
     *            updated data object
     */
    protected void refresh(T data) {
        if (updatedData.isEmpty()) {
            markAsDirty();
        }

        updatedData.add(data);
    }

    @Override
    protected DataRequestRpc createRpc() {
        return new SimpleDataRequestRpc();
    }

}
