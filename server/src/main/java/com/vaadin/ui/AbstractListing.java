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
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataGenerator;
import com.vaadin.server.data.DataSource;
import com.vaadin.shared.data.selection.SelectionModel;

/**
 * A base class for listing components. Provides common handling for fetching
 * backend data items, selection logic, and server-client communication.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the item data type
 * @param <SELECTIONMODEL>
 *            the selection logic supported by this listing
 *
 * @since 8.0
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
     * Creates a new {@code AbstractListing} with a default data communicator.
     * <p>
     * <strong>Note:</strong> This constructor does not set a selection model
     * for the new listing. The invoking constructor must explicitly call
     * {@link #setSelectionModel(SelectionModel)}.
     */
    protected AbstractListing() {
        this(new DataCommunicator<>());
    }

    /**
     * Creates a new {@code AbstractListing} with the given custom data
     * communicator.
     * <p>
     * <strong>Note:</strong> This method is for creating an
     * {@code AbstractListing} with a custom communicator. In the common case
     * {@link AbstractListing#AbstractListing()} should be used.
     * <p>
     * <strong>Note:</strong> This constructor does not set a selection model
     * for the new listing. The invoking constructor must explicitly call
     * {@link #setSelectionModel(SelectionModel)}.
     *
     * @param dataCommunicator
     *            the data communicator to use, not null
     */
    protected AbstractListing(DataCommunicator<T> dataCommunicator) {
        Objects.requireNonNull(dataCommunicator,
                "dataCommunicator cannot be null");

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
        assert selectionModel != null : "No selection model set by "
                + getClass().getName() + " constructor";
        return selectionModel;
    }

    /**
     * Sets the selection model for this listing.
     *
     * @param model
     *            the selection model to use, not null
     */
    protected void setSelectionModel(SELECTIONMODEL model) {
        if (selectionModel != null) {
            throw new IllegalStateException(
                    "A selection model can't be changed.");
        }

        Objects.requireNonNull(model, "selection model cannot be null");
        selectionModel = model;
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
