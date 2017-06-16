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
package com.vaadin.ui;

import com.vaadin.server.SerializablePredicate;

/**
 * A callback interface for resolving whether client-side collapsing should be
 * allowed for an item in a listing component that displays hierarchical data,
 * such as {@link TreeGrid}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @see TreeGrid#setItemCollapseAllowedProvider(ItemCollapseAllowedProvider)
 *
 * @param <T>
 *            item data type
 */
@FunctionalInterface
public interface ItemCollapseAllowedProvider<T> extends SerializablePredicate<T> {

    /**
     * Returns whether collapsing is allowed for the given item.
     *
     * @param item
     *            the item to test
     * @return {@code true} if collapse is allowed for the given item,
     *         {@code false} otherwise
     */
    @Override
    boolean test(T item);
}
