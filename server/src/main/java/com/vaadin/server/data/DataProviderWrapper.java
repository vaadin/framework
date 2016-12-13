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

import java.util.stream.Stream;

import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.Registration;

/**
 * Wrapper class for modifying, chaining and replacing filters and sorting in a
 * query. Used to create a suitable {@link Query} for the underlying data
 * provider with correct filters and sorting.
 *
 * @author Vaadin Ltd.
 * @since
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            wrapper query filter type
 * @param <M>
 *            underlying data provider filter type
 */
public abstract class DataProviderWrapper<T, F, M>
        implements DataProvider<T, F> {

    /**
     * Variant of data provider wrapper that supports chaining filters.
     *
     * @param <T>
     *            the data provider data type
     * @param <F>
     *            the data provider filter type
     */
    protected abstract static class AppendableFilterDataProviderWrapper<T, F>
            extends DataProviderWrapper<T, F, F>
            implements AppendableFilterDataProvider<T, F> {

        /**
         * Constructs a filtering wrapper for a data provider with filter
         * chaining.
         *
         * @param dataProvider
         *            the wrapped data provider
         */
        protected AppendableFilterDataProviderWrapper(
                AppendableFilterDataProvider<T, F> dataProvider) {
            super(dataProvider);
        }

        @Override
        public F combineFilters(F filter1, F filter2) {
            return ((AppendableFilterDataProvider<T, F>) dataProvider)
                    .combineFilters(filter1, filter2);
        }
    }

    /**
     * The actual data provider behind this wrapper.
     */
    protected DataProvider<T, M> dataProvider;

    /**
     * Constructs a filtering wrapper for a data provider.
     *
     * @param dataProvider
     *            the wrapped data provider
     */
    protected DataProviderWrapper(DataProvider<T, M> dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public boolean isInMemory() {
        return dataProvider.isInMemory();
    }

    @Override
    public void refreshAll() {
        dataProvider.refreshAll();
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener listener) {
        return dataProvider.addDataProviderListener(listener);
    }

    @Override
    public int size(Query<T, F> t) {
        return dataProvider.size(new Query<>(t.getOffset(), t.getLimit(),
                t.getSortOrders(), t.getInMemorySorting(), getFilter(t)));
    }

    @Override
    public Stream<T> fetch(Query<T, F> t) {
        return dataProvider.fetch(new Query<>(t.getOffset(), t.getLimit(),
                t.getSortOrders(), t.getInMemorySorting(), getFilter(t)));
    }

    /**
     * Gets the filter that should be used in the modified Query.
     *
     * @param query
     *            the current query
     * @return filter for the modified Query
     */
    protected abstract M getFilter(Query<T, F> query);

    /**
     * Creates a data provider wrapper with a static filter set to each Query.
     * This {@code DataProvider} will deliberately ignore any possible filters
     * from the Query.
     *
     * @see DataProvider#withFilter(Object)
     *
     * @param dataProvider
     *            the underlying data provider
     * @param filter
     *            the static filter for each query
     *
     * @param <T>
     *            data provider data type
     * @param <F>
     *            query filter type
     *
     * @return wrapped data provider with static filter
     */
    public static <T, F> DataProvider<T, Void> filter(
            DataProvider<T, F> dataProvider, F filter) {
        return new DataProviderWrapper<T, Void, F>(dataProvider) {

            @Override
            protected F getFilter(Query<T, Void> query) {
                return filter;
            }
        };
    }

    /**
     * Creates a data provider wrapper with filter type mapping. The mapper
     * function will be applied to a query filter if it is present.
     *
     * @see DataProvider#convertFilter(SerializableFunction)
     *
     * @param dataProvider
     *            the underlying data provider
     * @param mapper
     *            the function to map from one filter type to another
     *
     * @param <T>
     *            data provider data type
     * @param <F>
     *            wrapper query filter type
     * @param <M>
     *            underlying data provider filter type
     *
     * @return wrapped data provider with filter conversion
     */
    public static <T, F, M> DataProvider<T, F> convert(
            DataProvider<T, M> dataProvider,
            SerializableFunction<F, M> mapper) {
        return new DataProviderWrapper<T, F, M>(dataProvider) {

            @Override
            protected M getFilter(Query<T, F> query) {
                return query.getFilter().map(mapper).orElse(null);
            }
        };
    }

    /**
     * Creates a data provider wrapper with a chained filter. The filter will be
     * combined to existing filters using
     * {@link AppendableFilterDataProvider#combineFilters(Object, Object)}.
     *
     * @param dataProvider
     *            the underlying data provider
     * @param filter
     *            the chained filter
     *
     * @param <T>
     *            data provider data type
     * @param <F>
     *            query filter type
     * @return wrapped data provider with chained filter
     */
    public static <T, F> AppendableFilterDataProvider<T, F> chain(
            AppendableFilterDataProvider<T, F> dataProvider, F filter) {
        return new AppendableFilterDataProviderWrapper<T, F>(dataProvider) {

            @Override
            protected F getFilter(Query<T, F> query) {
                return query.getFilter().map(f -> combineFilters(filter, f))
                        .orElse(filter);
            }
        };
    }
}
