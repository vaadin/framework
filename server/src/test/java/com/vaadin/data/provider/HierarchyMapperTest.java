package com.vaadin.data.provider;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.provider.HierarchyMapper.TreeNode;

public class HierarchyMapperTest {

    private HierarchyMapper mapper;

    @Before
    public void setup() {
        mapper = new HierarchyMapper();
    }

    @Test
    public void testExpandCollapse_rootLevel_indexesUpdated() {
        mapper.reset(3);
        verifyRootLevel(0, 2);

        mapper.expand("1", 1, 3);

        verifyTreeTotalSize(6);
        verifyRootLevel(0, 5);
        verifyNodeExists("1", 2, 4);

        mapper.expand("0", 0, 3);

        verifyRootLevel(0, 8);
        verifyNodeExists("0", 1, 3);
        verifyNodeExists("1", 5, 7);
        verifyTreeTotalSize(9);

        mapper.collapse("0", 0);

        verifyRootLevel(0, 5);
        verifyNodeExists("1", 2, 4);
        verifyTreeTotalSize(6);
        verifyNoNodeExists("0");
    }

    @Test
    public void testExpandCollapse_secondLevelLastNode_indexesUpdated() {
        mapper.reset(3);
        verifyRootLevel(0, 2);

        mapper.expand("1", 1, 3);

        verifyTreeTotalSize(6);
        verifyRootLevel(0, 5);
        verifyNodeExists("1", 2, 4);

        mapper.expand("0", 0, 3);

        verifyRootLevel(0, 8);
        verifyNodeExists("0", 1, 3);
        verifyNodeExists("1", 5, 7);
        verifyTreeTotalSize(9);

        mapper.expand("2", 3, 3);

        verifyRootLevel(0, 11);
        verifyNodeExists("0", 1, 6);
        verifyNodeExists("1", 8, 10);
        verifyNodeExists("2", 4, 6);
        verifyTreeTotalSize(12);

        mapper.collapse("2", 3);

        verifyRootLevel(0, 8);
        verifyNodeExists("0", 1, 3);
        verifyNodeExists("1", 5, 7);
        verifyNoNodeExists("2");
        verifyTreeTotalSize(9);

        mapper.collapse("0", 0);

        verifyRootLevel(0, 5);
        verifyNodeExists("1", 2, 4);
        verifyNoNodeExists("0");
        verifyTreeTotalSize(6);
    }

    @Test
    public void testCollapse_multipleLevels_wholeSubtreeDropped() {
        // expand hierarchy up to 3 level
        mapper.reset(5);
        verifyRootLevel(0, 4);

        mapper.expand("1", 2, 2);

        verifyRootLevel(0, 6);
        verifyNodeExists("1", 3, 4);
        verifyTreeTotalSize(7);

        mapper.expand("2", 3, 2);

        verifyRootLevel(0, 8);
        verifyNodeExists("1", 3, 6);
        verifyNodeExists("2", 4, 5);
        verifyTreeTotalSize(9);

        mapper.expand("3", 6, 2);
        verifyRootLevel(0, 10);
        verifyNodeExists("1", 3, 8);
        verifyNodeExists("2", 4, 5);
        verifyNodeExists("3", 7, 8);
        verifyTreeTotalSize(11);

        // collapse root level node
        mapper.collapse("1", 2);
        verifyRootLevel(0, 4);
        verifyNoNodeExists("1", "2", "3");
    }

    private void verifyRootLevel(int start, int end) {
        verifyNode(start, end, mapper.getNodeForKey(null).get());
    }

    private void verifyNodeExists(String key, int start, int end) {
        Optional<TreeNode> node = mapper.getNodeForKey(key);
        Assert.assertTrue("NO NODE FOUND FOR KEY: " + key, node.isPresent());
        verifyNode(start, end, node.get());
    }

    private void verifyNoNodeExists(String... nodeKeys) {
        for (String key : nodeKeys) {
            Assert.assertFalse("No node should exist for key " + key,
                    mapper.getNodeForKey(key).isPresent());
        }
    }

    private void verifyNode(int start, int end, TreeNode node) {
        Assert.assertEquals("Invalid start for node " + node, start,
                node.getStartIndex());
        Assert.assertEquals("Invalid end for node " + node, end,
                node.getEndIndex());
    }

    private void verifyTreeTotalSize(int size) {
        Assert.assertEquals("Invalid tree size", size, mapper.getTreeSize());
    }
}
