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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Mapper for hierarchical data.
 * <p>
 * Keeps track of the expanded nodes, and size of of the subtrees for each
 * expanded node.
 * <p>
 * This class is considered framework internal implementation details, and can
 * be changed / moved at any point. This means that you should not directly use
 * this for anything.
 *
 * @author Vaadin Ltd
 * @since
 */
public class HierarchyMapper implements Serializable {

    private static final Logger LOGGER = Logger
            .getLogger(HierarchyMapper.class.getName());

    /**
     * A POJO that represents a query data for a certain tree level.
     */
    protected static class TreeLevelQuery {
        /**
         * The tree node that the query is for. Only used for fetching parent
         * key.
         */
        final TreeNode node;
        /** The start index of the query, from 0 to level's size - 1. */
        final int startIndex;
        /** The number of rows to fetch. s */
        final int size;
        /** The depth of this node. */
        final int depth;
        /** The first row index in grid, including all the nodes. */
        final int firstRowIndex;
        /** The direct subtrees for the node that effect the indexing. */
        final List<TreeNode> subTrees;

        TreeLevelQuery(TreeNode node, int startIndex, int size, int depth,
                int firstRowIndex, List<TreeNode> subTrees) {
            this.node = node;
            this.startIndex = startIndex;
            this.size = size;
            this.depth = depth;
            this.firstRowIndex = firstRowIndex;
            this.subTrees = subTrees;
        }
    }

    /**
     * A level in the tree, either the root level or an expanded subtree level.
     * <p>
     * Comparable based on the {@link #startIndex}, which is flat from 0 to data
     * size - 1.
     */
    protected static class TreeNode
            implements Serializable, Comparable<TreeNode> {

        /** The key for the expanded item that this is a subtree of. */
        private final String parentKey;
        /** The first index on this level. */
        private int startIndex;
        /** The last index on this level, INCLUDING subtrees. */
        private int endIndex;

        TreeNode(String parentKey, int startIndex, int size) {
            this.parentKey = parentKey;
            this.startIndex = startIndex;
            endIndex = startIndex + size - 1;
        }

        TreeNode(int startIndex) {
            parentKey = "INVALID";
            this.startIndex = startIndex;
        }

        int getStartIndex() {
            return startIndex;
        }

        int getEndIndex() {
            return endIndex;
        }

        String getParentKey() {
            return parentKey;
        }

        private void push(int offset) {
            startIndex += offset;
            endIndex += offset;
        }

        private void pushEnd(int offset) {
            endIndex += offset;
        }

        @Override
        public int compareTo(TreeNode other) {
            return Integer.valueOf(startIndex).compareTo(other.startIndex);
        }

        @Override
        public String toString() {
            return "TreeNode [parent=" + parentKey + ", start=" + startIndex
                    + ", end=" + getEndIndex() + "]";
        }

    }

    /** The expanded nodes in the tree. */
    private final TreeSet<TreeNode> nodes = new TreeSet<>();

    /**
     * Resets the tree, sets given the root level size.
     *
     * @param rootLevelSize
     *            the number of items in the root level
     */
    public void reset(int rootLevelSize) {
        nodes.clear();
        nodes.add(new TreeNode(null, 0, rootLevelSize));
    }

    /**
     * Returns the complete size of the tree, including all expanded subtrees.
     *
     * @return the size of the tree
     */
    public int getTreeSize() {
        TreeNode rootNode = getNodeForKey(null)
                .orElse(new TreeNode(null, 0, 0));
        return rootNode.endIndex + 1;
    }

    /**
     * Returns whether the node with the given is collapsed or not.
     *
     * @param itemKey
     *            the key of node to check
     * @return {@code true} if collapsed, {@code false} if expanded
     */
    public boolean isCollapsed(String itemKey) {
        return !getNodeForKey(itemKey).isPresent();
    }

    /**
     * Return the depth of expanded node's subtree.
     * <p>
     * The root node depth is 0.
     *
     * @param expandedNodeKey
     *            the item key of the expanded node
     * @return the depth of the expanded node
     * @throws IllegalArgumentException
     *             if the node was not expanded
     */
    protected int getDepth(String expandedNodeKey) {
        Optional<TreeNode> node = getNodeForKey(expandedNodeKey);
        if (!node.isPresent()) {
            throw new IllegalArgumentException("No node with given key "
                    + expandedNodeKey + " was expanded.");
        }
        TreeNode treeNode = node.get();
        AtomicInteger start = new AtomicInteger(treeNode.startIndex);
        AtomicInteger end = new AtomicInteger(treeNode.getEndIndex());
        AtomicInteger depth = new AtomicInteger();
        nodes.headSet(treeNode, false).descendingSet().forEach(higherNode -> {
            if (higherNode.startIndex < start.get()
                    && higherNode.getEndIndex() >= end.get()) {
                start.set(higherNode.startIndex);
                depth.incrementAndGet();
            }
        });

        return depth.get();
    }

