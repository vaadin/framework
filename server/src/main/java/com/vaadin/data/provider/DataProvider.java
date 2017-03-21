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
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.HasDataProvider;
import com.vaadin.data.HasFilterableDataProvider;
import com.vaadin.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.server.SerializableBiFunction;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializableToIntFunction;
import com.vaadin.shared.Registration;

/**
 * A common interface for fetching data from a backend. The {@link DataProvider}
 * interface is used by listing components implementing {@link HasDataProvider}
 * or {@link HasFilterableDataProvider}. The listing component will provide a
 * {@link Query} object with request information, and the data provider uses
 * this information to return a stream containing requested beans.
 * <p>
 * Vaadin comes with a ready-made solution for in-memory data, known as
 * {@link ListDataProvider} which can be created using static {@code create}
 * methods in this interface. For custom backends such as SQL, EntityManager,
 * REST APIs or SpringData, use a {@link BackEndDataProvider} or its subclass.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 *
 * @see #ofCollection(Collection)
 * @see #ofItems(Object...)
 * @see #fromStream(Stream)
 * @see #fromCallbacks(FetchCallback, CountCallback)
 * @see #fromFilteringCallbacks(FetchCallback, CountCallback)
 * @see ListDataProvider
 * @see BackEndDataProvider
 *
 * @since 8.0
 */
public interface DataProvider<T, F> extends Serializable {

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
     * @param query
     *            query with sorting and filtering
     * @return the size of the data provider
     */
    int size(Query<T, F> query);

    /**
     * Fetches data from this DataProvider using given {@code query}.
     *
     * @param query
     *            given query to request data
     * @return the result of the query request: a stream of data objects, not
     *         {@code null}
     */
    Stream<T> fetch(Query<T, F> query);

    /**
     * Refreshes the given item. This method should be used to inform all
     * {@link DataProviderListener DataProviderListeners} that an item has been
     * updated or replaced with a new instance.
     * <p>
     * For this to work properly, the item must either implement
     * {@link #equals(Object)} and {@link #hashCode()} to consider both the old
     * and the new item instances to be equal, or alternatively
     * {@link #getId(Object)} should be implemented to return an appropriate
     * identifier.
     *
     * @see #getId(Object)
     *
     * @param item
     *            the item to refresh
     */
    void refreshItem(T item);

    /**
     * Refreshes all data based on currently available data in the underlying
     * provider.
     */
    void refreshAll();

    /**
     * Gets an identifier for the given item. This identifier is used by the
     * framework to determine equality between two items.
     * <p>
     * Default is to use item itself as its own identifier. If the item has
     * {@link Object#equals(Object)} and {@link Object#hashCode()} implemented
     * in a way that it can be compared to other items, no changes are required.
     *
     * @param item
     *            the item to get identifier for; not {@code null}
     * @return the identifier for given item; not {@code null}
     */
    public default Object getId(T item) {
        Objects.requireNonNull(item, "Cannot provide an id for a null item.");
        return item;
    }

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
    Registration addDataProviderListener(DataProviderListener<T> listener);

    /**
     * Wraps this data provider to create a data provider that uses a different
     * filter type. This can be used for adapting this data provider to a filter
     * type provided by a Component such as ComboBox.
     * <p>
     * For example receiving a String from ComboBox and making a Predicate based
     * on it:
     *
     * <pre>
     * DataProvider&lt;Person, Predicate&lt;Person&gt;&gt; dataProvider;
     * // ComboBox uses String as the filter type
     * DataProvider&lt;Person, String&gt; wrappedProvider = dataProvider
     *         .withConvertedFilter(filterText -&gt; {
     *             Predicate&lt;Person&gt; predicate = person -&gt; person.getName()
     *                     .startsWith(filterText);
     *             return predicate;
     *         });
     * comboBox.setDataProvider(wrappedProvider);
     * </pre>
     *
     * @param filterConverter
     *            callback that converts the filter in the query of the wrapped
     *            data provider into a filter supported by this data provider.
     *            Will only be called if the query contains a filter. Not
     *            <code>null</code>
     *
     * @param <C>
     *            the filter type that the wrapped data provider accepts;
     *            typically provided by a Component
     *
     * @return wrapped data provider, not <code>null</code>
     */
    public default <C> DataProvider<T, C> withConvertedFilter(
            SerializableFunction<C, F> filterConverter) {
        Objects.requireNonNull(filterConverter,
                "Filter converter can't be null");
        return new DataProviderWrapper<T, C, F>(this) {
            @Override
            protected F getFilter(Query<T, C> query) {
                return query.getFilter().map(filterConverter).orElse(null);
            }
        };
    }

