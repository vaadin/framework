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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProviderWrapper.AppendableFilterDataProviderWrapper;

/**
 * {@link Searcher} supporting multiple search filters and further filtering
 * through regular data provider methods.
 *
 * @author Vaadin Ltd
 *
 * @since
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 */
public class AppendableSearcher<T, F> extends
        AppendableFilterDataProviderWrapper<T, F> implements Searcher<T, F, F> {

    private Set<F> filters = new HashSet<>();

    private final AppendableFilterDataProvider<T, F> dataProvider;

    /**
     * Creates a new appendable searcher by wrapping the provided appendable
     * data provider.
     *
     * @param dataProvider
     *            the appendable data provider to wrap, not <code>null</code>
     */
    public AppendableSearcher(AppendableFilterDataProvider<T, F> dataProvider) {
        super(dataProvider);
        Objects.requireNonNull(dataProvider, "dataProvider cannot be null");
        this.dataProvider = dataProvider;
    }

    @Override
    protected F getFilter(Query<T, F> query) {
        return Stream
                .concat(filters.stream(), optionalToStream(query.getFilter()))
                .reduce(dataProvider::combineFilters).orElse(null);
    }

    private Stream<F> optionalToStream(Optional<F> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    /**
     * Sets a single filter to search by, clearing any previously set filters.
     * Makes all using components refresh their data with the new filter.
     *
     * @param filter
     *            the filter to search by, or <code>null</code> to clear all
     *            previously set filters.
     */
    @Override
    public void searchBy(F filter) {
        if (filter == null) {
            clearFilters();
        } else {
            if (filters.size() == 1
                    && Objects.equals(filter, filters.iterator().next())) {
                return;
            }

            filters.clear();
            addFilter(filter);
        }
    }

    /**
     * Gets an unmodifiable view of all currently used filters.
     *
     * @return the currently used filters
     */
    public Set<F> getFilters() {
        return Collections.unmodifiableSet(filters);
    }

    /**
     * Adds a filter to be used in addition to any previously added filters.
     * Makes all using components refresh their data with the new filter.
     *
     * @param filter
     *            the filter to add, not <code>null</code>
     */
    public void addFilter(F filter) {
        Objects.requireNonNull(filter, "filter cannot be null");

        if (filters.add(filter)) {
            refreshAll();
        }
    }

    /**
     * Removes all currently used filters. Makes all using components refresh
     * their data with the new filter.
     */
    public void clearFilters() {
        if (filters.isEmpty()) {
            return;
        }

        filters.clear();
        refreshAll();
    }

    @Override
    public AppendableFilterDataProvider<T, F> withFilter(F filter) {
        // Method explicitly defined here to avoid getting the less specific
        // return type definition as a default implementation.
        return super.withFilter(filter);
    }
}
