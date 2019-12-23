package com.vaadin.tests.components.tree;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.TreeElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeInitiallyDisabledTest extends SingleBrowserTest {

    @Test
    public void checkDisabledStyleAdded() {
        openTestURL();
        TreeElement tree = $(TreeElement.class).first();
        assertTrue(tree.hasClassName("v-disabled"));
    }
}
