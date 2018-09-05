/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.TreeDataProvider;

/**
 * Class for representing hierarchical data.
 * <p>
 * Typically used as a backing data source for {@link TreeDataProvider}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 */
public class TreeData<T> implements Serializable {

    private static class HierarchyWrapper<T> implements Serializable {
        private T parent;
        private List<T> children;

        public HierarchyWrapper(T parent) {
            this.parent = parent;
            children = new ArrayList<>();
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
    public TreeData() {
        itemToWrapperMap = new LinkedHashMap<>();
        itemToWrapperMap.put(null, new HierarchyWrapper<>(null));
    }

    /**
     * Adds the items as root items to this structure.
     *
     * @param items
     *            the items to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if any of the given items have already been added to this
     *             structure
     * @throws NullPointerException
     *             if any of the items are {code null}
     */
    public TreeData<T> addRootItems(T... items) {
        addItems(null, items);
        return this;
    }

    /**
     * Adds the items of the given collection as root items to this structure.
     *
     * @param items
     *            the collection of items to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if any of the given items have already been added to this
     *             structure
     * @throws NullPointerException
     *             if any of the items are {code null}
     */
    public TreeData<T> addRootItems(Collection<T> items) {
        addItems(null, items);
        return this;
    }

    /**
     * Adds the items of the given stream as root items to this structure.
     *
     * @param items
     *            the stream of root items to add
     * @return this
     *
     * @throws IllegalArgumentException
     *             if any of the given items have already been added to this
     *             structure
     * @throws NullPointerException
     *             if any of the items are {code null}
     */
    public TreeData<T> addRootItems(Stream<T> items) {
        addItems(null, items);
        return this;
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
    public TreeData<T> addItem(T parent, T item) {
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
    public TreeData<T> addItems(T parent,
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
    public TreeData<T> addItems(T parent, Collection<T> items) {
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
    public TreeData<T> addItems(T parent, Stream<T> items) {
        items.forEach(item -> addItem(parent, item));
        return this;
    }

    /**
     * Adds the given items as root items and uses the given value provider to
     * recursively populate children of the root items.
     *
     * @param rootItems
     *            the root items to add
     * @param childItemProvider
     *            the value provider used to recursively populate this TreeData
     *            from the given root items
     * @return this
     */
    public TreeData<T> addItems(Collection<T> rootItems,
            ValueProvider<T, Collection<T>> childItemProvider) {
        rootItems.forEach(item -> {
            addItem(null, item);
            Collection<T> childItems = childItemProvider.apply(item);
            addItems(item, childItems);
            addItemsRecursively(childItems, childItemProvider);
        });
        return this;
    }

    /**
     * Adds the given items as root items and uses the given value provider to
     * recursively populate children of the root items.
     *
     * @param rootItems
     *            the root items to add
     * @param childItemProvider
     *            the value provider used to recursively populate this TreeData
     *            from the given root items
     * @return this
     */
    public TreeData<T> addItems(Stream<T> rootItems,
            ValueProvider<T, Stream<T>> childItemProvider) {
        // Must collect to lists since the algorithm iterates multiple times
        return addItems(rootItems.collect(Collectors.toList()),
                item -> childItemProvider.apply(item)
                        .collect(Collectors.toList()));
    }

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
    public TreeData<T> removeItem(T item) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in the hierarchy");
        }
        new ArrayList<>(getChildren(item)).forEach(child -> removeItem(child));
        itemToWrapperMap.get(itemToWrapperMap.get(item).getParent())
                .removeChild(item);
        if (item != null) {
            // remove non root item from backing map
            itemToWrapperMap.remove(item);
        }
        return this;
    }

    /**
     * Clear all items from this structure. Shorthand for calling
     * {@link #removeItem(Object)} with null.
     *
     * @return this
     */
    public TreeData<T> clear() {
        removeItem(null);
        return this;
    }

    /**
     * Gets the root items of this structure.
     *
     * @return an unmodifiable list of root items of this structure
     */
    public List<T> getRootItems() {
        return getChildren(null);
    }

    /**
     * Get the immediate child items for the given item.
     *
     * @param item
     *            the item for which to retrieve child items for, null to
     *            retrieve all root items
     * @return an unmodifiable list of child items for the given item
     *
     * @throws IllegalArgumentException
     *             if the item does not exist in this structure
     */
    public List<T> getChildren(T item) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in the hierarchy");
        }
        return Collections
                .unmodifiableList(itemToWrapperMap.get(item).getChildren());
    }

    /**
     * Get the parent item for the given item.
     *
     * @param item
     *            the item for which to retrieve the parent item for
     * @return parent item for the given item or {@code null} if the item is a
     *         root item.
     * @throws IllegalArgumentException
     *             if the item does not exist in this structure
     * @since 8.1.1
     */
    public T getParent(T item) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in hierarchy");
        }
        return itemToWrapperMap.get(item).getParent();
    }

