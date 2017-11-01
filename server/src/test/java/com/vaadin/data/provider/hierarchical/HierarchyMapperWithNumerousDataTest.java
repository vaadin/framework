package com.vaadin.data.provider.hierarchical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchyMapper;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Range;

public class HierarchyMapperWithNumerousDataTest {

    private static final int ROOT_COUNT = 1;
    private static final int PARENT_COUNT = 100000;

    private static TreeData<Node> data = new TreeData<>();
    private TreeDataProvider<Node> provider;
    private HierarchyMapper<Node, SerializablePredicate<Node>> mapper;
    private static List<Node> testData;
    private static List<Node> roots;
    private int mapSize = ROOT_COUNT;

    @BeforeClass
    public static void setupData() {
        testData = HierarchyMapperWithDataTest.generateTestData(ROOT_COUNT,
                PARENT_COUNT, 0);
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

    /**
     * Test for non-logarithmic {@code getParentOfItem} implementations 100000
     * entries and 1 second should be enought to make it run even on slow
     * machines and weed out linear solutions
     */
    @Test(timeout = 1000)
    public void expandRootNode() {
        assertEquals("Map size should be equal to root node count", ROOT_COUNT,
                mapper.getTreeSize());
        expand(testData.get(0));
        assertEquals("Should be root count + once parent count",
                ROOT_COUNT + PARENT_COUNT, mapper.getTreeSize());
        checkMapSize();
    }

    private void expand(Node node) {
        insertRows(mapper.doExpand(node, mapper.getIndexOf(node)));
    }

    public void insertRows(Range range) {
        assertTrue("Index not in range",
                0 <= range.getStart() && range.getStart() <= mapSize);
        mapSize += range.length();
    }

    private void checkMapSize() {
        assertEquals("Map size not properly updated", mapper.getTreeSize(),
                mapSize);
    }
}