    /**
     * Returns the tree node for the given expanded item key, or an empty
     * optional if the item was not expanded.
     *
     * @param expandedNodeKey
     *            the key of the item
     * @return the tree node for the expanded item, or an empty optional if not
     *         expanded
     */
    protected Optional<TreeNode> getNodeForKey(String expandedNodeKey) {
        return nodes.stream()
                .filter(node -> Objects.equals(node.parentKey, expandedNodeKey))
                .findAny();
    }

    /**
     * Expands the node in the given index and with the given key.
     *
     * @param expanedRowKey
     *            the key of the expanded item
     * @param expandedRowIndex
     *            the index of the expanded item
     * @param expandedNodeSize
     *            the size of the subtree of the expanded node
     * @throws IllegalStateException
     *             if the node was expanded already
     */
    protected void expand(String expanedRowKey, int expandedRowIndex,
            int expandedNodeSize) {
        if (expandedNodeSize < 1) {
            throw new IllegalArgumentException(
                    "The expanded node's size cannot be less than 1, was "
                            + expandedNodeSize);
        }
        TreeNode newNode = new TreeNode(expanedRowKey, expandedRowIndex + 1,
                expandedNodeSize);

        boolean added = nodes.add(newNode);
        if (!added) {
            throw new IllegalStateException("Node in index " + expandedRowIndex
                    + " was expanded already.");
        }

        // push end indexes for parent nodes
        List<TreeNode> updated = nodes.headSet(newNode, false).stream()
                .filter(node -> node.getEndIndex() >= expandedRowIndex)
                .collect(Collectors.toList());
        nodes.removeAll(updated);
        updated.stream().forEach(node -> node.pushEnd(expandedNodeSize));
        nodes.addAll(updated);

        // push start and end indexes for later nodes
        updated = nodes.tailSet(newNode, false).stream()
                .collect(Collectors.toList());
        nodes.removeAll(updated);
        updated.stream().forEach(node -> node.push(expandedNodeSize));
        nodes.addAll(updated);
    }

    /**
     * Collapses the node in the given index.
     *
     * @param key
     *            the key of the collapsed item
     * @param collapsedRowIndex
     *            the index of the collapsed item
     * @return the size of the complete subtree that was collapsed
     * @throws IllegalStateException
     *             if the node was not collapsed, or if the given key is not the
     *             same as it was when the node has been expanded
     */
    protected int collapse(String key, int collapsedRowIndex) {
        Objects.requireNonNull(key,
                "The key for the item to collapse cannot be null.");
        TreeNode collapsedNode = nodes
                .ceiling(new TreeNode(collapsedRowIndex + 1));
        if (collapsedNode == null
                || collapsedNode.startIndex != collapsedRowIndex + 1) {
            throw new IllegalStateException(
                    "Could not find expanded node for index "
                            + collapsedRowIndex + ", node was not collapsed");
        }
        if (!Objects.equals(key, collapsedNode.parentKey)) {
            throw new IllegalStateException("The expected parent key " + key
                    + " is different for the collapsed node " + collapsedNode);
        }

        // remove complete subtree
        AtomicInteger removedSubTreeSize = new AtomicInteger(
                collapsedNode.getEndIndex() - collapsedNode.startIndex + 1);
        nodes.tailSet(collapsedNode, false).removeIf(
                node -> node.startIndex <= collapsedNode.getEndIndex());

        final int offset = -1 * removedSubTreeSize.get();
        // adjust parent end indexes
        List<TreeNode> updated = nodes.headSet(collapsedNode, false).stream()
                .filter(node -> node.getEndIndex() >= collapsedRowIndex)
                .collect(Collectors.toList());
        nodes.removeAll(updated);
        updated.stream().forEach(node -> node.pushEnd(offset));
        nodes.addAll(updated);

        // adjust start and end indexes for latter nodes
        updated = nodes.tailSet(collapsedNode, false).stream()
                .collect(Collectors.toList());
        nodes.removeAll(updated);
        updated.stream().forEach(node -> node.push(offset));
        nodes.addAll(updated);

        nodes.remove(collapsedNode);

        return removedSubTreeSize.get();
    }

