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

        mapper.collapse(0);

        verifyRoot(0, 5);
        verifyNode("1", 2, 4);
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

        mapper.expand("2", 3, 3);

        verifyRoot(0, 11);
        verifyNode("0", 1, 6);
        verifyNode("1", 8, 10);
        verifyNode("2", 4, 6);

        mapper.collapse(3);

        verifyRoot(0, 8);
        verifyNode("0", 1, 3);
        verifyNode("1", 5, 7);

        mapper.collapse(0);

        verifyRoot(0, 5);
        verifyNode("1", 2, 4);
    }

    private void verifyRoot(int start, int end) {
        verifyNode(start, end, mapper.getNodeForKey(null).get());
    }

    private void verifyNode(String key, int start, int end) {
        Optional<TreeNode> node = mapper.getNodeForKey(key);
        Assert.assertTrue("NO NODE FOUND FOR KEY: " + key, node.isPresent());
        verifyNode(start, end, node.get());
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
