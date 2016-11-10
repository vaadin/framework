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
 * Minimal DataProvider API for communication between the DataProvider and a back
 * end service.
 *
 * @author Vaadin Ltd.
 * 
 * @param <T>
 *            data type
 *
 * @see ListDataProvider
 * @see BackEndDataProvider
 *
 * @since 8.0
 */
public interface DataProvider<T> extends Serializable {

    /**
     * Gets whether the DataProvider content all available in memory or does it
     * use some external backend.
     *
     * @return {@code true} if all data is in memory; {@code false} if not
     */
    boolean isInMemory();

    /**
     * Gets the amount of data in this DataProvider.
     *
     * @param t
     *            query with sorting and filtering
     * @return the size of the data provider
     */
    int size(Query t);

    /**
     * Fetches data from this DataProvider using given {@code query}.
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
     * Adds a data provider listener. The listener is called when some piece of
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
    Registration addDataProviderListener(DataProviderListener listener);

    /**
     * This method creates a new {@link ListDataProvider} from a given Collection.
     * The ListDataProvider creates a protective List copy of all the contents in
     * the Collection.
     * 
     * @param <T>
     *            the data item type
     * @param items
     *            the collection of data, not null
     * @return a new list data provider
     */
    public static <T> ListDataProvider<T> create(Collection<T> items) {
        return new ListDataProvider<>(items);
    }

    /**
     * This method creates a new {@link ListDataProvider} from given objects.The
     * ListDataProvider creates a protective List copy of all the contents in the
     * array.
     * 
     * @param <T>
     *            the data item type
     * @param items
     *            the data items
     * @return a new list data provider
     */
    @SafeVarargs
    public static <T> ListDataProvider<T> create(T... items) {
        return new ListDataProvider<>(Arrays.asList(items));
    }
}
