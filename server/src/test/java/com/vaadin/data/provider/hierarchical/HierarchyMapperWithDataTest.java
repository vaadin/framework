package com.vaadin.data.provider.hierarchical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchyMapper;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Range;

public class HierarchyMapperWithDataTest {

    private static final int ROOT_COUNT = 5;
    private static final int PARENT_COUNT = 4;
    private static final int LEAF_COUNT = 2;

    private static TreeData<Node> data = new TreeData<>();
    private TreeDataProvider<Node> provider;
    private HierarchyMapper<Node, SerializablePredicate<Node>> mapper;
    private static List<Node> testData;
    private static List<Node> roots;
    private int mapSize = ROOT_COUNT;

    @BeforeClass
    public static void setupData() {
        testData = generateTestData(ROOT_COUNT, PARENT_COUNT, LEAF_COUNT);
        roots = testData.stream().filter(item -> item.getParent() == null)
                .collect(Collectors.toList());
        data.addItems(roots,
                parent -> testData.stream().filter(
                        item -> Objects.equals(item.getParent(), parent))
                        .collect(Collectors.toList()));
    }

    @Before
    public void setup() {
        provider = new TreeDataProvider<>(data);
        mapper = new HierarchyMapper<>(provider);
    }

    @Test
    public void expandRootNode() {
        assertEquals("Map size should be equal to root node count", ROOT_COUNT,
                mapper.getTreeSize());
        expand(testData.get(0));
        assertEquals("Should be root count + once parent count",
                ROOT_COUNT + PARENT_COUNT, mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void expandAndCollapseLastRootNode() {
        assertEquals("Map size should be equal to root node count", ROOT_COUNT,
                mapper.getTreeSize());
        expand(roots.get(roots.size() - 1));
        assertEquals("Should be root count + once parent count",
                ROOT_COUNT + PARENT_COUNT, mapper.getTreeSize());
        checkMapSize();
        collapse(roots.get(roots.size() - 1));
        assertEquals("Map size should be equal to root node count again",
                ROOT_COUNT, mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void expandHiddenNode() {
        assertEquals("Map size should be equal to root node count", ROOT_COUNT,
                mapper.getTreeSize());
        expand(testData.get(1));
        assertEquals("Map size should not change when expanding a hidden node",
                ROOT_COUNT, mapper.getTreeSize());
        checkMapSize();
        expand(roots.get(0));
        assertEquals("Hidden node should now be expanded as well",
                ROOT_COUNT + PARENT_COUNT + LEAF_COUNT, mapper.getTreeSize());
        checkMapSize();
        collapse(roots.get(0));
        assertEquals("Map size should be equal to root node count", ROOT_COUNT,
                mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void expandLeafNode() {
        assertEquals("Map size should be equal to root node count", ROOT_COUNT,
                mapper.getTreeSize());
        expand(testData.get(0));
        expand(testData.get(1));
        assertEquals("Root and parent node expanded",
                ROOT_COUNT + PARENT_COUNT + LEAF_COUNT, mapper.getTreeSize());
        checkMapSize();
        expand(testData.get(2));
        assertEquals("Expanding a leaf node should have no effect",
                ROOT_COUNT + PARENT_COUNT + LEAF_COUNT, mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void findParentIndexOfLeaf() {
        expand(testData.get(0));
        assertEquals("Could not find the root node of a parent",
                Integer.valueOf(0), mapper.getParentIndex(testData.get(1)));

        expand(testData.get(1));
        assertEquals("Could not find the parent of a leaf", Integer.valueOf(1),
                mapper.getParentIndex(testData.get(2)));
    }

    @Test
    public void fetchRangeOfRows() {
        expand(testData.get(0));
        expand(testData.get(1));

        List<Node> expectedResult = testData.stream()
                .filter(n -> roots.contains(n)
                        || n.getParent().equals(testData.get(0))
                        || n.getParent().equals(testData.get(1)))
                .collect(Collectors.toList());

        // Range containing deepest level of expanded nodes without their
        // parents in addition to root nodes at the end.
        Range range = Range.between(3, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);

        // Only the expanded two nodes, nothing more.
        range = Range.between(0, 2);
        verifyFetchIsCorrect(expectedResult, range);

        // Fetch everything
        range = Range.between(0, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
    }

    @Test
    public void fetchRangeOfRowsWithSorting() {
        // Expand before sort
        expand(testData.get(0));
        expand(testData.get(1));

        // Construct a sorted version of test data with correct filters
        List<List<Node>> levels = new ArrayList<>();
        Comparator<Node> comparator = Comparator.comparing(Node::getNumber)
                .reversed();
        levels.add(testData.stream().filter(n -> n.getParent() == null)
                .sorted(comparator).collect(Collectors.toList()));
        levels.add(
                testData.stream().filter(n -> n.getParent() == testData.get(0))
                        .sorted(comparator).collect(Collectors.toList()));
        levels.add(
                testData.stream().filter(n -> n.getParent() == testData.get(1))
                        .sorted(comparator).collect(Collectors.toList()));

        List<Node> expectedResult = levels.get(0).stream().flatMap(root -> {
            Stream<Node> nextLevel = levels.get(1).stream()
                    .filter(n -> n.getParent() == root)
                    .flatMap(node -> Stream.concat(Stream.of(node),
                            levels.get(2).stream()
                                    .filter(n -> n.getParent() == node)));
            return Stream.concat(Stream.of(root), nextLevel);
        }).collect(Collectors.toList());

        // Apply sorting
        mapper.setInMemorySorting(comparator::compare);

        // Range containing deepest level of expanded nodes without their
        // parents in addition to root nodes at the end.
        Range range = Range.between(8, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);

        // Only the root nodes, nothing more.
        range = Range.between(0, ROOT_COUNT);
        verifyFetchIsCorrect(expectedResult, range);

        // Fetch everything
        range = Range.between(0, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
    }

    @Test
    public void fetchWithFilter() {
        expand(testData.get(0));
        Node expandedNode = testData.get(2 + LEAF_COUNT); // Expand second node
        expand(expandedNode);

        SerializablePredicate<Node> filter = n -> n.getNumber() % 2 == 0;

        // Root nodes plus children of expanded nodes 0 and 4 that match the
        // filter
        List<Node> expectedResult = IntStream
            .of(0, 1, 4, 6, 7, 10, 13, 26, 39, 52).mapToObj(testData::get)
            .collect(Collectors.toList());

        mapper.setFilter(filter);

        // Fetch everything
        Range range = Range.between(0, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
    }

    private void expand(Node node) {
        insertRows(mapper.expand(node, mapper.getIndexOf(node).orElse(null)));
    }

    private void collapse(Node node) {
        removeRows(mapper.collapse(node, mapper.getIndexOf(node).orElse(null)));
    }

    private void verifyFetchIsCorrect(List<Node> expectedResult, Range range) {
        List<Node> collect = mapper.fetchItems(range)
                .collect(Collectors.toList());
        for (int i = 0; i < range.length(); ++i) {
            assertEquals("Unexpected fetch results.",
                    expectedResult.get(i + range.getStart()), collect.get(i));
        }
    }

    static List<Node> generateTestData(int rootCount, int parentCount,
            int leafCount) {
        int nodeCounter = 0;
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < rootCount; ++i) {
            Node root = new Node(nodeCounter++);
            nodes.add(root);
            for (int j = 0; j < parentCount; ++j) {
                Node parent = new Node(root, nodeCounter++);
                nodes.add(parent);
                for (int k = 0; k < leafCount; ++k) {
                    nodes.add(new Node(parent, nodeCounter++));
                }
            }
        }
        return nodes;
    }

    private void checkMapSize() {
        assertEquals("Map size not properly updated", mapper.getTreeSize(),
                mapSize);
    }

    public void removeRows(Range range) {
        assertTrue("Index not in range",
                0 <= range.getStart() && range.getStart() < mapSize);
        assertTrue("Removing more items than in map",
                range.getEnd() <= mapSize);
        mapSize -= range.length();
    }

    public void insertRows(Range range) {
        assertTrue("Index not in range",
                0 <= range.getStart() && range.getStart() <= mapSize);
        mapSize += range.length();
    }
}
