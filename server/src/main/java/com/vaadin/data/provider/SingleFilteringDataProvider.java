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
import java.util.Optional;

/**
 * Filtering data provider implementation that doesn't support further filtering
 * through the query. Filtering is only possible through
 * {@link #setFilter(Object)}.
 *
 *
 * @since
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 */
public class SingleFilteringDataProvider<T, F>
        extends DataProviderWrapper<T, Void, F>
        implements FilteringDataProvider<T, Void, F> {
    private F filter;

    /**
     * Creates a new filtering data provider by wrapping the provided data
     * provider.
     *
     * @param dataProvider
     *            the data provider to wrap, not <code>null</code>
     */
    public SingleFilteringDataProvider(DataProvider<T, F> dataProvider) {
        super(dataProvider);
        Objects.requireNonNull(dataProvider, "dataProvider cannot be null");
    }

    @Override
    public void setFilter(F filter) {
        if (Objects.equals(this.filter, filter)) {
            return;
        }
        this.filter = filter;
        refreshAll();
    }

    @Override
    protected F getFilter(Query<T, Void> query) {
        return filter;
    }

    /**
     * Gets the currently used filter.
     *
     * @return the currently used filter, or an empty optional if no filter is
     *         set
     */
    public Optional<F> getFilter() {
        return Optional.ofNullable(filter);
    }
}
