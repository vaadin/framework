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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.HierarchicalDataProvider;
import com.vaadin.data.provider.TreeDataProvider;

/**
 * TODO
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            the item data type
 */
public interface HasHierarchicalDataProvider<T> extends HasDataProvider<T> {

    /**
     * TODO
     *
     * @return TODO
     * @throws IllegalStateException
     *             if TODO
     */
    @SuppressWarnings("unchecked")
    public default TreeData<T> getTreeData() {
        if (getDataProvider() instanceof TreeDataProvider) {
            return ((TreeDataProvider<T>) getDataProvider()).getTreeData();
        } else {
            throw new IllegalStateException("");
        }
    }

    /**
     * TODO
     *
     * @param rootItems
     *            TODO
     * @param childItemProvider
     *            TODO
     */
    public default void setItems(Collection<T> rootItems,
            ValueProvider<T, Collection<T>> childItemProvider) {
        Objects.requireNonNull(rootItems, "Given root items may not be null");
        Objects.requireNonNull(childItemProvider,
                "Given child item provider may not be null");
        setDataProvider(new TreeDataProvider<>(
                new TreeData<T>().addItems(rootItems, childItemProvider)));
    }

    /**
     * Sets the data items of this component provided as a collection.
     * <p>
     * The provided items are wrapped into a {@link TreeDataProvider} backed by
     * a flat {@link TreeData} structure. The data provider instance is used as
     * a parameter for the {@link #setDataProvider(DataProvider)} method. It
     * means that the items collection can be accessed later on via
     * {@link #getTreeData()}:
     *
     * <pre>
     * <code>
     * HasHierarchicalDataProvider<String> treeGrid = new TreeGrid<>();
     * treeGrid.setItems(Arrays.asList("a","b"));
     * ...
     *
     * TreeData<String> data = treeGrid.getTreeData();
     * </code>
     * </pre>
     * <p>
     * The returned HierarchyData instance may be used as-is to add, remove or
     * modify items in the hierarchy. These modifications to the object are not
     * automatically reflected back to the TreeGrid. Items modified should be
     * refreshed with {@link HierarchicalDataProvider#refreshItem(Object)} and
     * when adding or removing items
     * {@link HierarchicalDataProvider#refreshAll()} should be called.
     *
     * @param items
     *            the data items to display, not {@code null}
     */
    @Override
    public default void setItems(Collection<T> items) {
        Objects.requireNonNull(items, "Given collection may not be null");
        setDataProvider(new TreeDataProvider<>(
                new TreeData<T>().addItems(null, items)));
    }

    /**
     * Sets the data items of this component provided as a stream.
     * <p>
     * The provided items are wrapped into a {@link TreeDataProvider} backed by
     * a flat {@link TreeData} structure. The data provider instance is used as
     * a parameter for the {@link #setDataProvider(DataProvider)} method. It
     * means that the items collection can be accessed later on via
     * {@link #getTreeData()}:
     *
     * <pre>
     * <code>
     * HasHierarchicalDataProvider<String> treeGrid = new TreeGrid<>();
     * treeGrid.setItems(Stream.of("a","b"));
     * ...
     *
     * TreeData<String> data = treeGrid.getTreeData();
     * </code>
     * </pre>
     * <p>
     * The returned HierarchyData instance may be used as-is to add, remove or
     * modify items in the hierarchy. These modifications to the object are not
     * automatically reflected back to the TreeGrid. Items modified should be
     * refreshed with {@link HierarchicalDataProvider#refreshItem(Object)} and
     * when adding or removing items
     * {@link HierarchicalDataProvider#refreshAll()} should be called.
     *
     * @param items
     *            the data items to display, not {@code null}
     */
    @Override
    public default void setItems(Stream<T> items) {
        Objects.requireNonNull(items, "Given stream may not be null");
        setItems(items.collect(Collectors.toList()));
    }

    /**
     * Sets the data items of this listing.
     * <p>
     * The provided items are wrapped into a {@link TreeDataProvider} backed by
     * a flat {@link TreeData} structure. The data provider instance is used as
     * a parameter for the {@link #setDataProvider(DataProvider)} method. It
     * means that the items collection can be accessed later on via
     * {@link #getTreeData()}:
     *
     * <pre>
     * <code>
     * TreeGrid<String> treeGrid = new TreeGrid<>();
     * treeGrid.setItems("a","b");
     * ...
     *
     * TreeData<String> data = treeGrid.getTreeData();
     * </code>
     * </pre>
     * <p>
     * The returned HierarchyData instance may be used as-is to add, remove or
     * modify items in the hierarchy. These modifications to the object are not
     * automatically reflected back to the TreeGrid. Items modified should be
     * refreshed with {@link HierarchicalDataProvider#refreshItem(Object)} and
     * when adding or removing items
     * {@link HierarchicalDataProvider#refreshAll()} should be called.
     *
     * @param items
     *            the data items to display, not {@code null}
     */
    @Override
    public default void setItems(@SuppressWarnings("unchecked") T... items) {
        Objects.requireNonNull(items, "Given items may not be null");
        setItems(Arrays.asList(items));
    }
}