    /**
     * Wraps this data provider to create a data provider that supports
     * programmatically setting a filter that will be combined with a filter
     * provided through the query.
     *
     * @see #withConfigurableFilter()
     * @see ConfigurableFilterDataProvider#setFilter(Object)
     *
     * @param filterCombiner
     *            a callback for combining and the configured filter with the
     *            filter from the query to get a filter to pass to the wrapped
     *            provider. Either parameter might be <code>null</code>, but the
     *            callback will not be invoked at all if both would be
     *            <code>null</code>. Not <code>null</code>.
     *
     * @return a data provider with a configurable filter, not <code>null</code>
     */
    public default <Q, C> ConfigurableFilterDataProvider<T, Q, C> withConfigurableFilter(
            SerializableBiFunction<Q, C, F> filterCombiner) {
        return new ConfigurableFilterDataProviderWrapper<T, Q, C, F>(this) {
            @Override
            protected F combineFilters(Q queryFilter, C configuredFilter) {
                return filterCombiner.apply(queryFilter, configuredFilter);
            }
        };
    }

    /**
     * Wraps this data provider to create a data provider that supports
     * programmatically setting a filter but no filtering through the query.
     *
     * @see #withConfigurableFilter(SerializableBiFunction)
     * @see ConfigurableFilterDataProvider#setFilter(Object)
     *
     * @return a data provider with a configurable filter, not <code>null</code>
     */
    public default ConfigurableFilterDataProvider<T, Void, F> withConfigurableFilter() {
        return withConfigurableFilter((queryFilter, configuredFilter) -> {
            assert queryFilter == null : "Filter from Void query must be null";

            return configuredFilter;
        });
    }

    /**
     * Creates a new data provider backed by a collection.
     * <p>
     * The collection is used as-is. Changes in the collection will be visible
     * via the created data provider. The caller should copy the collection if
     * necessary.
     *
     * @param <T>
     *            the data item type
     * @param items
     *            the collection of data, not <code>null</code>
     * @return a new list data provider
     */
    public static <T> ListDataProvider<T> ofCollection(Collection<T> items) {
        return new ListDataProvider<>(items);
    }

    /**
     * Creates a new data provider from the given items.
     * <p>
     * The items are copied into a new backing list, so structural changes to
     * the provided array will not be visible via the created data provider.
     *
     * @param <T>
     *            the data item type
     * @param items
     *            the data items
     * @return a new list data provider
     */
    @SafeVarargs
    public static <T> ListDataProvider<T> ofItems(T... items) {
        return new ListDataProvider<>(Arrays.asList(items));
    }

    /**
     * Creates a new data provider from the given stream. <b>All items in the
     * stream are eagerly collected to a list.</b>
     * <p>
     * This is a shorthand for using {@link #ofCollection(Collection)} after
     * collecting the items in the stream to a list with e.g.
     * {@code stream.collect(Collectors.toList));}.
     * <p>
     * <strong>Using big streams is not recommended, you should instead use a
     * lazy data provider.</strong> See
     * {@link #fromCallbacks(FetchCallback, CountCallback)}
     * or {@link BackEndDataProvider} for more info.
     *
     * @param <T>
     *            the data item type
     * @param items
     *            a stream of data items, not {@code null}
     * @return a new list data provider
     */
    public static <T> ListDataProvider<T> fromStream(Stream<T> items) {
        return new ListDataProvider<>(items.collect(Collectors.toList()));
    }

    /**
     * Creates a new data provider that uses filtering callbacks for fetching
     * and counting items from any backing store.
     * <p>
     * The query that is passed to each callback may contain a filter value that
     * is provided by the component querying for data.
     *
     * @param fetchCallback
     *            function that returns a stream of items from the back end for
     *            a query
     * @param countCallback
     *            function that returns the number of items in the back end for
     *            a query
     * @return a new callback data provider
     */
    public static <T, F> CallbackDataProvider<T, F> fromFilteringCallbacks(
            FetchCallback<T, F> fetchCallback,
            CountCallback<T, F> countCallback) {
        return new CallbackDataProvider<>(fetchCallback, countCallback);
    }

    /**
     * Creates a new data provider that uses callbacks for fetching and counting
     * items from any backing store.
     * <p>
     * The query that is passed to each callback will not contain any filter
     * values.
     *
     * @param fetchCallback
     *            function that returns a stream of items from the back end for
     *            a query
     * @param countCallback
     *            function that returns the number of items in the back end for
     *            a query
     * @return a new callback data provider
     */
    public static <T> CallbackDataProvider<T, Void> fromCallbacks(
            FetchCallback<T, Void> fetchCallback,
            CountCallback<T, Void> countCallback) {
        return fromFilteringCallbacks(fetchCallback, countCallback);
    }
}
