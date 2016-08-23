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
package com.vaadin.server.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.grid.Range;

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
public class DataCommunicator<T> extends AbstractExtension {

    /**
     * Simple implementation of collection data provider communication. All data
     * is sent by server automatically and no data is requested by client.
     */
    protected class SimpleDataRequestRpc implements DataRequestRpc {

        @Override
        public void requestRows(int firstRowIndex, int numberOfRows,
                int firstCachedRowIndex, int cacheSize) {
            pushRows = Range.withLength(firstRowIndex, numberOfRows);
            markAsDirty();
        }

        @Override
        public void dropRows(JsonArray keys) {
            for (int i = 0; i < keys.length(); ++i) {
                handler.dropActiveData(keys.getString(i));
            }
        }
    }

    /**
     * A class for handling currently active data and dropping data that is no
     * longer needed. Data tracking is based on key string provided by
     * {@link DataKeyMapper}.
     * <p>
     * When the {@link DataCommunicator} is pushing new data to the client-side
     * via {@link DataCommunicator#pushData(long, Collection)},
     * {@link #addActiveData(Collection)} and {@link #cleanUp(Collection)} are
     * called with the same parameter. In the clean up method any dropped data
     * objects that are not in the given collection will be cleaned up and
     * {@link TypedDataGenerator#destroyData(Object)} will be called for them.
     */
    protected class ActiveDataHandler
            implements Serializable, TypedDataGenerator<T> {

        /**
         * Set of key strings for currently active data objects
         */
        private final Set<String> activeData = new HashSet<>();

        /**
         * Set of key strings for data objects dropped on the client. This set
         * is used to clean up old data when it's no longer needed.
         */
        private final Set<String> droppedData = new HashSet<>();

        /**
         * Adds given objects as currently active objects.
         *
         * @param dataObjects
         *            collection of new active data objects
         */
        public void addActiveData(Stream<T> dataObjects) {
            dataObjects.map(getKeyMapper()::key)
                    .filter(key -> !activeData.contains(key))
                    .forEach(activeData::add);
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
         * @param dataObjects
         *            collection of most recently sent data to the client
         */
        public void cleanUp(Stream<T> dataObjects) {
            Collection<String> keys = dataObjects.map(getKeyMapper()::key)
                    .collect(Collectors.toSet());

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
            HashSet<T> hashSet = new HashSet<>();
            for (String key : activeData) {
                hashSet.add(getKeyMapper().get(key));
            }
            return hashSet;
        }

        @Override
        public void generateData(T data, JsonObject jsonObject) {
            // Write the key string for given data object
            jsonObject.put(DataCommunicatorConstants.KEY,
                    getKeyMapper().key(data));
        }

        @Override
        public void destroyData(T data) {
            // Remove from active data set
            activeData.remove(getKeyMapper().key(data));
            // Drop the registered key
            getKeyMapper().remove(data);
        }
    }

    private Collection<TypedDataGenerator<T>> generators = new LinkedHashSet<>();
    private ActiveDataHandler handler = new ActiveDataHandler();

    private DataSource<T> dataSource;
    private DataKeyMapper<T> keyMapper;

    private boolean reset = false;
    private final Set<T> updatedData = new HashSet<>();
    private Range pushRows = Range.withLength(0, 40);

    private Comparator<T> inMemorySorting;
    private List<SortOrder<String>> backEndSorting = new ArrayList<>();
    private DataCommunicatorClientRpc rpc;

    public DataCommunicator() {
        addDataGenerator(handler);
        rpc = getRpcProxy(DataCommunicatorClientRpc.class);
        registerRpc(createRpc());
        keyMapper = createKeyMapper();
    }

    /**
     * Initially and in the case of a reset all data should be pushed to the
     * client.
     */
    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        if (getDataSource() == null) {
            return;
        }

        // FIXME: Sorting and Filtering with Backend
        Set<Object> filters = Collections.emptySet();

        if (initial || reset) {
            int dataSourceSize = getDataSource().size(new Query(filters));
            rpc.reset(dataSourceSize);
        }

        if (!pushRows.isEmpty()) {
            int offset = pushRows.getStart();
            int limit = pushRows.length();

            Stream<T> rowsToPush;

            if (getDataSource().isInMemory()) {
                // We can safely request all the data when in memory
                // FIXME: filter.
                rowsToPush = getDataSource().apply(new Query());
                if (inMemorySorting != null) {
                    rowsToPush = rowsToPush.sorted(inMemorySorting);
                }
                rowsToPush = rowsToPush.skip(offset).limit(limit);
            } else {
                Query query = new Query(offset, limit, backEndSorting, filters);
                rowsToPush = getDataSource().apply(query);
            }
            pushData(offset, rowsToPush);
        }

        if (!updatedData.isEmpty()) {
            JsonArray dataArray = Json.createArray();
            int i = 0;
            for (T data : updatedData) {
                dataArray.set(i++, getDataObject(data));
            }
            rpc.updateData(dataArray);
        }

        pushRows = Range.withLength(0, 0);
        reset = false;
        updatedData.clear();
    }

