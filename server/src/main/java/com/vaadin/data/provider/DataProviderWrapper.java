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

import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.shared.Registration;

/**
 * Wrapper class for modifying, chaining and replacing filters and sorting in a
 * query. Used to create a suitable {@link Query} for the underlying data
 * provider with correct filters and sorting.
 *
 * @author Vaadin Ltd.
 * @since 8.0
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
     * The actual data provider behind this wrapper.
     */
    protected DataProvider<T, M> dataProvider;

    /**
     * Constructs a filtering wrapper for a data provider.
     *
     * @param dataProvider
     *            the wrapped data provider, not <code>null</code>
     */
    protected DataProviderWrapper(DataProvider<T, M> dataProvider) {
        this.dataProvider = Objects.requireNonNull(dataProvider,
                "The wrapped data provider cannot be null.");
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
    public void refreshItem(T item) {
        dataProvider.refreshItem(item);
    }

    @Override
    public Object getId(T item) {
        return dataProvider.getId(item);
    }

    @Override
    public Registration addDataProviderListener(
            DataProviderListener<T> listener) {
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
}
