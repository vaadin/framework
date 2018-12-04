package com.vaadin.tests.elements;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.elements.TreeElement;

public class TreeElementGetValueTest extends MultiBrowserTest {
    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testGetValue() {
        TreeElement tree = $(TreeElement.class).get(0);
        assertEquals(tree.getValue(), TreeElementGetValue.TEST_VALUE_LVL2);
    }
}
