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
import com.vaadin.server.data.DataSource;

/**
 * Base class for Listing components. Provides common handling for
 * {@link DataCommunicator}, {@link SelectionModel} and
 * {@link TypedDataGenerator}s.
 *
 * @param <T>
 *            listing data type
 */
public abstract class AbstractListing<T> extends AbstractComponent
        implements Listing<T> {

    private DataSource<T> dataSource;

    @Override
    public void setDataSource(DataSource<T> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource<T> getDataSource() {
        return dataSource;
    }
}
