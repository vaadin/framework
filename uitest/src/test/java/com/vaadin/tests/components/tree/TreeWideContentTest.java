package com.vaadin.tests.components.tree;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeWideContentTest extends SingleBrowserTest {

    @Test
    public void testInitialSize() {
        openTestURL();

        TreeGridElement tree = $(TreeElement.class).first()
                .wrap(TreeGridElement.class);
        Assert.assertTrue("Row should be wider than tree",
                tree.getTableWrapper().getSize().getWidth() < tree.getRow(0)
                        .getSize().getWidth());
    }

    @Test
    public void testSizeAfterCollapse() {
        openTestURL();

        TreeElement tree = $(TreeElement.class).first();
        tree.collapse(0);
        TreeGridElement treeGrid = tree.wrap(TreeGridElement.class);
        Assert.assertTrue("Row should be as wide as tree",
                treeGrid.getTableWrapper().getSize().getWidth() == treeGrid
                        .getRow(0).getSize().getWidth());
    }

    @Test
    public void testSizeWithAutoRecalcDisabled() {
        openTestURL();
        // Disable auto recalc
        $(ButtonElement.class).first().click();

        TreeElement tree = $(TreeElement.class).first();
        TreeGridElement treeGrid = tree.wrap(TreeGridElement.class);
        Assert.assertTrue("Row should be as wide as tree",
                treeGrid.getTableWrapper().getSize().getWidth() == treeGrid
                        .getRow(0).getSize().getWidth());
    }
}
