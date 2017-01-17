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

import com.vaadin.server.SerializableFunction;
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
 * @see #create(Collection)
 * @see #create(Stream)
 * @see #create(Object...)
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
     * Returns a new wrapped data provider with given default filter. Default
     * filter will apply to each query and further filtering will not be
     * supported in the wrapped data provider.
     *
     * @param filter
     *            the default filter
     * @return wrapped data provider with provided filter
     */
    public default DataProvider<T, ?> withFilter(F filter) {
        Objects.requireNonNull(filter, "Filter can't be null");
        return DataProviderWrapper.filter(this, filter);
    }

    /**
     * Convert the data provider to use a different filter type. It is used for
     * adapting this data provider to a filter type provided by a Component such
     * as ComboBox.
     * <p>
     * For example receiving a String from ComboBox and making a Predicate based
     * on it:
     *
     * <pre>
     * DataProvider&lt;Person, Predicate&lt;Person&gt;&gt; dataProvider;
     * // ComboBox uses String as the filter type
     * DataProvider&lt;Person, String&gt; wrappedProvider = dataProvider
     *         .convertFilter(filterText -&gt; {
     *             Predicate&lt;Person&gt; predicate = person -&gt; person.getName()
     *                     .startsWith(filterText);
     *             return predicate;
     *         });
     * comboBox.setDataProvider(wrappedProvider);
     * </pre>
     *
     * @param mapper
     *            the mapper from new filter type to old filter type; not null
     *
     * @param <M>
     *            the filter type to map from; typically provided by a Component
     *
     * @return wrapped data provider
     */
    public default <M> DataProvider<T, M> convertFilter(
            SerializableFunction<M, F> mapper) {
        Objects.requireNonNull(mapper, "Filter mapper can't be null");
        return DataProviderWrapper.convert(this, mapper);
    }

    /**
     * This method creates a new {@link ListDataProvider} from a given
     * Collection. The ListDataProvider creates a protective List copy of all
     * the contents in the Collection.
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
     * ListDataProvider creates a protective List copy of all the contents in
     * the array.
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

    /**
     * This method creates a new {@link ListDataProvider} from the given stream.
     * The ListDataProvider <b>collects all the items in the stream to a
     * list</b>.
     * <p>
     * This is just a shorthand for using {@link #create(Collection)} after
     * collecting the items in the stream to a list with e.g.
     * {@code stream.collect(Collectors.toList));}.
     * <p>
     * <strong>Using big streams is not recommended, you should instead use a
     * lazy data provider.</strong> See {@link BackEndDataProvider} for more
     * info.
     *
     * @param <T>
     *            the data item type
     * @param items
     *            a stream of data items, not {@code null}
     * @return a new list data provider
     */
    public static <T> ListDataProvider<T> create(Stream<T> items) {
        return new ListDataProvider<>(items.collect(Collectors.toList()));
    }
}
