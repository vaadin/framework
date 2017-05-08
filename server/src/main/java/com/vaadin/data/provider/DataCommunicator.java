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
package com.vaadin.data.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataChangeEvent.DataRefreshEvent;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.KeyMapper;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.extension.datacommunicator.DataCommunicatorState;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * DataProvider base class. This class is the base for all DataProvider
 * communication implementations. It uses {@link DataGenerator}s to write
 * {@link JsonObject}s representing each data object to be sent to the
 * client-side.
 *
 * @param <T>
 *            the bean type
 *
 * @since 8.0
 */
public class DataCommunicator<T> extends AbstractExtension {

    private Registration dataProviderUpdateRegistration;

    /**
     * Simple implementation of collection data provider communication. All data
     * is sent by server automatically and no data is requested by client.
     */
    protected class SimpleDataRequestRpc implements DataRequestRpc {

        @Override
        public void requestRows(int firstRowIndex, int numberOfRows,
                int firstCachedRowIndex, int cacheSize) {
            onRequestRows(firstRowIndex, numberOfRows, firstCachedRowIndex,
                    cacheSize);
        }

        @Override
        public void dropRows(JsonArray keys) {
            onDropRows(keys);
        }
    }

    /**
     * A class for handling currently active data and dropping data that is no
     * longer needed. Data tracking is based on key string provided by
     * {@link DataKeyMapper}.
     * <p>
     * When the {@link DataCommunicator} is pushing new data to the client-side
     * via {@link DataCommunicator#pushData(int, Stream)},
     * {@link #addActiveData(Stream)} and {@link #cleanUp(Stream)} are called
     * with the same parameter. In the clean up method any dropped data objects
     * that are not in the given collection will be cleaned up and
     * {@link DataGenerator#destroyData(Object)} will be called for them.
     */
    protected class ActiveDataHandler
            implements Serializable, DataGenerator<T> {

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

        @Override
        public void destroyAllData() {
            activeData.clear();
            getKeyMapper().removeAll();
        }
    }

    private final Collection<DataGenerator<T>> generators = new LinkedHashSet<>();
    private final ActiveDataHandler handler = new ActiveDataHandler();

    /** Empty default data provider */
    protected DataProvider<T, ?> dataProvider = new CallbackDataProvider<>(
            q -> Stream.empty(), q -> 0);
    private final DataKeyMapper<T> keyMapper;

    protected boolean reset = false;
    private final Set<T> updatedData = new HashSet<>();
    private int minPushSize = 40;
    private Range pushRows = Range.withLength(0, minPushSize);

    private Object filter;
    private Comparator<T> inMemorySorting;
    private final List<QuerySortOrder> backEndSorting = new ArrayList<>();
    private final DataCommunicatorClientRpc rpc;

    public DataCommunicator() {
        addDataGenerator(handler);
        rpc = getRpcProxy(DataCommunicatorClientRpc.class);
        registerRpc(createRpc());
        keyMapper = createKeyMapper();
    }

    @Override
    public void attach() {
        super.attach();
        attachDataProviderListener();
    }

    @Override
    public void detach() {
        super.detach();
        detachDataProviderListener();
    }

    /**
     * Set the range of rows to push for next response.
     *
     * @param pushRows
     * @since 8.0.6
     */
    protected void setPushRows(Range pushRows) {
        this.pushRows = pushRows;
    }

    /**
     * Get the current range of rows to push in the next response.
     *
     * @return the range of rows to push
     * @since 8.0.6
     */
    protected Range getPushRows() {
        return pushRows;
    }

    /**
     * Get the object used for filtering in this data communicator.
     *
     * @return the filter object of this data communicator
     * @since 8.0.6
     */
    protected Object getFilter() {
        return filter;
    }

    /**
     * Get the client rpc interface for this data communicator.
     *
     * @return the client rpc interface for this data communicator
     * @since 8.0.6
     */
    protected DataCommunicatorClientRpc getClientRpc() {
        return rpc;
    }

    /**
     * Request the given rows to be available on the client side.
     *
     * @param firstRowIndex
     *            the index of the first requested row
     * @param numberOfRows
     *            the number of requested rows
     * @param firstCachedRowIndex
     *            the index of the first cached row
     * @param cacheSize
     *            the number of cached rows
     * @since 8.0.6
     */
    protected void onRequestRows(int firstRowIndex, int numberOfRows,
            int firstCachedRowIndex, int cacheSize) {
        setPushRows(Range.withLength(firstRowIndex, numberOfRows));
        markAsDirty();
    }

