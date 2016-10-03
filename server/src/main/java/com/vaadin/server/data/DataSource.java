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
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import com.vaadin.shared.Registration;

/**
 * Minimal DataSource API for communication between the DataProvider and a back
 * end service.
 *
 * @author Vaadin Ltd.
 * 
 * @param <T>
 *            data type
 *
 * @see ListDataSource
 * @see BackEndDataSource
 *
 * @since 8.0
 */
public interface DataSource<T> extends Serializable {

    /**
     * Gets whether the DataSource content all available in memory or does it
     * use some external backend.
     *
     * @return {@code true} if all data is in memory; {@code false} if not
     */
    boolean isInMemory();

    /**
     * Gets the amount of data in this DataSource.
     *
     * @param t
     *            query with sorting and filtering
     * @return the size of the data source
     */
    int size(Query t);

    /**
     * Fetches data from this DataSource using given {@code query}.
     * 
     * @param query
     *            given query to request data
     * @return the result of the query request: a stream of data objects, not
     *         {@code null}
     */
    Stream<T> fetch(Query query);

    /**
     * Refreshes all data based on currently available data in the underlying
     * provider.
     */
    void refreshAll();

    /**
     * Adds a data source listener. The listener is called when some piece of
     * data is updated.
     * <p>
     * The {@link #refreshAll()} method fires {@link DataChangeEvent} each time
     * when it's called. It allows to update UI components when user changes
     * something in the underlying data.
     *
     * @see #refreshAll()
     * @param listener
     *            the data change listener, not null
     * @return a registration for the listener
     */
    Registration addDataSourceListener(DataSourceListener listener);

    /**
     * This method creates a new {@link ListDataSource} from a given Collection.
     * The ListDataSource creates a protective List copy of all the contents in
     * the Collection.
     * 
     * @param <T>
     *            the data item type
     * @param items
     *            the collection of data, not null
     * @return a new list data source
     */
    public static <T> ListDataSource<T> create(Collection<T> items) {
        return new ListDataSource<>(items);
    }

    /**
     * This method creates a new {@link ListDataSource} from given objects.The
     * ListDataSource creates a protective List copy of all the contents in the
     * array.
     * 
     * @param <T>
     *            the data item type
     * @param items
     *            the data items
     * @return a new list data source
     */
    @SafeVarargs
    public static <T> ListDataSource<T> create(T... items) {
        return new ListDataSource<>(Arrays.asList(items));
    }
}