    /**
     * Splits the given range into queries per tree level.
     *
     * @param firstRow
     *            the first row to fetch
     * @param lastRow
     *            the last row to fetch
     * @return a stream of query data per level
     * @see #reorderLevelQueryResultsToFlatOrdering(BiConsumer, TreeLevelQuery,
     *      List)
     */
    protected Stream<TreeLevelQuery> splitRangeToLevelQueries(
            final int firstRow, final int lastRow) {
        return nodes.stream()
                // filter to parts intersecting with the range
                .filter(node -> node.startIndex <= lastRow
                        && firstRow <= node.getEndIndex())
                // split into queries per level with level based indexing
                .map(node -> {

                    // calculate how subtrees effect indexing and size
                    int depth = getDepth(node.parentKey);
                    List<TreeNode> directSubTrees = nodes.tailSet(node, false)
                            .stream()
                            // find subtrees
                            .filter(subTree -> node.startIndex < subTree
                                    .getEndIndex()
                                    && subTree.startIndex < node.getEndIndex())
                            // filter to direct subtrees
                            .filter(subTree -> getDepth(
                                    subTree.parentKey) == (depth + 1))
                            .collect(Collectors.toList());
                    // index in flat order
                    AtomicInteger firstIntersectingRowIndex = new AtomicInteger(
                            Math.max(node.startIndex, firstRow));
                    // index for this node
                    AtomicInteger start;
                    int end = Math.min(node.getEndIndex(), lastRow);
                    AtomicInteger size;
                    List<TreeNode> intersectingSubTrees = new ArrayList<>();
                    start = new AtomicInteger(
                            firstIntersectingRowIndex.get() - node.startIndex);

                    // reduce subtrees before requested index
                    directSubTrees.stream().filter(subtree -> subtree
                            .getEndIndex() < firstIntersectingRowIndex.get())
                            .forEachOrdered(subtree -> {
                                start.addAndGet(-1 * (subtree.getEndIndex()
                                        - subtree.startIndex + 1));
                            });
                    size = new AtomicInteger(
                            end - firstIntersectingRowIndex.get() + 1);
                    // if requested start index is in the middle of a
                    // subtree, start is after that
                    directSubTrees.stream()
                            .filter(subtree -> subtree.startIndex <= firstIntersectingRowIndex
                                    .get() && firstIntersectingRowIndex
                                            .get() <= subtree.getEndIndex())
                            .findFirst().ifPresent(subtree -> {
                                int previous = firstIntersectingRowIndex
                                        .getAndSet(subtree.getEndIndex() + 1);
                                int delta = previous
                                        - firstIntersectingRowIndex.get();
                                start.addAndGet(subtree.startIndex - previous);
                                size.addAndGet(delta);
                                intersectingSubTrees.add(subtree);
                            });

                    directSubTrees.stream()
                            .filter(subtree -> firstIntersectingRowIndex
                                    .get() < subtree.startIndex)
                            .forEachOrdered(subtree -> {
                                // reduce subtree size from size
                                size.addAndGet(-1 * (subtree.getEndIndex()
                                        - subtree.startIndex + 1));
                                intersectingSubTrees.add(subtree);
                            });
                    return new TreeLevelQuery(node, start.get(), size.get(),
                            depth, firstIntersectingRowIndex.get(),
                            intersectingSubTrees);

                }).filter(query -> query.size > 0);

    }

    /**
     * Merges the tree level query results into flat grid ordering.
     *
     * @param rangePositionCallback
     *            the callback to place the results into
     * @param query
     *            the query data for the results
     * @param results
     *            the results to reorder
     * @param <T>
     *            the type of the results
     */
    protected <T> void reorderLevelQueryResultsToFlatOrdering(
            BiConsumer<T, Integer> rangePositionCallback, TreeLevelQuery query,
            List<T> results) {
        AtomicInteger nextPossibleIndex = new AtomicInteger(
                query.firstRowIndex);
        for (T item : results) {
            // search for any intersecting subtrees and push index if necessary
            query.subTrees.stream().filter(
                    subTree -> subTree.startIndex <= nextPossibleIndex.get()
                            && nextPossibleIndex.get() <= subTree.getEndIndex())
                    .findAny().ifPresent(intersecting -> {
                        nextPossibleIndex.addAndGet(intersecting.getEndIndex()
                                - intersecting.startIndex + 1);
                        query.subTrees.remove(intersecting);
                    });
            rangePositionCallback.accept(item,
                    nextPossibleIndex.getAndIncrement());
        }
    }

}
