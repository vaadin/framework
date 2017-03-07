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
 *
 * @author Vaadin Ltd
 * @since
 */
public class HierarchyMapper implements Serializable {

    private static final Logger LOGGER = Logger
            .getLogger(HierarchyMapper.class.getName());

    public static class TreeLevelQuery implements Serializable {
        final TreeNode node;
        final int startIndex;
        final int size;
        final int depth;
        final int firstRowIndex;
        final List<TreeNode> subTrees;

        public TreeLevelQuery(TreeNode node, int startIndex, int size,
                int depth, int firstRowIndex, List<TreeNode> subTrees) {
            this.node = node;
            this.startIndex = startIndex;
            this.size = size;
            this.depth = depth;
            this.firstRowIndex = firstRowIndex;
            this.subTrees = subTrees;
        }
    }

    public static class TreeNode implements Serializable, Comparable<TreeNode> {

        private final String parentKey;
        private int startIndex;
        private int size;
        private int endIndex;

        public TreeNode(String parentKey, int startIndex, int size) {
            this.parentKey = parentKey;
            this.startIndex = startIndex;
            endIndex = startIndex + size - 1;
            this.size = size;
        }

        public TreeNode(int startIndex) {
            parentKey = "INVALID";
            this.startIndex = startIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public String getParentKey() {
            return parentKey;
        }

        public void push(int offset) {
            startIndex += offset;
            endIndex += offset;
        }

        public void pushEnd(int offset) {
            endIndex += offset;
        }

        @Override
        public int compareTo(TreeNode other) {
            return Integer.valueOf(startIndex).compareTo(other.startIndex);
        }

        @Override
        public String toString() {
            return "TreeNode [parent=" + parentKey + ", start=" + startIndex
                    + ", end=" + getEndIndex() + ", size=" + size + "]";
        }

    }

    private final TreeSet<TreeNode> nodes = new TreeSet<>();

    public void reset(int rootDepthSize) {
        nodes.clear();
        nodes.add(new TreeNode(null, 0, rootDepthSize));
    }

    public int getTreeSize() {
        AtomicInteger count = new AtomicInteger(0);
        nodes.forEach(node -> count.getAndAdd(node.size));
        return count.get();
    }

    public boolean isCollapsed(String itemKey) {
        return !getNodeForKey(itemKey).isPresent();
    }

    public int getDepth(String expandedNodeKey) {
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

    protected Optional<TreeNode> getNodeForKey(String expandedNodeKey) {
        return nodes.stream()
                .filter(node -> Objects.equals(node.parentKey, expandedNodeKey))
                .findAny();
    }

    public void expand(String expanedRowKey, int expandedRowIndex,
            int expandedNodeSize) {
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
        System.out.println("PUSHED END INDEXES " + expandedNodeSize + " FOR "
                + updated.stream().map(TreeNode::toString)
                        .collect(Collectors.joining(",")));
        nodes.addAll(updated);

        // push start and end indexes for later nodes
        updated = nodes.tailSet(newNode, false).stream()
                .collect(Collectors.toList());
        nodes.removeAll(updated);
        updated.stream().forEach(node -> node.push(expandedNodeSize));
        System.out.println("PUSHED BOTH INDEXES " + expandedNodeSize + " FOR "
                + updated.stream().map(TreeNode::toString)
                        .collect(Collectors.joining(",")));
        nodes.addAll(updated);
    }

    public int collapse(int collapsedRowIndex) {
        TreeNode collapsedNode = nodes
                .ceiling(new TreeNode(collapsedRowIndex + 1));
        if (collapsedNode == null
                || collapsedNode.startIndex != collapsedRowIndex + 1) {
            throw new IllegalArgumentException(
                    "Could not find expanded node for index "
                            + collapsedRowIndex + ", node was not collapsed");
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
        System.out.println("PUSHED END INDEXES " + removedSubTreeSize.get()
                + " FOR " + updated.stream().map(TreeNode::toString)
                        .collect(Collectors.joining(",")));
        nodes.addAll(updated);

        // adjust start and end indexes for latter nodes
        updated = nodes.tailSet(collapsedNode, false).stream()
                .collect(Collectors.toList());
        nodes.removeAll(updated);
        updated.stream().forEach(node -> node.push(offset));
        System.out.println("PUSHED BOTH INDEXES " + removedSubTreeSize.get()
                + " FOR " + updated.stream().map(TreeNode::toString)
                        .collect(Collectors.joining(",")));
        nodes.addAll(updated);

        nodes.remove(collapsedNode);

        return removedSubTreeSize.get();
    }

    public Stream<TreeLevelQuery> splitRangeToLevelQueries(final int firstRow,
            final int lastRow) {
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
                    System.out.println("requested: " + firstRow + "-" + lastRow
                            + ", for " + node);
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
                    System.out.println("FINAL for node:" + node.startIndex + "-"
                            + end + ", start: " + start.get() + " size:"
                            + size.get());
                    return new TreeLevelQuery(node, start.get(), size.get(),
                            depth, firstIntersectingRowIndex.get(),
                            intersectingSubTrees);

                }).filter(query -> query.size > 0);

    }

    public <T> void mergeLevelQueryResultIntoRange(
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
