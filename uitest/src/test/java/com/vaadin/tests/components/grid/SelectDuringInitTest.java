package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class SelectDuringInitTest extends SingleBrowserTest {

    @Test
    public void testSelectDuringInit() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        Assert.assertTrue(grid.getRow(1).isSelected());
    }

}
