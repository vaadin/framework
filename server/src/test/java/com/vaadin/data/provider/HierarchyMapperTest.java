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
    public void testCollapseExpandRootLevel() {
        mapper.reset(3);
        verifyRoot(0, 2);

        mapper.expand("1", 1, 3);

        verifySize(6);
        verifyRoot(0, 5);
        verifyNode("1", 2, 4);

        mapper.expand("0", 0, 3);

        verifyRoot(0, 8);
        verifyNode("0", 1, 3);
        verifyNode("1", 5, 7);
        verifySize(9);

        mapper.collapse("0", 0);

        verifyRoot(0, 5);
        verifyNode("1", 2, 4);
        verifySize(6);
        verifyNoNodeExists("0");
    }

    @Test
    public void testCollapseExpandSecondLevelLastNode() {
        mapper.reset(3);
        verifyRoot(0, 2);

        mapper.expand("1", 1, 3);

        verifySize(6);
        verifyRoot(0, 5);
        verifyNode("1", 2, 4);

        mapper.expand("0", 0, 3);

        verifyRoot(0, 8);
        verifyNode("0", 1, 3);
        verifyNode("1", 5, 7);
        verifySize(9);

        mapper.expand("2", 3, 3);

        verifyRoot(0, 11);
        verifyNode("0", 1, 6);
        verifyNode("1", 8, 10);
        verifyNode("2", 4, 6);
        verifySize(12);

        mapper.collapse("2", 3);

        verifyRoot(0, 8);
        verifyNode("0", 1, 3);
        verifyNode("1", 5, 7);
        verifyNoNodeExists("2");
        verifySize(9);

        mapper.collapse("0", 0);

        verifyRoot(0, 5);
        verifyNode("1", 2, 4);
        verifyNoNodeExists("0");
        verifySize(6);
    }

    @Test
    public void testCollapseMultipleLevels() {
        mapper.reset(5);
        verifyRoot(0, 4);

        mapper.expand("1", 2, 2);

        verifyRoot(0, 6);
        verifyNode("1", 3, 4);
        verifySize(7);

        mapper.expand("2", 3, 2);

        verifyRoot(0, 8);
        verifyNode("1", 3, 6);
        verifyNode("2", 4, 5);
        verifySize(9);

        mapper.expand("3", 6, 2);
        verifyRoot(0, 10);
        verifyNode("1", 3, 8);
        verifyNode("2", 4, 5);
        verifyNode("3", 7, 8);
        verifySize(11);

        mapper.collapse("1", 2);
        verifyRoot(0, 4);
        verifyNoNodeExists("1", "2", "3");
    }

    private void verifyRoot(int start, int end) {
        verifyNode(start, end, mapper.getNodeForKey(null).get());
    }

    private void verifyNode(String key, int start, int end) {
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

    private void verifySize(int size) {
        Assert.assertEquals("Invalid tree size", size, mapper.getTreeSize());
    }
}