    /**
     * Moves an item to become a child of the given parent item. The new parent
     * item must exist in the hierarchy. Setting the parent to {@code null}
     * makes the item a root item. After making changes to the tree data,
     * {@link TreeDataProvider#refreshAll()} should be called.
     *
     * @param item
     *            the item to be set as the child of {@code parent}
     * @param parent
     *            the item to be set as parent or {@code null} to set the item
     *            as root
     * @since 8.1
     */
    public void setParent(T item, T parent) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in the hierarchy");
        }

        if (parent != null && !contains(parent)) {
            throw new IllegalArgumentException(
                    "Parent needs to be added before children. "
                            + "To set as root item, call with parent as null");
        }

        if (item.equals(parent)) {
            throw new IllegalArgumentException(
                    "Item cannot be the parent of itself");
        }

        T oldParent = itemToWrapperMap.get(item).getParent();

        if (!Objects.equals(oldParent, parent)) {
            // Remove item from old parent's children
            itemToWrapperMap.get(oldParent).removeChild(item);

            // Add item to parent's children
            itemToWrapperMap.get(parent).addChild(item);

            // Set item's new parent
            itemToWrapperMap.get(item).setParent(parent);
        }
    }

    /**
     * Moves an item to the position immediately after a sibling item. The two
     * items must have the same parent. After making changes to the tree data,
     * {@link TreeDataProvider#refreshAll()} should be called.
     *
     * @param item
     *            the item to be moved
     * @param sibling
     *            the item after which the moved item will be located, or {@code
     *         null} to move item to first position
     * @since 8.1
     */
    public void moveAfterSibling(T item, T sibling) {
        if (!contains(item)) {
            throw new IllegalArgumentException(
                    "Item '" + item + "' not in the hierarchy");
        }

        if (sibling == null) {
            List<T> children = itemToWrapperMap.get(getParent(item))
                    .getChildren();

            // Move item to first position
            children.remove(item);
            children.add(0, item);
        } else {
            if (!contains(sibling)) {
                throw new IllegalArgumentException(
                        "Item '" + sibling + "' not in the hierarchy");
            }

            T parent = itemToWrapperMap.get(item).getParent();

            if (!Objects.equals(parent,
                    itemToWrapperMap.get(sibling).getParent())) {
                throw new IllegalArgumentException("Items '" + item + "' and '"
                        + sibling + "' don't have the same parent");
            }

            List<T> children = itemToWrapperMap.get(parent).getChildren();

            // Move item to the position after the sibling
            children.remove(item);
            children.add(children.indexOf(sibling) + 1, item);
        }
    }

    /**
     * Check whether the given item is in this hierarchy.
     *
     * @param item
     *            the item to check
     * @return {@code true} if the item is in this hierarchy, {@code false} if
     *         not
     */
    public boolean contains(T item) {
        return itemToWrapperMap.containsKey(item);
    }

    private void putItem(T item, T parent) {
        HierarchyWrapper<T> wrappedItem = new HierarchyWrapper<>(parent);
        if (itemToWrapperMap.containsKey(parent)) {
            itemToWrapperMap.get(parent).addChild(item);
        }
        itemToWrapperMap.put(item, wrappedItem);
    }

    private void addItemsRecursively(Collection<T> items,
            ValueProvider<T, Collection<T>> childItemProvider) {
        items.forEach(item -> {
            Collection<T> childItems = childItemProvider.apply(item);
            addItems(item, childItems);
            addItemsRecursively(childItems, childItemProvider);
        });
    }
}
