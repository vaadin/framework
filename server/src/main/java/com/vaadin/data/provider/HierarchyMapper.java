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
package com.vaadin.data.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.SerializableComparator;
import com.vaadin.shared.Range;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.shared.data.HierarchicalDataCommunicatorConstants;
import com.vaadin.ui.ItemCollapseAllowedProvider;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Mapper for hierarchical data.
 * <p>
 * Keeps track of the expanded nodes, and size of of the subtrees for each
 * expanded node.
 * <p>
 * This class is framework internal implementation details, and can be changed /
 * moved at any point. This means that you should not directly use this for
 * anything.
 *
 * @author Vaadin Ltd
 * @since 8.1
 * 
 * @param <T>
 *            the data type
 * @param <F>
 *            the filter type
 */
public class HierarchyMapper<T, F> implements DataGenerator<T> {

    // childMap is only used for finding indices.
    private Map<T, List<T>> childMap = new HashMap<>();

    private final HierarchicalDataProvider<T, F> provider;
    private F filter;
    private List<QuerySortOrder> backEndSorting;
    private Comparator<T> inMemorySorting;
    private ItemCollapseAllowedProvider<T> itemCollapseAllowedProvider = t -> true;

    private Set<Object> expandedItemIds = new HashSet<>();

    /**
     * Constructs a new HierarchyMapper.
     * 
     * @param provider
     *            the hierarchical data provider for this mapper
     */
    public HierarchyMapper(HierarchicalDataProvider<T, F> provider) {
        this.provider = provider;
    }

    /**
     * Returns the size of the currently expanded hierarchy.
     * 
     * @return the amount of available data
     */
    public int getTreeSize() {
        return (int) getHierarchy(null).count();
    }

    /**
     * Finds the index of the parent of the item in given target index.
     * 
     * @param target
     *            the target index
     * @return the parent index
     * 
     * @throws IllegalArgumentException
     *             if given target index is not found
     */
    public Integer getParentIndex(int target) throws IllegalArgumentException {
        List<T> flatHierarchy = getHierarchy(null).collect(Collectors.toList());
        return flatHierarchy.indexOf(getParent(target, flatHierarchy));
    }

    /**
     * Finds the parent item for given index.
     * 
     * @param target
     *            the index of target
     * @param list
     *            the flattened hierarchy
     * @return the parent node
     * 
     * @throws IllegalArgumentException
     *             if given target index is not found
     */
    public T getParent(int target, List<T> list)
            throws IllegalArgumentException {
        if (0 <= target && target < list.size()) {
            T item = list.get(target);
            return getParentOfItem(item);
        }
        throw new IllegalArgumentException(
                "Unable to determine parent for index: " + target);
    }

    /**
     * Returns whether the given item is expanded.
     * 
     * @param item
     *            the item to test
     * @return {@code true} if item is expanded; {@code false} if not
     */
    public boolean isExpanded(T item) {
        if (item == null) {
            // Root nodes are always visible.
            return true;
        }
        return expandedItemIds.contains(getDataProvider().getId(item));
    }

    /**
     * Expands the given item.
     * 
     * @param item
     *            the item to expand
     * @return range of rows added by expanding the item
     */
    public Range expand(T item) {
        Range addedRows = Range.withLength(0, 0);
        if (!isExpanded(item) && hasChildren(item)) {
            Object id = getDataProvider().getId(item);
            expandedItemIds.add(id);
            Optional<Integer> position = getIndexOf(item);
            if (position.isPresent()) {
                addedRows = Range.withLength(position.get() + 1,
                        (int) getHierarchy(item, false).count());
            }
        }
        return addedRows;
    }

    /**
     * Collapses the given item.
     * 
     * @param item
     *            the item to expand
     * @return range of rows removed by collapsing the item
     */
    public Range collapse(T item) {
        Range removedRows = Range.withLength(0, 0);
        if (isExpanded(item)) {
            Object id = getDataProvider().getId(item);
            Optional<Integer> position = getIndexOf(item);
            if (position.isPresent()) {
                long childCount = getHierarchy(item, false).count();
                removedRows = Range.withLength(position.get() + 1,
                        (int) childCount);
            }
            expandedItemIds.remove(id);
        }
        return removedRows;
    }

