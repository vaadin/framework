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
import com.vaadin.server.data.DataSource;
import com.vaadin.server.data.TypedDataGenerator;

/**
 * Base class for {@link Listing} components. Provides common handling for
 * {@link DataCommunicator} and {@link TypedDataGenerator}s.
 *
 * @param <T>
 *            listing data type
 */
public abstract class AbstractListing<T> extends AbstractComponent
        implements Listing<T> {

    /**
     * Helper base class for creating extensions for Listing components. This
     * class provides helpers for accessing the underlying parts of the
     * component and its communicational mechanism.
     *
     * @param <T>
     *            listing data type
     */
    public abstract static class AbstractListingExtension<T>
            extends AbstractExtension implements TypedDataGenerator<T> {

        /**
         * {@inheritDoc}
         * <p>
         * Note: AbstractListingExtensions need parent to be of type
         * AbstractListing.
         *
         * @throws IllegalArgument
         *             if parent is not an AbstractListing
         */
        public void extend(Listing<T> listing) {
            if (listing instanceof AbstractListing) {
                AbstractListing<T> parent = (AbstractListing<T>) listing;
                super.extend(parent);
                parent.addDataGenerator(this);
            } else {
                throw new IllegalArgumentException(
                        "Parent needs to extend AbstractListing");
            }
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
        public AbstractListing<T> getParent() {
            return (AbstractListing<T>) super.getParent();
        }

        /**
         * Helper method for refreshing a single data object.
         *
         * @param data
         *            data object to refresh
         */
        protected void refresh(T data) {
            getParent().getDataCommunicator().refresh(data);
        }
    }

    /* DataCommunicator for this Listing component */
    private final DataCommunicator<T> dataCommunicator;

    /**
     * Constructs an {@link AbstractListing}, extending it with a
     * {@link DataCommunicator}.
     */
    protected AbstractListing() {
        this(new DataCommunicator<>());
    }

    /**
     * Constructs an {@link AbstractListing}, extending it with given
     * {@link DataCommunicator}.
     * <p>
     * <strong>Note:</strong> This method is for creating an
     * {@link AbstractListing} with a custom {@link DataCommunicator}. In the
     * common case {@link AbstractListing#AbstractListing()} should be used.
     *
     * @param dataCommunicator
     *            a customized data communicator instance
     */
    protected AbstractListing(DataCommunicator<T> dataCommunicator) {
        Objects.requireNonNull(dataCommunicator,
                "The data communicator can't be null");
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

    /**
     * Adds a {@link TypedDataGenerator} for the {@link DataCommunicator} of
     * this Listing component.
     *
     * @param generator
     *            typed data generator
     */
    protected void addDataGenerator(TypedDataGenerator<T> generator) {
        dataCommunicator.addDataGenerator(generator);
    }

    /**
     * Removed a {@link TypedDataGenerator} from the {@link DataCommunicator} of
     * this Listing component.
     *
     * @param generator
     *            typed data generator
     */
    protected void removeDataGenerator(TypedDataGenerator<T> generator) {
        dataCommunicator.removeDataGenerator(generator);
    }

    /**
     * Get the {@link DataCommunicator} of this Listing component.
     *
     * @return data provider
     */
    public DataCommunicator<T> getDataCommunicator() {
        return dataCommunicator;
    }
}
