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
 * Class for representing hierarchical data.
 * 
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 */
public class HierarchyData<T> implements Serializable {

    private static class HierarchyWrapper<T> implements Serializable {
        private T item;
        private T parent;
        private List<T> children;

        public HierarchyWrapper(T item, T parent) {
            this.item = item;
            this.parent = parent;
            children = new ArrayList<>();
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public T getParent() {
            return parent;
        }

        public void setParent(T parent) {
            this.parent = parent;
        }

        public List<T> getChildren() {
            return children;
        }

        public void setChildren(List<T> children) {
            this.children = children;
        }

        public void addChild(T child) {
            children.add(child);
        }

        public void removeChild(T child) {
            children.remove(child);
        }
    }

    private final Map<T, HierarchyWrapper<T>> itemToWrapperMap;

    /**
     * Creates an initially empty hierarchical data representation to which
     * items can be added or removed.
     */
    public HierarchyData() {
        itemToWrapperMap = new LinkedHashMap<>();
        itemToWrapperMap.put(null, new HierarchyWrapper<>(null, null));
    }

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
    public HierarchyData<T> addItem(T parent, T item) {
        Objects.requireNonNull(item, "Item cannot be null");
        if (parent != null && !contains(parent)) {
            throw new IllegalArgumentException(
                    "Parent needs to be added before children. "
                            + "To add root items, call with parent as null");
        }
        if (contains(item)) {
            throw new IllegalArgumentException(
                    "Cannot add the same item multiple times: " + item);
        }
        putItem(item, parent);
        return this;
    }

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
            @SuppressWarnings("unchecked") T... items) {
        Arrays.asList(items).stream().forEach(item -> addItem(parent, item));
        return this;
    }

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
    public HierarchyData<T> addItems(T parent, Collection<T> items) {
        items.stream().forEach(item -> addItem(parent, item));
        return this;
    }

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
    public HierarchyData<T> addItems(T parent, Stream<T> items) {
        items.forEach(item -> addItem(parent, item));
        return this;
    }

    /**
     * Remove a given item from this structure. Additionally, this will
     * recursively remove any descendants of the item.
     * 
     * @param item
     *            the item to remove
     * @return this
     * 
     * @throws IllegalArgumentException
     *             if the item does not exist in this structure
     */
    public HierarchyData<T> removeItem(T item) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in the hierarchy");
        }
        getChildren(item).forEach(child -> removeItem(child));
        itemToWrapperMap.get(itemToWrapperMap.get(item).getParent())
                .removeChild(item);
        return this;
    }

    /**
     * Get the immediate child items for the given item.
     * 
     * @param item
     *            the item for which to retrieve child items for
     * @return a list of child items for the given item
     * 
     * @throws IllegalArgumentException
     *             if the item does not exist in this structure
     */
    public List<T> getChildren(T item) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in the hierarchy");
        }
        return itemToWrapperMap.get(item).getChildren();
    }

    private boolean contains(T item) {
        return itemToWrapperMap.containsKey(item);
    }

    private void putItem(T item, T parent) {
        HierarchyWrapper<T> wrappedItem = new HierarchyWrapper<>(item, parent);
        if (itemToWrapperMap.containsKey(parent)) {
            itemToWrapperMap.get(parent).addChild(item);
        }
        itemToWrapperMap.put(item, wrappedItem);
    }
}
