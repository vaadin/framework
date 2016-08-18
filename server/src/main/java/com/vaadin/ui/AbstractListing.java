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

import com.vaadin.data.Listing;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.DataSource;
import com.vaadin.server.data.TypedDataGenerator;

/**
 * Base class for Listing components. Provides common handling for
 * {@link DataCommunicator} and {@link TypedDataGenerator}s.
 *
 * @param <T>
 *            listing data type
 */
public abstract class AbstractListing<T> extends AbstractComponent
        implements Listing<T> {

    /* DataCommunicator for this Listing component */
    private final DataCommunicator<T> dataCommunicator = new DataCommunicator<>();

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
