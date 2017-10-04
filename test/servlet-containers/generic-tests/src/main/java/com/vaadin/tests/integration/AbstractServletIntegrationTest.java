package com.vaadin.tests.integration;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;

public abstract class AbstractServletIntegrationTest
        extends AbstractIntegrationTest {

    @Test
    public void runTest() throws Exception {
        // Test initial state
        GridElement grid = $(GridElement.class).first();
        Assert.assertFalse("Row should not be initially selected",
                grid.getRow(0).isSelected());
        compareScreen("initial");

        // Test selection and side effects
        grid.getCell(0, 1).click();
        Assert.assertTrue("Row should be selected on click",
                grid.getRow(0).isSelected());
        Assert.assertEquals("Text label should contain 'FI'", "FI",
                $(LabelElement.class).first().getText());
        compareScreen("finland");

    }

}
