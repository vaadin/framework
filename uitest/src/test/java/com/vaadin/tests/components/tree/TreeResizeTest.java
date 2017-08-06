package com.vaadin.tests.components.tree;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeResizeTest extends SingleBrowserTest {

    @Test
    public void testInitialSize() {
        openTestURL();

        TreeGridElement tree = $(TreeElement.class).first()
                .wrap(TreeGridElement.class);
        Assert.assertEquals("Row should be as wide as tree",
                tree.getTableWrapper().getSize().getWidth(),
                tree.getRow(0).getSize().getWidth());
    }

    @Test
    public void testSizeAfterResize() throws Exception {
        openTestURL();

        ButtonElement resizeButton = $(ButtonElement.class).first();
        TreeGridElement tree = $(TreeElement.class).first()
                .wrap(TreeGridElement.class);

        // Make tree narrower
        resizeButton.click();
        Thread.sleep(500);  // Wait for the resize to be triggered
        Assert.assertTrue("Row should be wider than tree",
                tree.getTableWrapper().getSize().getWidth() < tree.getRow(0)
                        .getSize().getWidth());

        // Make tree wider
        resizeButton.click();
        Thread.sleep(500);  // Wait for the resize to be triggered
        Assert.assertEquals("Row should be as wide as tree",
                tree.getTableWrapper().getSize().getWidth(),
                tree.getRow(0).getSize().getWidth());
    }
}
