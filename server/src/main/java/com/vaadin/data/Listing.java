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

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;

import com.vaadin.server.data.BackEndDataProvider;
import com.vaadin.server.data.DataProvider;

/**
 * A generic interface for components that show a list of data.
 *
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the item data type
 * @param <D>
 *            the data provider type; used to provide constraints on the data
 *            provider and filter
 * @since 8.0
 */
public interface Listing<T, D extends DataProvider<T, ?>> extends Serializable {

    /**
     * Returns the source of data items used by this listing.
     *
     * @return the data provider, not null
     */
    D getDataProvider();

    /**
     * Sets the data provider for this listing. The data provider is queried for
     * displayed items as needed.
     *
     * @param dataProvider
     *            the data provider, not null
     */
    void setDataProvider(D dataProvider);

    /**
     * Sets the data items of this listing provided as a collection.
     * <p>
     * <strong>Note for component developers: </strong> If the component
     * implementing this interface uses a custom data provider and/or filter
     * types, this method should be overridden to provide the same functionality
     * with the correct data provider type. This might require filter conversion
     * or a completely custom implementation.
     *
     * @param items
     *            the data items to display, not null
     *
     */
    default void setItems(Collection<T> items) {
        setDataProvider((D) DataProvider.create(items));
    }

    /**
     * Sets the data items of this listing.
     * <p>
     * <strong>Note for component developers: </strong> If the component
     * implementing this interface uses a custom data provider and/or filter
     * types, this method should be overridden to provide the same functionality
     * with the correct data provider type. This might require filter conversion
     * or a completely custom implementation.
     *
     * @param items
     *            the data items to display
     */
    default void setItems(@SuppressWarnings("unchecked") T... items) {
        setDataProvider((D) DataProvider.create(items));
    }

    /**
     * Sets the data items of this listing provided as a stream.
     * <p>
     * This is just a shorthand for {@link #setItems(Collection)}, by
     * <b>collecting all the items in the stream to a list</b>.
     * <p>
     * <strong>Using big streams is not recommended, you should instead use a
     * lazy data provider.</strong> See {@link BackEndDataProvider} for more
     * info.
     * <p>
     * <strong>Note for component developers: </strong> If the component
     * implementing this interface uses a custom data provider and/or filter
     * types, this method should be overridden to provide the same functionality
     * with the correct data provider type. This might require filter conversion
     * or a completely custom implementation.
     *
     * @param streamOfItems
     *            the stream of data items to display, not {@code null}
     */
    default void setItems(Stream<T> streamOfItems) {
        setDataProvider((D) DataProvider.create(streamOfItems));
    }

}
