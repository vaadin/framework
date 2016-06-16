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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.event.handler.Registration;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.data.typed.DataProviderClientRpc;
import com.vaadin.shared.data.typed.DataProviderConstants;
import com.vaadin.ui.AbstractComponent;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * DataProvider base class. This class is the base for all DataProvider
 * communication implementations. It uses {@link TypedDataGenerator}s to write
 * {@link JsonObject}s representing each data object to be sent to the
 * client-side.
 * 
 * @since
 */
public abstract class DataProvider<T> extends AbstractExtension {

    /**
     * Creates the appropriate type of DataProvider based on the type of
     * Collection provided to the method.
     * <p>
     * TODO: Actually use different DataProviders and provide an API for the
     * back end to inform changes back.
     * 
     * @param data
     *            collection of data objects
     * @param component
     *            component to extend with the data provider
     * @return created data provider
     */
    public static <V> SimpleDataProvider<V> create(DataSource<V> data) {
        SimpleDataProvider<V> dataProvider = new SimpleDataProvider<V>(data);
        return dataProvider;
    }

    /**
     * A class for handling currently active data and dropping data that is no
     * longer needed. Data tracking is based on key string provided by
     * {@link DataKeyMapper}.
     * <p>
     * When the {@link DataProvider} is pushing new data to the client-side via
     * {@link DataProvider#pushData(long, Collection)},
     * {@link #addActiveData(Collection)} and {@link #cleanUp(Collection)} are
     * called with the same parameter. In the clean up method any dropped data
     * objects that are not in the given collection will be cleaned up and
     * {@link TypedDataGenerator#destroyData(Object)} will be called for them.
     */
    protected class ActiveDataHandler implements Serializable,
            TypedDataGenerator<T> {

        /**
         * Set of key strings for currently active data objects
         */
        private final Set<String> activeData = new HashSet<String>();

        /**
         * Set of key strings for data objects dropped on the client. This set
         * is used to clean up old data when it's no longer needed.
         */
        private final Set<String> droppedData = new HashSet<String>();

        /**
         * Adds given objects as currently active objects.
         * 
         * @param dataObjects
         *            collection of new active data objects
         */
        public void addActiveData(Iterable<T> dataObjects) {
            for (T data : dataObjects) {
                if (!activeData.contains(getKeyMapper().key(data))) {
                    activeData.add(getKeyMapper().key(data));
                }
            }
        }

        /**
         * Executes the data destruction for dropped data that is not sent to
         * the client. This method takes most recently sent data objects in a
         * collection. Doing the clean up like this prevents the
         * {@link ActiveDataHandler} from creating new keys for rows that were
         * dropped but got re-requested by the client-side. In the case of
         * having all data at the client, the collection should be all the data
         * in the back end.
         * 
         * @see DataProvider#pushData(long, Collection)
         * @param dataObjects
         *            collection of most recently sent data to the client
         */
        public void cleanUp(Iterable<T> dataObjects) {
            Collection<String> keys = new HashSet<String>();
            for (T data : dataObjects) {
                keys.add(getKeyMapper().key(data));
            }

            // Remove still active rows that were dropped by the client
            droppedData.removeAll(keys);
            // Do data clean up for object no longer needed.
            dropData(droppedData);
            droppedData.clear();
        }

        /**
         * Marks a data object identified by given key string to be dropped.
         * 
         * @param key
         *            key string
         */
        public void dropActiveData(String key) {
            if (activeData.contains(key)) {
                droppedData.add(key);
            }
        }

        /**
         * Returns the collection of all currently active data.
         * 
         * @return collection of active data objects
         */
        public Collection<T> getActiveData() {
            HashSet<T> hashSet = new HashSet<T>();
            for (String key : activeData) {
                hashSet.add(getKeyMapper().get(key));
            }
            return hashSet;
        }

        @Override
        public void generateData(T data, JsonObject jsonObject) {
            // Write the key string for given data object
            jsonObject.put(DataProviderConstants.KEY, getKeyMapper().key(data));
        }

        @Override
        public void destroyData(T data) {
            // Remove from active data set
            activeData.remove(getKeyMapper().key(data));
            // Drop the registered key
            getKeyMapper().remove(data);
        }
    }

    private Collection<TypedDataGenerator<T>> generators = new LinkedHashSet<TypedDataGenerator<T>>();
    protected ActiveDataHandler handler = new ActiveDataHandler();
    protected DataProviderClientRpc rpc;

