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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.shared.Range;
import com.vaadin.shared.data.HierarchicalDataCommunicatorConstants;
import com.vaadin.ui.ItemCollapseAllowedProvider;

import elemental.json.Json;
import elemental.json.JsonObject;

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

    // childMap is only used for finding parents of items and clean up on
    // removing children of expanded nodes.
    // TODO: 27/11/2017 Get rid of child map
    private Map<T, Set<T>> childMap = new HashMap<>();

    /**
     *
     */
    private Map<Object, T> parentMap = new HashMap<>();

    private final HierarchicalDataProvider<T, F> provider;
    private F filter;
    private List<QuerySortOrder> backEndSorting;
    private Comparator<T> inMemorySorting;
    private ItemCollapseAllowedProvider<T> itemCollapseAllowedProvider = t -> true;

    private Set<Object> expandedItemIds = new HashSet<>();

    /**
     * Maps items' ID to their order among siblings.
     */
    private Map<Object, Integer> siblingIndex = new HashMap<>();
    private T referenceItem = null;
    private int referenceItemIndex = -1;

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
     * @param item
     *            the item to get the parent of
     * @return the parent index or a negative value if the parent is not found
     *
     */
    public Integer getParentIndex(T item) {
        // TODO: This can be optimized.
        List<T> flatHierarchy = getHierarchy(null).collect(Collectors.toList());
        return flatHierarchy.indexOf(getParentOfItem(item));
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
     * @param position
     *            the index of item
     * @return range of rows added by expanding the item
     */
    public Range doExpand(T item, Optional<Integer> position) {
        Range rows = Range.withLength(0, 0);
        if (!isExpanded(item) && hasChildren(item)) {
            Object id = getDataProvider().getId(item);
            expandedItemIds.add(id);
            if (position.isPresent()) {
                rows = Range.withLength(position.get() + 1,
                        (int) getHierarchy(item, false).count());

                // Move reference forward if an item was expanded before it
                if (rows.getStart() <= referenceItemIndex) {
                    shiftReferenceItem(rows.length());
                }
            }
        }
        return rows;
    }

    /**
     * Collapses the given item.
     *
     * @param item
     *            the item to expand
     * @param position
     *            the index of item
     *
     * @return range of rows removed by collapsing the item
     */
    public Range doCollapse(T item, Optional<Integer> position) {
        Range removedRows = Range.withLength(0, 0);
        if (isExpanded(item)) {
            Object id = getDataProvider().getId(item);
            if (position.isPresent()) {
                long childCount = getHierarchy(item, false).count();
                removedRows = Range.withLength(position.get() + 1,
                        (int) childCount);

                if (removedRows.contains(referenceItemIndex)) {
                    // Remove reference if ancestor was collapsed
                    resetReferenceItem();
                } else if (removedRows.getStart() < referenceItemIndex) {
                    // Move reference up if an item was collapsed before it
                    shiftReferenceItem(-removedRows.length());
                }
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
        resetReferenceItem();
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
        resetReferenceItem();
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
        resetReferenceItem();
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

    /* Fetch methods. These are used to calculate what to request. */

    /**
     * Gets a stream of items in the form of a flattened hierarchy from the
     * back-end and filter the wanted results from the list.
     *
     * @param range
     *            the requested item range
     * @return the stream of items
     */
    public Stream<T> fetchItems(Range range) {
        List<T> items = new ArrayList<>(range.length());

        if (referenceItem != null) {
            // Fetch items before the reference
            items.addAll(fetchItemsBefore(referenceItem,
                    referenceItemIndex - range.getStart()).stream()
                    .limit(range.length()).collect(Collectors.toList()));

            // Add the reference item to the list
            if (range.contains(referenceItemIndex)) {
                items.add(referenceItem);
            }

            // Fetch items after the reference
            items.addAll(fetchItemsAfter(referenceItem,
                    range.getEnd() - referenceItemIndex - 1).stream()
                    .skip(Math.max(0, range.getStart() - referenceItemIndex - 1))
                    .collect(Collectors.toList()));
        } else {
            // Get complete flattened hierarchy when there is no reference
            items = getHierarchy(null).skip(range.getStart())
                    .limit(range.length()).collect(Collectors.toList());
        }

        // Set reference as top of the fetched list
        setReferenceItem(items.get(0), range.getStart());

        return items.stream();
    }

    /**
     * Gets a stream of children for the given item in the form of a flattened
     * hierarchy from the back-end and filter the wanted results from the list.
     *
     * @param parent
     *            the parent item for the fetch
     * @param range
     *            the requested item range
     * @return the stream of items
     */
    public Stream<T> fetchItems(T parent, Range range) {
        // TODO: 27/11/2017 perhaps optimize as well
        return getHierarchy(parent, false).skip(range.getStart())
                .limit(range.length());
    }

    /**
     * Returns {@code limit} number of items that are after {@code item} in the
     * flattened hierarchy.
     * @param item
     * @param limit
     * @return
     */
    private List<T> fetchItemsAfter(T item, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }

        List<T> items = new ArrayList<>(limit);

        int skipChildren = 0;
        boolean needsFetchingAncestors = false;

        while (limit > 0 && item != null) {
            if (needsFetchingAncestors) {
                skipChildren = getSiblingIndex(item) + 1;
                item = getParentOfItem(item);
            }

            List<T> fetchedItems = fetchChildrenRecursively(item, false,
                    skipChildren, limit);
            items.addAll(fetchedItems);
            limit -= fetchedItems.size();

            needsFetchingAncestors = true;
        }

        return items;
    }

    /**
     * Fetches children of {@code parent} recursively.
     * @param parent
     * @param includeParent
     * @param skipChildren
     * @param limit
     * @return
     */
    private List<T> fetchChildrenRecursively(T parent, boolean includeParent,
            int skipChildren, int limit) {
        List<T> items = new ArrayList<>(limit);
        if (includeParent && limit-- > 0) {
            items.add(parent);
        }

        if (limit > 0 && isExpanded(parent)) {
            Iterator<T> children = getDirectChildren(parent,
                    Range.withLength(skipChildren, limit)).iterator();
            while (children.hasNext() && limit > 0) {
                List<T> childrenRecursive = fetchChildrenRecursively(
                        children.next(), true, 0, limit);
                items.addAll(childrenRecursive);
                limit -= childrenRecursive.size();
            }
        }
        return items;
    }

    /**
     * Fetches {@code limit} number of items that are before {@code item} in the
     * flattened hierarchy.
     * @param item
     * @param limit
     * @return
     */
    private List<T> fetchItemsBefore(T item, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }

        List<T> items = new ArrayList<>(limit);
        while (limit > 0 && item != null) {
            int siblingIndex = getSiblingIndex(item);
            item = getParentOfItem(item);
            List<T> fetchedItems = fetchChildrenRecursivelyReverse(item, true,
                    doGetChildCount(item) - siblingIndex, limit);
            items.addAll(fetchedItems);
            limit -= fetchedItems.size();
        }

        Collections.reverse(items);
        return items;
    }

    /**
     * Fetches children of {@code parent} recursively in reverse order.
     * @param parent
     * @param includeParent
     * @param offset
     * @param limit
     * @return
     */
    private List<T> fetchChildrenRecursivelyReverse(T parent,
            boolean includeParent, int offset, int limit) {
        List<T> items = new ArrayList<>(limit);

        if (limit > 0 && isExpanded(parent)) {

            int childCount = doGetChildCount(parent);
            List<T> children = getDirectChildren(parent,
                    Range.between(Math.max(0, childCount - offset - limit),
                            childCount - offset));

            for (int i = children.size() - 1; i >= 0 && limit > 0; i--) {
                List<T> childrenRecursive = fetchChildrenRecursivelyReverse(
                        children.get(i), true, 0, limit);
                items.addAll(childrenRecursive);
                limit -= childrenRecursive.size();
            }
        }

        if (limit > 0) {
            items.add(parent);
        }

        return items;
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
                range.getStart(), range.length(), getBackEndSorting(),
                getInMemorySorting(), getFilter(), parent));
    }

    private int getDepth(T item) {
        int depth = -1;
        while (item != null) {
            item = getParentOfItem(item);
            ++depth;
        }
        return depth;
    }

    /**
     * Find parent for the given item among open folders.
     * @param item the item
     * @return parent item or {@code null} for root items or if the parent is closed
     */
    protected T getParentOfItem(T item) {
        Objects.requireNonNull(item, "Can not find the parent of null");
        return parentMap.get(getDataProvider().getId(item));
    }

    /**
     * Removes all children of an item identified by a given id. Items removed
     * by this method as well as the original item are all marked to be
     * collapsed.
     * May be overridden in subclasses for removing obsolete data to avoid memory leaks.
     *
     * @param id
     *            the item id
     */
    protected void removeChildren(Object id) {
        // Clean up removed nodes from child map
        Iterator<Entry<T, Set<T>>> iterator = childMap.entrySet().iterator();
        Set<T> invalidatedChildren = new HashSet<>();
        while (iterator.hasNext()) {
            Entry<T, Set<T>> entry = iterator.next();
            T key = entry.getKey();
            if (key != null && getDataProvider().getId(key).equals(id)) {
                invalidatedChildren.addAll(entry.getValue());
                iterator.remove();
            }
        }
        expandedItemIds.remove(id);
        invalidatedChildren.stream().map(getDataProvider()::getId).forEach(x -> {
            removeChildren(x);
            parentMap.remove(x);
        });
    }

    /**
     * Finds the current index of given object. This is based on a search in
     * flattened version of the hierarchy.
     *
     * @param target
     *            the target object to find
     * @return optional index of given object
     */
    public Optional<Integer> getIndexOf(T target) {
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
        return getDirectChildren(parent,
                Range.between(0, doGetChildCount(parent))).stream();
    }

    private List<T> getDirectChildren(T parent, Range range) {
        List<T> items = doFetchDirectChildren(parent, range)
                .collect(Collectors.toList());

        // TODO: 22/11/2017 see if registering should be elsewhere
        // Keep index of sibling and parents for later use
        int index = range.getStart();
        for (T item : items) {
            Object id = getDataProvider().getId(item);
            siblingIndex.put(id, index++);
            parentMap.put(id, parent);
        }

        return items;
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
            childList = getDirectChildren(parent).collect(Collectors.toList());
            if (childList.isEmpty()) {
                removeChildren(parent == null ? null
                        : getDataProvider().getId(parent));
            } else {
                registerChildren(parent, childList);
            }
        }
        return combineParentAndChildStreams(parent,
                childList.stream().flatMap(this::getChildrenStream),
                includeParent);
    }

    private int doGetChildCount(T parent) {
        return getDataProvider()
                .getChildCount(new HierarchicalQuery<>(getFilter(), parent));
    }

    /**
     * Register parent and children items into inner structures.
     * May be overridden in subclasses.
     *
     * @param parent the parent item
     * @param childList list of parents children to be registered.
     */
    protected void registerChildren(T parent, List<T> childList) {
        childMap.put(parent, new HashSet<>(childList));
        childList.forEach(x -> parentMap.put(getDataProvider().getId(x), parent));
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

    private int getSiblingIndex(T item) {
        return siblingIndex.get(getDataProvider().getId(item));
    }

    private void setReferenceItem(T item, int index) {
        referenceItem = item;
        referenceItemIndex = index;
    }

    private void resetReferenceItem() {
        referenceItem = null;
        referenceItemIndex = -1;
    }

    private void shiftReferenceItem(int offset) {
        referenceItemIndex += offset;
    }

    @Override
    public void destroyAllData() {
        childMap.clear();
        parentMap.clear();
    }
}
