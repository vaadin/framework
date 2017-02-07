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

import java.util.Collection;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;

/**
 * A generic interface for listing components that use a data provider for
 * showing data.
 * <p>
 * A listing component should implement either this interface or
 * {@link HasFilterableDataProvider}, but not both.
 * 
 * @author Vaadin Ltd.
 *
 * @param <T>
 *            the item data type
 * @since 8.0
 *
 * @see HasFilterableDataProvider
 */
public interface HasDataProvider<T> extends HasItems<T> {

    /**
     * Returns the source of data items used by this listing.
     *
     * @return the data provider, not null
     */
    public DataProvider<T, ?> getDataProvider();

    /**
     * Sets the data provider for this listing. The data provider is queried for
     * displayed items as needed.
     *
     * @param dataProvider
     *            the data provider, not null
     */
    public void setDataProvider(DataProvider<T, ?> dataProvider);

    /**
     * Sets the data items of this component provided as a collection.
     * <p>
     * The provided items are wrapped into a {@link ListDataProvider} and this
     * instance is used as a data provider for the
     * {@link #setDataProvider(DataProvider)} method. It means that the items
     * collection can be accessed later on via
     * {@link ListDataProvider#getItems()}:
     * 
     * <pre>
     * <code>
     * HasDataProvider<String> listing = new CheckBoxGroup<>();
     * listing.setItems(Arrays.asList("a","b"));
     * ...
     * 
     * Collection<String> collection = ((ListDataProvider<String>)listing.getDataProvider()).getItems();
     * </code>
     * </pre>
     * <p>
     * The provided collection instance may be used as-is. Subsequent
     * modification of the collection might cause inconsistent data to be shown
     * in the component unless it is explicitly instructed to read the data
     * again.
     *
     * @param items
     *            the data items to display, not null
     *
     */
    @Override
    public default void setItems(Collection<T> items) {
        setDataProvider(DataProvider.ofCollection(items));
    }

}
