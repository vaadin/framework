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
package com.vaadin.ui;

import java.util.Objects;

import com.vaadin.data.Listing;
import com.vaadin.data.selection.SelectionModel;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataGenerator;
import com.vaadin.server.data.DataSource;

/**
 * A base class for listing components. Provides common handling for fetching
 * backend data items, selection logic, and server-client communication.
 * 
 * @param <T>
 *            the item data type
 * @param <SELECTIONMODEL>
 *            the selection logic supported by this listing
 */
public abstract class AbstractListing<T, SELECTIONMODEL extends SelectionModel<T>>
        extends AbstractComponent implements Listing<T, SELECTIONMODEL> {

    /**
     * A helper base class for creating extensions for Listing components. This
     * class provides helpers for accessing the underlying parts of the
     * component and its communication mechanism.
     *
     * @param <T>
     *            the listing item type
     */
    public abstract static class AbstractListingExtension<T>
            extends AbstractExtension implements DataGenerator<T> {

        /**
         * Adds this extension to the given parent listing.
         * 
         * @param listing
         *            the parent component to add to
         */
        public void extend(AbstractListing<T, ?> listing) {
            super.extend(listing);
            listing.addDataGenerator(this);
        }

        @Override
        public void remove() {
            getParent().removeDataGenerator(this);

            super.remove();
        }

        /**
         * Gets a data object based on its client-side identifier key.
         *
         * @param key
         *            key for data object
         * @return the data object
         */
        protected T getData(String key) {
            return getParent().getDataCommunicator().getKeyMapper().get(key);
        }

        @Override
        @SuppressWarnings("unchecked")
        public AbstractListing<T, ?> getParent() {
            return (AbstractListing<T, ?>) super.getParent();
        }

        /**
         * A helper method for refreshing the client-side representation of a
         * single data item.
         *
         * @param item
         *            the item to refresh
         */
        protected void refresh(T item) {
            getParent().getDataCommunicator().refresh(item);
        }
    }

    private final DataCommunicator<T> dataCommunicator;

    private SELECTIONMODEL selectionModel;

    /**
     * Creates a new {@code AbstractListing} using the given selection model.
     * 
     * @param selectionModel
     *            the selection model to use, not null
     */
    protected AbstractListing(SELECTIONMODEL selectionModel) {
        this(selectionModel, new DataCommunicator<>());
    }

    /**
     * Creates a new {@code AbstractListing} with the given selection model and
     * data communicator.
     * <p>
     * <strong>Note:</strong> This method is for creating an
     * {@link AbstractListing} with a custom {@link DataCommunicator}. In the
     * common case {@link AbstractListing#AbstractListing()} should be used.
     *
     * @param selectionModel
     *            the selection model to use, not null
     * @param dataCommunicator
     *            the custom data communicator to use, not null
     */
    protected AbstractListing(SELECTIONMODEL selectionModel,
            DataCommunicator<T> dataCommunicator) {
        Objects.requireNonNull(selectionModel, "selectionModel cannot be null");
        Objects.requireNonNull(dataCommunicator,
                "dataCommunicator cannot be null");

        this.selectionModel = selectionModel;
        this.dataCommunicator = dataCommunicator;
        addExtension(dataCommunicator);
    }

    @Override
    public void setDataSource(DataSource<T> dataSource) {
        getDataCommunicator().setDataSource(dataSource);
    }

    @Override
    public DataSource<T> getDataSource() {
        return getDataCommunicator().getDataSource();
    }

    @Override
    public SELECTIONMODEL getSelectionModel() {
        return selectionModel;
    }

    /**
     * Adds the given data generator to this listing. If the generator was
     * already added, does nothing.
     *
     * @param generator
     *            the data generator to add, not null
     */
    protected void addDataGenerator(DataGenerator<T> generator) {
        getDataCommunicator().addDataGenerator(generator);
    }

    /**
     * Removes the given data generator from this listing. If this listing does
     * not have the generator, does nothing.
     *
     * @param generator
     *            the data generator to remove, not null
     */
    protected void removeDataGenerator(DataGenerator<T> generator) {
        getDataCommunicator().removeDataGenerator(generator);
    }

    /**
     * Returns the data communicator of this listing.
     *
     * @return the data communicator, not null
     */
    public DataCommunicator<T> getDataCommunicator() {
        return dataCommunicator;
    }
}
