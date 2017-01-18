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
 *
 * @param <T>
 *            the data provider item type
 * @param <Q>
 *            the query filter type
 * @param <C>
 *            the configurable filter type
 */
public abstract class ConfigurableFilterDataProviderWrapper<T, Q, C>
        extends DataProviderWrapper<T, Q, C>
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
            DataProvider<T, C> dataProvider) {
        super(dataProvider);
    }

    @Override
    protected C getFilter(Query<T, Q> query) {
        return query.getFilter().map(
                queryFilter -> combineFilters(configuredFilter, queryFilter))
                .orElse(configuredFilter);
    }

    /**
     * Combines the configured filter and the filter from the query into one
     * filter instance that can be passed to the wrapped data provider. This
     * method is called only if there is a query filter, otherwise the
     * configured filter will be directly passed to the query.
     *
     * @param configuredFilter
     *            the filter that this data provider is configured to use, or
     *            <code>null</code> if no filter has been configured
     * @param queryFilter
     *            the filter received through the query, not <code>null</code>
     * @return a filter that combines the two provided queries, or
     *         <code>null</code> to not pass any filter to the wrapped data
     *         provider
     */
    protected abstract C combineFilters(C configuredFilter, Q queryFilter);

    @Override
    public void setFilter(C filter) {
        this.configuredFilter = filter;
        refreshAll();
    }
}
