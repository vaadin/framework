package com.vaadin.tests.components.tree;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeOnBrowserResizeTest extends MultiBrowserTest {

    @Test
    public void testTreeSizeOnBrowserShrink() {
        openTestURL();

        int originalWidth = $(TreeGridElement.class).first().getTableWrapper()
                .getSize().getWidth();

        getDriver().manage().window().setSize(new Dimension(600, 800));

        assertTrue("Tree size should decrease.",
                originalWidth > $(TreeGridElement.class).first()
                        .getTableWrapper().getSize().getWidth());
    }

    @Test
    public void testTreeSizeOnBrowserEnlarge() {
        getDriver().manage().window().setSize(new Dimension(600, 800));
        openTestURL();

        int originalWidth = $(TreeGridElement.class).first().getTableWrapper()
                .getSize().getWidth();

        getDriver().manage().window().setSize(new Dimension(800, 800));

        assertTrue("Tree size should increase.",
                originalWidth < $(TreeGridElement.class).first()
                        .getTableWrapper().getSize().getWidth());
    }

}
