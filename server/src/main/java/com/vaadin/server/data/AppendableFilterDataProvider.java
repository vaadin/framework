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

import java.util.Objects;
import java.util.Optional;

/**
 * Interface for DataProviders that support chaining filters.
 *
 * @author Vaadin Ltd
 * @since
 *
 * @param <T>
 *            the data provider data type
 * @param <F>
 *            the data provider filter type
 */
public interface AppendableFilterDataProvider<T, F> extends DataProvider<T, F> {

    /**
     * Applies a filter to the current chain of filters in this data provider.
     *
     * @param filter
     *            the applied filter; not {@code null}
     * @return new data provider with the filter applied
     */
    public default AppendableFilterDataProvider<T, F> applyFilter(F filter) {
        Objects.requireNonNull(filter, "The applied filter can't be null");
        return DataProviderWrapper.chain(this, filter);
    }

    /**
     * Combines two filters into one. Default implementation returns the base
     * filter if the optional is not present.
     * <p>
     * Actual implementation of the combination is done in the method
     * {@link #combineFilters(Object, Object)}.
     *
     * @param filter1
     *            the base filter; not {@code null}
     * @param filter2
     *            an optional filter
     * @return combined filter; not {@code null}
     */
    public default F combineFilters(F filter1, Optional<F> filter2) {
        Objects.requireNonNull(filter1, "The base filter can't be null");
        return filter2.map(f -> combineFilters(filter1, f)).orElse(filter1);
    }

    /**
     * Combines two filters into one.
     *
     * @param filter1
     *            the base filter; not {@code null}
     * @param filter2
     *            the filter to merge to the base filter; not {@code null}
     * @return combined filter; not {@code null}
     */
    public F combineFilters(F filter1, F filter2);

}