    protected DataSource<T> dataSource;
    private Registration dataChangeHandler;
    private DetachListener detachListener;
    private DataKeyMapper<T> keyMapper;

    protected DataProvider(DataSource<T> dataSource) {
        addDataGenerator(handler);
        this.dataSource = dataSource;
        rpc = getRpcProxy(DataProviderClientRpc.class);
        registerRpc(createRpc());
        dataChangeHandler = this.dataSource
                .addDataChangeHandler(createDataChangeHandler());
        keyMapper = createKeyMapper();
    }

    @Override
    public void attach() {
        super.attach();

        if (detachListener == null) {
            detachListener = new DetachListener() {

                @Override
                public void detach(DetachEvent event) {
                    cleanUp();
                }
            };
            getUI().addDetachListener(detachListener);
        }
    }

    @Override
    public void setParent(ClientConnector parent) {
        if (getParent() != null && parent == null) {
            // Removing from parent, clean up.
            cleanUp();
        }

        super.setParent(parent);
    }

    /**
     * Adds a {@link TypedDataGenerator} to this {@link DataProvider}.
     * 
     * @param generator
     *            typed data generator
     */
    public void addDataGenerator(TypedDataGenerator<T> generator) {
        generators.add(generator);
    }

    /**
     * Removes a {@link TypedDataGenerator} from this {@link DataProvider}.
     * 
     * @param generator
     *            typed data generator
     */
    public void removeDataGenerator(TypedDataGenerator<T> generator) {
        generators.remove(generator);
    }

    /**
     * Gets the {@link DataKeyMapper} used by this {@link DataProvider}. Key
     * mapper can be used to map keys sent to the client-side back to their
     * respective data objects.
     * 
     * @return key mapper
     */
    public DataKeyMapper<T> getKeyMapper() {
        return keyMapper;
    }

    public abstract void refresh(T data);

    /**
     * Sends given collection of data objects to the client-side.
     * 
     * @param firstIndex
     *            first index of pushed data
     * @param data
     *            data objects to send as an iterable
     */
    protected void pushData(long firstIndex, Iterable<T> data) {
        JsonArray dataArray = Json.createArray();

        int i = 0;
        for (T item : data) {
            dataArray.set(i++, getDataObject(item));
        }

        rpc.setData(firstIndex, dataArray);
        handler.addActiveData(data);
        handler.cleanUp(data);
    }

    /**
     * Creates the JsonObject for given data object. This method calls all data
     * generators for it.
     * 
     * @param data
     *            data object to be made into a json object
     * @return json object representing the data object
     */
    protected JsonObject getDataObject(T data) {
        JsonObject dataObject = Json.createObject();

        for (TypedDataGenerator<T> generator : generators) {
            generator.generateData(data, dataObject);
        }

        return dataObject;
    }

    /**
     * Drops data objects identified by given keys from memory. This will invoke
     * {@link TypedDataGenerator#destroyData} for each of those objects.
     * 
     * @param droppedKeys
     *            collection of dropped keys
     */
    private void dropData(Collection<String> droppedKeys) {
        for (String key : droppedKeys) {
            assert key != null : "Bookkeepping failure. Dropping a null key";

            T data = getKeyMapper().get(key);
            assert data != null : "Bookkeepping failure. No data object to match key";

            for (TypedDataGenerator<T> g : generators) {
                g.destroyData(data);
            }
        }
    }

    /**
     * Clean up method for removing all listeners attached by the
     * {@link DataProvider}. This method is called from {@link #remove()} or
     * when the UI gets detached.
     */
    protected void cleanUp() {
        if (dataSource != null) {
            dataChangeHandler.removeHandler();
            dataChangeHandler = null;
        }
        if (detachListener != null) {
            getUI().removeDetachListener(detachListener);
            detachListener = null;
        }
    }

    /**
     * Creates a {@link DataKeyMapper} to use with this {@link DataProvider}.
     * <p>
     * This method is called from the constructor.
     * 
     * @return key mapper
     */
    protected abstract DataKeyMapper<T> createKeyMapper();

    /**
     * Creates a {@link DataRequestRpc} used with this {@link DataProvider}.
     * <p>
     * This method is called from the constructor.
     * 
     * @return data request rpc implementation
     */
    protected abstract DataRequestRpc createRpc();

    /**
     * Creates a {@link DataChangeHandler} to use with the {@link DataSource}.
     * <p>
     * This method is called from the constructor.
     * 
     * @return data change handler
     */
    protected abstract DataChangeHandler<T> createDataChangeHandler();
}