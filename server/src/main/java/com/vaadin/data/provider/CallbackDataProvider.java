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
import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.data.ValueProvider;

/**
 * Data provider that uses one callback for fetching items from a back end and
 * another callback for counting the number of available items.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            data provider filter type
 */
public class CallbackDataProvider<T, F>
        extends AbstractBackEndDataProvider<T, F> {
    /**
     * Callback interface for fetching a stream of items from a backend based on
     * a query.
     *
     * @param <T>
     *            the type of the items to fetch
     * @param <F>
     *            the type of the optional filter in the query,
     *            <code>Void</code> if filtering is not supported
     */
    @FunctionalInterface
    public interface FetchCallback<T, F> extends Serializable {
        /**
         * Fetches a stream of items based on a query. The query defines the
         * paging of the items to fetch through {@link Query#getOffset()} and
         * {@link Query#getLimit()}, the sorting through
         * {@link Query#getSortOrders()} and optionally also any filtering to
         * use through {@link Query#getFilter()}.
         *
         * @param query
         *            the query that defines which items to fetch
         * @return a stream of items
         */
        public Stream<T> fetch(Query<T, F> query);
    }

    /**
     * Callback interface for counting the number of items in a backend based on
     * a query.
     *
     * @param <T>
     *            the type of the items to count
     * @param <F>
     *            the type of the optional filter in the query,
     *            <code>Void</code> if filtering is not supported
     */
    @FunctionalInterface
    public interface CountCallback<T, F> extends Serializable {
        /**
         * Counts the number of available items based on a query. The query
         * optionally defines any filtering to use through
         * {@link Query#getFilter()}. The query also contains information about
         * paging and sorting although that information is generally not
         * applicable for determining the number of items.
         *
         * @param query
         *            the query that defines which items to count
         * @return the number of available items
         */
        public int count(Query<T, F> query);
    }

    private final FetchCallback<T, F> fetchCallback;
    private final CountCallback<T, F> countCallback;
    private final ValueProvider<T, Object> idGetter;

    /**
     * Constructs a new DataProvider to request data using callbacks for
     * fetching and counting items in the back end.
     *
     * @param fetchCallback
     *            function that returns a stream of items from the back end for
     *            a query
     * @param countCallback
     *            function that return the number of items in the back end for a
     *            query
     *
     * @see #CallbackDataProvider(FetchCallback, CountCallback, ValueProvider)
     */
    public CallbackDataProvider(FetchCallback<T, F> fetchCallback,
            CountCallback<T, F> countCallback) {
        this(fetchCallback, countCallback, t -> t);
    }

    /**
     * Constructs a new DataProvider to request data using callbacks for
     * fetching and counting items in the back end.
     *
     * @param fetchCallBack
     *            function that requests data from back end based on query
     * @param countCallback
     *            function that returns the amount of data in back end for query
     * @param identifierGetter
     *            function that returns the identifier for a given item
     */
    public CallbackDataProvider(FetchCallback<T, F> fetchCallBack,
            CountCallback<T, F> countCallback,
            ValueProvider<T, Object> identifierGetter) {
        Objects.requireNonNull(fetchCallBack, "Request function can't be null");
        Objects.requireNonNull(countCallback, "Count callback can't be null");
        Objects.requireNonNull(identifierGetter,
                "Identifier getter function can't be null");
        this.fetchCallback = fetchCallBack;
        this.countCallback = countCallback;
        this.idGetter = identifierGetter;
    }

    @Override
    public Stream<T> fetchFromBackEnd(Query<T, F> query) {
        return fetchCallback.fetch(query);
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        return countCallback.count(query);
    }

    @Override
    public Object getId(T item) {
        Object itemId = idGetter.apply(item);
        assert itemId != null : "CallbackDataProvider got null as an id for item: "
                + item;
        return itemId;
    }
}