    /**
     * Triggered when rows have been dropped from the client side cache.
     *
     * @param keys
     *            the keys of the rows that have been dropped
     * @since 8.0.6
     */
    protected void onDropRows(JsonArray keys) {
        for (int i = 0; i < keys.length(); ++i) {
            handler.dropActiveData(keys.getString(i));
        }
    }

    /**
     * Initially and in the case of a reset all data should be pushed to the
     * client.
     */
    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        sendDataToClient(initial);
    }

    /**
     * Send the needed data and updates to the client side.
     *
     * @param initial
     *            {@code true} if initial data load, {@code false} if not
     * @since 8.0.6
     */
    protected void sendDataToClient(boolean initial) {
        if (getDataProvider() == null) {
            return;
        }

        if (initial || reset) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            int dataProviderSize = getDataProvider().size(new Query(filter));
            rpc.reset(dataProviderSize);
        }

        Range requestedRows = getPushRows();
        boolean triggerReset = false;
        if (!requestedRows.isEmpty()) {
            int offset = requestedRows.getStart();
            int limit = requestedRows.length();

            @SuppressWarnings({ "rawtypes", "unchecked" })
            List<T> rowsToPush = (List<T>) getDataProvider()
                    .fetch(new Query(offset, limit, backEndSorting,
                            inMemorySorting, filter))
                    .collect(Collectors.toList());

            if (!initial && !reset && rowsToPush.size() == 0) {
                triggerReset = true;
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

        setPushRows(Range.withLength(0, 0));
        reset = triggerReset;
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
    public void addDataGenerator(DataGenerator<T> generator) {
        Objects.requireNonNull(generator, "generator cannot be null");
        generators.add(generator);

        // Make sure data gets generated when adding data generators.
        reset();
    }

    /**
     * Removes a data generator from this data communicator. If there is no such
     * data generator, does nothing.
     *
     * @param generator
     *            the data generator to remove, not null
     */
    public void removeDataGenerator(DataGenerator<T> generator) {
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
    protected void pushData(int firstIndex, List<T> data) {
        JsonArray dataArray = Json.createArray();

        int i = 0;
        for (T item : data) {
            dataArray.set(i++, getDataObject(item));
        }

        rpc.setData(firstIndex, dataArray);
        handler.addActiveData(data.stream());
        handler.cleanUp(data.stream());
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

        for (DataGenerator<T> generator : generators) {
            generator.generateData(data, dataObject);
        }

        return dataObject;
    }

    /**
     * Returns the active data handler.
     *
     * @return the active data handler
     * @since 8.0.6
     */
    protected ActiveDataHandler getActiveDataHandler() {
        return handler;
    }

    /**
     * Drops data objects identified by given keys from memory. This will invoke
     * {@link DataGenerator#destroyData} for each of those objects.
     *
     * @param droppedKeys
     *            collection of dropped keys
     */
    private void dropData(Collection<String> droppedKeys) {
        for (String key : droppedKeys) {
            assert key != null : "Bookkeepping failure. Dropping a null key";

            T data = getKeyMapper().get(key);
            assert data != null : "Bookkeepping failure. No data object to match key";

            for (DataGenerator<T> g : generators) {
                g.destroyData(data);
            }
        }
    }

    private void dropAllData() {
        for (DataGenerator<T> g : generators) {
            g.destroyAllData();
        }
        handler.destroyAllData();
    }

    /**
     * Informs the DataProvider that the collection has changed.
     */
    public void reset() {
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
        if (!handler.getActiveData().contains(data)) {
            // Item is not currently available at the client-side
            return;
        }

        if (updatedData.isEmpty()) {
            markAsDirty();
        }

        updatedData.add(data);
    }

    /**
     * Returns the currently set updated data.
     *
     * @return the set of data that should be updated on the next response
     * @since 8.0.6
     */
    protected Set<T> getUpdatedData() {
        return updatedData;
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
     * Returns the {@link Comparator} to use with in-memory sorting.
     *
     * @return comparator used to sort data
     * @since 8.0.6
     */
    public Comparator<T> getInMemorySorting() {
        return inMemorySorting;
    }

    /**
     * Sets the {@link QuerySortOrder}s to use with backend sorting.
     *
     * @param sortOrder
     *            list of sort order information to pass to a query
     */
    public void setBackEndSorting(List<QuerySortOrder> sortOrder) {
        backEndSorting.clear();
        backEndSorting.addAll(sortOrder);
        reset();
    }

    /**
     * Returns the {@link QuerySortOrder} to use with backend sorting.
     *
     * @return list of sort order information to pass to a query
     * @since 8.0.6
     */
    public List<QuerySortOrder> getBackEndSorting() {
        return backEndSorting;
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
     * Gets the current data provider from this DataCommunicator.
     *
     * @return the data provider
     */
    public DataProvider<T, ?> getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the current data provider for this DataCommunicator.
     * <p>
     * The returned consumer can be used to set some other filter value that
     * should be included in queries sent to the data provider. It is only valid
     * until another data provider is set.
     *
     * @param dataProvider
     *            the data provider to set, not <code>null</code>
     * @param initialFilter
     *            the initial filter value to use, or <code>null</code> to not
     *            use any initial filter value
     *
     * @param <F>
     *            the filter type
     *
     * @return a consumer that accepts a new filter value to use
     */
    public <F> SerializableConsumer<F> setDataProvider(
            DataProvider<T, F> dataProvider, F initialFilter) {
        Objects.requireNonNull(dataProvider, "data provider cannot be null");
        filter = initialFilter;
        detachDataProviderListener();
        dropAllData();
        this.dataProvider = dataProvider;

        /*
         * This introduces behavior which influence on the client-server
         * communication: now the very first response to the client will always
         * contain some data. If data provider has been set already then {@code
         * pushRows} is empty at this point. So without the next line the very
         * first response will be without data. And the client will request more
         * data in the next request after the response. The next line allows to
         * send some data (in the {@code pushRows} range) to the client even in
         * the very first response. This is necessary for disabled component
         * (and theoretically allows to the client doesn't request more data in
         * a happy path).
         */
        setPushRows(Range.between(0, getMinPushSize()));
        if (isAttached()) {
            attachDataProviderListener();
        }
        reset();

        return filter -> {
            if (this.dataProvider != dataProvider) {
                throw new IllegalStateException(
                        "Filter slot is no longer valid after data provider has been changed");
            }

            if (!Objects.equals(this.filter, filter)) {
                this.filter = filter;
                reset();
            }
        };
    }

    /**
     * Set minimum size of data which will be sent to the client when data
     * source is set.
     * <p>
     * Server doesn't send all data from data source to the client. It sends
     * some initial chunk of data (whose size is determined as minimum between
     * {@code size} parameter of this method and data size). Client decides
     * whether it is able to show more data and request server to send more data
     * (next chunk).
     * <p>
     * When component is disabled then client cannot communicate to the server
     * side (by design, because of security reasons). It means that client will
     * get <b>only</b> initial chunk of data whose size is set here.
     *
     * @param size
     *            the size of initial data to send to the client
     */
    public void setMinPushSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
        minPushSize = size;
    }

    /**
     * Get minimum size of data which will be sent to the client when data
     * source is set.
     *
     * @see #setMinPushSize(int)
     *
     * @return current minimum push size of initial data chunk which is sent to
     *         the client when data source is set
     */
    public int getMinPushSize() {
        return minPushSize;
    }

    @Override
    protected DataCommunicatorState getState(boolean markAsDirty) {
        return (DataCommunicatorState) super.getState(markAsDirty);
    }

    @Override
    protected DataCommunicatorState getState() {
        return (DataCommunicatorState) super.getState();
    }

    private void attachDataProviderListener() {
        dataProviderUpdateRegistration = getDataProvider()
                .addDataProviderListener(event -> {
                    getUI().access(() -> {
                        if (event instanceof DataRefreshEvent) {
                            T item = ((DataRefreshEvent<T>) event).getItem();
                            generators.forEach(g -> g.refreshData(item));
                            keyMapper.refresh(item, dataProvider::getId);
                            refresh(item);
                        } else {
                            reset();
                        }
                    });
                });
    }

    private void detachDataProviderListener() {
        if (dataProviderUpdateRegistration != null) {
            dataProviderUpdateRegistration.remove();
            dataProviderUpdateRegistration = null;
        }
    }
}
