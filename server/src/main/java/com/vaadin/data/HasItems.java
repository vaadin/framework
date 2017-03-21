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
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.BackEndDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Component;

/**
 * A component that displays a collection of items.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 *
 * @param <T>
 *            the type of the displayed item
 */
public interface HasItems<T> extends Component, Serializable {

    /**
     * Returns the source of data items used by this listing.
     *
     * @return the data provider, not null
     */
    public DataProvider<T, ?> getDataProvider();

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
     * HasDataProvider&lt;String&gt; listing = new CheckBoxGroup&lt;&gt;();
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
    public void setItems(Collection<T> items);

    /**
     * Sets the data items of this listing.
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
     * listing.setItems("a","b");
     * ...
     *
     * Collection<String> collection = ((ListDataProvider<String>)listing.getDataProvider()).getItems();
     * </code>
     * </pre>
     * <p>
     *
     * @see #setItems(Collection)
     *
     * @param items
     *            the data items to display
     */
    public default void setItems(@SuppressWarnings("unchecked") T... items) {
        setItems(Arrays.asList(items));
    }

    /**
     * Sets the data items of this listing provided as a stream.
     * <p>
     * This is just a shorthand for {@link #setItems(Collection)}, that
     * <b>collects objects in the stream to a list</b>. Thus, using this method,
     * instead of its array and Collection variations, doesn't save any memory.
     * If you have a large data set to bind, using a lazy data provider is
     * recommended. See {@link BackEndDataProvider} for more info.
     * <p>
     * The provided items are wrapped into a {@link ListDataProvider} and this
     * instance is used as a data provider for the
     * {@link #setDataProvider(DataProvider)} method. It means that the items
     * collection can be accessed later on via
     * {@link ListDataProvider#getItems()}:
     *
     * <pre>
     * <code>
     * HasDataProvider&lt;String&gt; listing = new CheckBoxGroup<&gt;();
     * listing.setItems(Stream.of("a","b"));
     * ...
     *
     * Collection<String> collection = ((ListDataProvider&lt;String&gt;)listing.getDataProvider()).getItems();
     * </code>
     * </pre>
     * <p>
     *
     * @see #setItems(Collection)
     *
     * @param streamOfItems
     *            the stream of data items to display, not {@code null}
     */
    public default void setItems(Stream<T> streamOfItems) {
        setItems(streamOfItems.collect(Collectors.toList()));
    }
}