    /**
     * Adds a data generator to this data communicator. Data generators can be
     * used to insert custom data to the rows sent to the client. If the data
     * generator is already added, does nothing.
     *
     * @param generator
     *            the data generator to add, not null
     */
    public void addDataGenerator(TypedDataGenerator<T> generator) {
        Objects.requireNonNull(generator, "generator cannot be null");
        generators.add(generator);
    }

    /**
     * Removes a data generator from this data communicator. If there is no such
     * data generator, does nothing.
     *
     * @param generator
     *            the data generator to remove, not null
     */
    public void removeDataGenerator(TypedDataGenerator<T> generator) {
        Objects.requireNonNull(generator, "generator cannot be null");
        generators.remove(generator);
    }

    /**
     * Gets the {@link DataKeyMapper} used by this {@link DataCommunicator}. Key
     * mapper can be used to map keys sent to the client-side back to their
     * respective data objects.
     *
     * @return key mapper
     */
    public DataKeyMapper<T> getKeyMapper() {
        return keyMapper;
    }

    /**
     * Sends given collection of data objects to the client-side.
     *
     * @param firstIndex
     *            first index of pushed data
     * @param data
     *            data objects to send as an iterable
     */
    protected void pushData(int firstIndex, Stream<T> data) {
        JsonArray dataArray = Json.createArray();

        int i = 0;
        List<T> collected = data.collect(Collectors.toList());
        for (T item : collected) {
            dataArray.set(i++, getDataObject(item));
        }

        rpc.setData(firstIndex, dataArray);
        handler.addActiveData(collected.stream());
        handler.cleanUp(collected.stream());
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
    public void refresh(T data) {
        if (updatedData.isEmpty()) {
            markAsDirty();
        }

        updatedData.add(data);
    }

    /**
     * Sets the {@link Comparator} to use with in-memory sorting.
     *
     * @param comparator
     *            comparator used to sort data
     */
    public void setInMemorySorting(Comparator<T> comparator) {
        inMemorySorting = comparator;
        reset();
    }

    /**
     * Sets the {@link SortOrder}s to use with backend sorting.
     *
     * @param sortOrder
     *            list of sort order information to pass to a query
     */
    public void setBackEndSorting(List<SortOrder<String>> sortOrder) {
        backEndSorting.clear();
        backEndSorting.addAll(sortOrder);
        reset();
    }

    /**
     * Creates a {@link DataKeyMapper} to use with this DataCommunicator.
     * <p>
     * This method is called from the constructor.
     *
     * @return key mapper
     */
    protected DataKeyMapper<T> createKeyMapper() {
        return new KeyMapper<>();
    }

    /**
     * Creates a {@link DataRequestRpc} used with this {@link DataCommunicator}.
     * <p>
     * This method is called from the constructor.
     *
     * @return data request rpc implementation
     */
    protected DataRequestRpc createRpc() {
        return new SimpleDataRequestRpc();
    }

    /**
     * Gets the current data source from this DataCommunicator.
     *
     * @return the data source
     */
    public DataSource<T> getDataSource() {
        return dataSource;
    }

    /**
     * Sets the current data source for this DataCommunicator.
     *
     * @param dataSource
     *            the data source to set, not null
     */
    public void setDataSource(DataSource<T> dataSource) {
        Objects.requireNonNull(dataSource, "data source cannot be null");
        this.dataSource = dataSource;
        reset();
    }
}
