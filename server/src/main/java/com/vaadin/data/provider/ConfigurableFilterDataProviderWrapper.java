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

/**
 * A configurable data provider that wraps another data provider by combining
 * any filter from the component with the configured filter and passing that to
 * the wrapped provider through the query.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            the data provider item type
 * @param <Q>
 *            the query filter type
 * @param <C>
 *            the configurable filter type
 * @param <F>
 *            the filter type of the wrapped data provider
 */
public abstract class ConfigurableFilterDataProviderWrapper<T, Q, C, F>
        extends DataProviderWrapper<T, Q, F>
        implements ConfigurableFilterDataProvider<T, Q, C> {

    private C configuredFilter;

    /**
     * Creates a new configurable filter data provider by wrapping an existing
     * data provider.
     *
     * @param dataProvider
     *            the data provider to wrap, not <code>null</code>
     */
    public ConfigurableFilterDataProviderWrapper(
            DataProvider<T, F> dataProvider) {
        super(dataProvider);
    }

    @Override
    protected F getFilter(Query<T, Q> query) {
        Q queryFilter = query.getFilter().orElse(null);
        if (configuredFilter == null && queryFilter == null) {
            return null;
        }
        return combineFilters(queryFilter, configuredFilter);
    }

    /**
     * Combines the configured filter and the filter from the query into one
     * filter instance that can be passed to the wrapped data provider. Will not
     * be called if the configured filter is <code>null</code> and the query has
     * no filter.
     *
     * @param queryFilter
     *            the filter received through the query, or <code>null</code> if
     *            no filter was provided in the query
     * @param configuredFilter
     *            the filter that this data provider is configured to use, or
     *            <code>null</code> if no filter has been configured
     * @return a filter that combines the two provided queries, or
     *         <code>null</code> to not pass any filter to the wrapped data
     *         provider
     */
    protected abstract F combineFilters(Q queryFilter, C configuredFilter);

    @Override
    public void setFilter(C filter) {
        this.configuredFilter = filter;
        refreshAll();
    }
}
