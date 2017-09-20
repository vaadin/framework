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
package com.vaadin.data;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.SerializableFunction;

/**
 * A generic interface for listing components that use a filterable data
 * provider for showing data.
 * <p>
 * A listing component should implement either this interface or
 * {@link HasDataProvider}, but not both.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the item data type
 * @param <F>
 *            the filter type
 * @since 8.0
 *
 * @see HasDataProvider
 */
public interface HasFilterableDataProvider<T, F> extends HasItems<T> {

    /**
     * Sets the data provider for this listing. The data provider is queried for
     * displayed items as needed.
     *
     * @param dataProvider
     *            the data provider, not <code>null</code>
     */
    public default void setDataProvider(DataProvider<T, F> dataProvider) {
        setDataProvider(dataProvider, SerializableFunction.identity());
    }

    /**
     * Sets the data provider and filter converter for this listing. The data
     * provider is queried for displayed items as needed.
     *
     * @param dataProvider
     *            the data provider, not <code>null</code>
     * @param filterConverter
     *            a function that converts filter values produced by this
     *            listing into filter values expected by the provided data
     *            provider, not <code>null</code>
     */
    public <C> void setDataProvider(DataProvider<T, C> dataProvider,
            SerializableFunction<F, C> filterConverter);
}