    @Override
    public void generateData(T item, JsonObject jsonObject) {
        JsonObject hierarchyData = Json.createObject();

        int depth = getDepth(item);
        if (depth >= 0) {
            hierarchyData.put(HierarchicalDataCommunicatorConstants.ROW_DEPTH,
                    depth);
        }

        boolean isLeaf = !getDataProvider().hasChildren(item);
        if (isLeaf) {
            hierarchyData.put(HierarchicalDataCommunicatorConstants.ROW_LEAF,
                    true);
        } else {
            hierarchyData.put(
                    HierarchicalDataCommunicatorConstants.ROW_COLLAPSED,
                    !isExpanded(item));
            hierarchyData.put(HierarchicalDataCommunicatorConstants.ROW_LEAF,
                    false);
            hierarchyData.put(
                    HierarchicalDataCommunicatorConstants.ROW_COLLAPSE_ALLOWED,
                    getItemCollapseAllowedProvider().test(item));
        }

        // add hierarchy information to row as metadata
        jsonObject.put(
                HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION,
                hierarchyData);
    }

    private int getDepth(T item) {
        int depth = -1;
        while (item != null) {
            item = getParentOfItem(item);
            ++depth;
        }
        return depth;
    }

    private T getParentOfItem(T item) {
        for (Entry<T, List<T>> entry : childMap.entrySet()) {
            if (entry.getValue().contains(item)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException(
                "Given item does not exist in hierarchy");
    }

    /**
     * Gets the current item collapse allowed provider.
     * 
     * @return the item collapse allowed provider
     */
    public ItemCollapseAllowedProvider<T> getItemCollapseAllowedProvider() {
        return itemCollapseAllowedProvider;
    }

    /**
     * Sets the current item collapse allowed provider.
     * 
     * @param itemCollapseAllowedProvider
     *            the item collapse allowed provider
     */
    public void setItemCollapseAllowedProvider(
            ItemCollapseAllowedProvider<T> itemCollapseAllowedProvider) {
        this.itemCollapseAllowedProvider = itemCollapseAllowedProvider;
    }

    /**
     * Gets the current in-memory sorting.
     * 
     * @return the in-memory sorting
     */
    public Comparator<T> getInMemorySorting() {
        return inMemorySorting;
    }

    /**
     * Sets the current in-memory sorting. This will cause the hierarchy to be
     * constructed again.
     * 
     * @param inMemorySorting
     *            the in-memory sorting
     */
    public void setInMemorySorting(Comparator<T> inMemorySorting) {
        this.inMemorySorting = inMemorySorting;
        // Invalidate child map
        childMap.clear();
    }

    /**
     * Gets the current back-end sorting.
     * 
     * @return the back-end sorting
     */
    public List<QuerySortOrder> getBackEndSorting() {
        return backEndSorting;
    }

    /**
     * Sets the current back-end sorting. This will cause the hierarchy to be
     * constructed again.
     * 
     * @param backEndSorting
     *            the back-end sorting
     */
    public void setBackEndSorting(List<QuerySortOrder> backEndSorting) {
        this.backEndSorting = backEndSorting;
        // Invalidate child map
        childMap.clear();
    }

    /**
     * Gets the current filter.
     * 
     * @return the filter
     */
    public F getFilter() {
        return filter;
    }

    /**
     * Sets the current filter. This will cause the hierarchy to be constructed
     * again.
     * 
     * @param filter
     *            the filter
     */
    public void setFilter(Object filter) {
        this.filter = (F) filter;
        // Invalidate child map
        childMap.clear();
    }

    /**
     * Gets the {@code HierarchicalDataProvider} for this
     * {@code HierarchyMapper}.
     * 
     * @return the hierarchical data provider
     */
    public HierarchicalDataProvider<T, F> getDataProvider() {
        return provider;
    }

    /* Fetch methods. These are used to calculate what to request. */

    /**
     * 
     * <p>
     * An example on how this works. Hierarchy contains 3 levels of nodes. Each
     * node on first and second level has 3 children. The first node of the two
     * first levels are expanded:
     * 
     * <pre>
     * 1
     *  2
     *   3
     *   4
     *   5
     *  6
     *  7
     * 8
     * 9
     * </pre>
     * 
     * The original request is from index 3 to end of the hierarchy. Different
     * request ranges become {@code (parent -> range)}:
     * 
     * <pre>
     *   null -> [1..3) // Root nodes
     *      1 -> [1..3) // Second level
     *      2 -> [1..3) // Leaves
     * </pre>
     * 
     * This results into a stream of:
     * 
     * <pre>
     *  4, 5, 6, 7, 8, 9
     * </pre>
     * 
     * In this particular example, none of the parents were included in the
     * result. Should a parent be included, the {@link #doFetchItems} method
     * would take care of recursively expanding it, when fetching its parent.
     * 
     * @param range
     *            the requested item range
     * @return the stream of items
     */
    public Stream<T> fetchItems(Range range) {
        boolean pendingFetch = childMap.isEmpty();
        List<T> flatHierarchy = getHierarchy(null).collect(Collectors.toList());
        if (pendingFetch) {
            // A full fetch was pending. Data up to date
            return flatHierarchy.stream().skip(range.getStart())
                    .limit(range.length());
        }

        // Map to store the request ranges for individual items.
        Map<T, Range> splitRequests = new HashMap<>();

        // The recursive fetch will initiate from these nodes.
        Set<T> parentNodes = new LinkedHashSet<>();

        final int limit = Math.min(flatHierarchy.size(), range.getEnd());
        for (int i = range.getStart(); i < limit; ++i) {
            // Find the parent for given index
            T parent = getParent(i, flatHierarchy);
            // Find the corresponding location in parents child list.
            int parentChildIndex = childMap.get(parent)
                    .indexOf(flatHierarchy.get(i));

            if (parent == null
                    || !range.contains(flatHierarchy.indexOf(parent))) {
                // Parent will not be part of the result set.
                parentNodes.add(parent);
            }

            if (!splitRequests.containsKey(parent)) {
                // First request for this parent
                splitRequests.put(parent, Range.withOnly(parentChildIndex));
            } else {
                // Combine with earlier range
                Range expand = splitRequests.get(parent)
                        .combineWith(Range.withOnly(parentChildIndex));
                splitRequests.put(parent, expand);
            }
        }

        // Initiate recursive fetch from parents not in the result set
        return parentNodes.stream()
                .flatMap(node -> doFetchItems(node, splitRequests, false));
    }

    /**
     * Method used with {@link Stream#flatMap} to recursively fetch and combine
     * the requested ranges of items into a flat stream.
     * 
     * @param parent
     *            the parent node
     * @param splitRequests
     *            the item to range map for making the correct requests
     * @param includeParent
     *            {@code true} if parent should be included in result;
     *            {@code false} if not
     * @return the flattened hierarchy as a stream
     */
    private Stream<T> doFetchItems(T parent, Map<T, Range> splitRequests,
            boolean includeParent) {
        List<T> children = new ArrayList<>();

        // Need to fetch children
        if (splitRequests.containsKey(parent)) {
            Range range = splitRequests.get(parent);
            children = doFetchDirectChildren(parent, range)
                    .collect(Collectors.toList());
        }

        // Combine parent and children
        return combineParentAndChildStreams(parent,
                children.stream()
                        .flatMap(n -> doFetchItems(n, splitRequests, true)),
                includeParent);
    }

    /* Methods for providing information on the hierarchy. */

    /**
     * Generic method for finding direct children of a given parent, limited by
     * given range.
     * 
     * @param parent
     *            the parent
     * @param range
     *            the range of direct children to return
     * @return the requested children of the given parent
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Stream<T> doFetchDirectChildren(T parent, Range range) {
        return getDataProvider().fetchChildren(new HierarchicalQuery(
                range.getStart(), range.getEnd(), getBackEndSorting(),
                getInMemorySorting(), getFilter(), parent));
    }

    /**
     * Returns whether given item has children.
     * 
     * @param item
     *            the node to test
     * @return {@code true} if node has children; {@code false} if not
     */
    public boolean hasChildren(T item) {
        return getDataProvider().hasChildren(item);
    }

    /**
     * Finds the current index of given object. This is based on a search in
     * flattened version of the hierarchy.
     * 
     * @param target
     *            the target object to find
     * @return optional index of given object
     */
    private Optional<Integer> getIndexOf(T target) {
        if (target == null) {
            return Optional.empty();
        }

        final List<Object> collect = getHierarchy(null).map(provider::getId)
                .collect(Collectors.toList());
        int index = collect.indexOf(getDataProvider().getId(target));
        return Optional.ofNullable(index < 0 ? null : index);
    }

    /**
     * Gets the full hierarchy tree starting from given node.
     * 
     * @param parent
     *            the parent node to start from
     * @return the flattened hierarchy as a stream
     */
    private Stream<T> getHierarchy(T parent) {
        return getHierarchy(parent, true);
    }

    /**
     * Getst hte full hierarchy tree starting from given node. The starting node
     * can be omitted.
     * 
     * @param parent
     *            the parent node to start from
     * @param includeParent
     *            {@code true} to include the parent; {@code false} if not
     * @return the flattened hierarchy as a stream
     */
    private Stream<T> getHierarchy(T parent, boolean includeParent) {
        return Stream.of(parent)
                .flatMap(node -> getChildrenStream(node, includeParent));
    }

    /**
     * Gets the stream of direct children for given node.
     * 
     * @param parent
     *            the parent node
     * @return the stream of direct children
     */
    private Stream<T> getDirectChildren(T parent) {
        T key = parent;
        if (parent != null) {
            Object id = getDataProvider().getId(parent);
            key = childMap.keySet().stream().filter(
                    k -> k != null && getDataProvider().getId(k).equals(id))
                    .findFirst().orElse(parent);
        }
        if (childMap.containsKey(key)) {
            return childMap.get(key).stream();
        }
        return doFetchDirectChildren(parent, Range.between(0, getDataProvider()
                .getChildCount(new HierarchicalQuery<>(filter, parent))));
    }

    /**
     * The method to recursively fetch the children of given parent. Used with
     * {@link Stream#flatMap} to expand a stream of parent nodes into a
     * flattened hierarchy.
     * 
     * @param parent
     *            the parent node
     * @return the stream of all children under the parent, includes the parent
     */
    private Stream<T> getChildrenStream(T parent) {
        return getChildrenStream(parent, true);
    }

    /**
     * The method to recursively fetch the children of given parent. Used with
     * {@link Stream#flatMap} to expand a stream of parent nodes into a
     * flattened hierarchy.
     * 
     * @param parent
     *            the parent node
     * @param includeParent
     *            {@code true} to include the parent in the stream;
     *            {@code false} if not
     * @return the stream of all children under the parent
     */
    private Stream<T> getChildrenStream(T parent, boolean includeParent) {
        List<T> childList = Collections.emptyList();
        if (isExpanded(parent)) {
            if (!childMap.containsKey(parent)) {
                Stream<T> children = getDirectChildren(parent);
                childMap.put(parent, children.collect(Collectors.toList()));
            }
            childList = childMap.get(parent);
        }
        return combineParentAndChildStreams(parent,
                childList.stream().flatMap(this::getChildrenStream),
                includeParent);
    }

    /**
     * Helper method for combining parent and a stream of children into one
     * stream. {@code null} item is never included, and parent can be skipped by
     * providing the correct value for {@code includeParent}.
     * 
     * @param parent
     *            the parent node
     * @param children
     *            the stream of children
     * @param includeParent
     *            {@code true} to include the parent in the stream;
     *            {@code false} if not
     * @return the combined stream of parent and its children
     */
    private Stream<T> combineParentAndChildStreams(T parent, Stream<T> children,
            boolean includeParent) {
        boolean parentIncluded = includeParent && parent != null;
        Stream<T> parentStream = parentIncluded ? Stream.of(parent)
                : Stream.empty();
        return Stream.concat(parentStream, children);
    }
}
