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
 * Data provider wrapper with a configurable filter. Changing the filter makes
 * all using components refresh their data based on the new filter.
 * <p>
 * A filtering data provider has two different type parameters related to
 * filtering. The <code>F</code> type parameter defines the filtering type used
 * in {@link Query} instances passed to this data provider and is also used as
 * the basis for further filtering using methods such as
 * {@link #convertFilter(com.vaadin.server.SerializableFunction)}. The
 * <code>S</code> type parameter defined the type of the filter passed to
 * {@link #setFilter(Object)} and related methods. A
 * <code>FilteringDataProvider&lt;Person, Void, String&gt;</code> is thus a
 * filtering data provider that doesn't expect any filter in the {@link Query}
 * passed to {@link #fetch(Query)} and {@link #size(Query)}, but filtering can
 * be set through {@link #setFilter(Object)} as a string.
 *
 * @since
 *
 * @param <T>
 *            the type of data items provided by this data provider
 * @param <F>
 *            filter type used in queries and further filtering.
 * @param <S>
 *            filter type used for the configurable filtering of this data
 *            provider
 */
public interface FilteringDataProvider<T, F, S> extends DataProvider<T, F> {

    /**
     * Sets the filter to filter by. Makes all using components refresh their
     * data with the new filter if the set filter is not {@link #equals(Object)}
     * to the current filter.
     *
     * @param filter
     *            the filter to search by, or <code>null</code> to clear a
     *            previously set filter.
     */
    void setFilter(S filter);

}
