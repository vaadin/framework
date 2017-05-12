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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Interface for representing hierarchical data.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 */
public interface HierarchyData<T> extends Serializable {

    /**
     * Adds a data item as a child of {@code parent}. Call with {@code null} as
     * parent to add a root level item. The given parent item must already exist
     * in this structure, and an item can only be added to this structure once.
     *
     * @param parent
     *            the parent item for which the items are added as children
     * @param item
     *            the item to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if parent is not null and not already added to this structure
     * @throws IllegalArgumentException
     *             if the item has already been added to this structure
     * @throws NullPointerException
     *             if item is null
     */
    public HierarchyData<T> addItem(T parent, T item);
    /**
     * Adds a list of data items as children of {@code parent}. Call with
     * {@code null} as parent to add root level items. The given parent item
     * must already exist in this structure, and an item can only be added to
     * this structure once.
     *
     * @param parent
     *            the parent item for which the items are added as children
     * @param items
     *            the list of items to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if parent is not null and not already added to this structure
     * @throws IllegalArgumentException
     *             if any of the given items have already been added to this
     *             structure
     * @throws NullPointerException
     *             if any of the items are null
     */
    public HierarchyData<T> addItems(T parent,
            @SuppressWarnings("unchecked") T... items);

    /**
     * Adds a list of data items as children of {@code parent}. Call with
     * {@code null} as parent to add root level items. The given parent item
     * must already exist in this structure, and an item can only be added to
     * this structure once.
     *
     * @param parent
     *            the parent item for which the items are added as children
     * @param items
     *            the collection of items to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if parent is not null and not already added to this structure
     * @throws IllegalArgumentException
     *             if any of the given items have already been added to this
     *             structure
     * @throws NullPointerException
     *             if any of the items are null
     */
    public HierarchyData<T> addItems(T parent, Collection<T> items);

    /**
     * Adds data items contained in a stream as children of {@code parent}. Call
     * with {@code null} as parent to add root level items. The given parent
     * item must already exist in this structure, and an item can only be added
     * to this structure once.
     *
     * @param parent
     *            the parent item for which the items are added as children
     * @param items
     *            stream of items to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if parent is not null and not already added to this structure
     * @throws IllegalArgumentException
     *             if any of the given items have already been added to this
     *             structure
     * @throws NullPointerException
     *             if any of the items are null
     */
    public HierarchyData<T> addItems(T parent, Stream<T> items);

    /**
     * Remove a given item from this structure. Additionally, this will
     * recursively remove any descendants of the item.
     *
     * @param item
     *            the item to remove, or null to clear all data
     * @return this
     *
     * @throws IllegalArgumentException
     *             if the item does not exist in this structure
     */
    public HierarchyData<T> removeItem(T item);

    /**
     * Clear all items from this structure. Shorthand for calling
     * {@link #removeItem(Object)} with null.
     *
     * @return this
     */
    public HierarchyData<T> clear();

    /**
     * Get the immediate child items for the given item.
     *
     * @param item
     *            the item for which to retrieve child items for, null to
     *            retrieve all root items
     * @return a list of child items for the given item
     *
     * @throws IllegalArgumentException
     *             if the item does not exist in this structure
     */
    public List<T> getChildren(T item);

    /**
     * Check whether the given item is in this hierarchy.
     *
     * @param item
     *            the item to check
     * @return {@code true} if the item is in this hierarchy, {@code false} if
     *         not
     */
    public boolean contains(T item);
}
