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
import com.vaadin.ui.Component;

/**
 * A component that displays a collection of items.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            the type of the displayed item
 */
public interface HasItems<T> extends Component, Serializable {
    /**
     * Sets the data items of this component provided as a collection.
     *
     * @param items
     *            the data items to display, not null
     *
     */
    void setItems(Collection<T> items);

    /**
     * Sets the data items of this listing.
     *
     * @param items
     *            the data items to display
     */
    default void setItems(@SuppressWarnings("unchecked") T... items) {
        setItems(Arrays.asList(items));
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
     *
     * @param streamOfItems
     *            the stream of data items to display, not {@code null}
     */
    default void setItems(Stream<T> streamOfItems) {
        setItems(streamOfItems.collect(Collectors.toList()));
    }
}
