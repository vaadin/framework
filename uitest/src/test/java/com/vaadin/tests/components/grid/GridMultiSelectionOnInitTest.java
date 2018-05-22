package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridMultiSelectionOnInitTest extends MultiBrowserTest {

    @Test
    public void testSelectAllCheckBoxExists() {
        openTestURL();
        assertTrue("The select all checkbox was missing.",
                $(GridElement.class).first().getHeaderCell(0, 0)
                        .isElementPresent(By.tagName("input")));
    }

    @Test
    public void testSetSelectedUpdatesClient() {
        openTestURL();
        assertFalse("Rows should not be selected initially.",
                $(GridElement.class).first().getRow(0).isSelected());
        $(ButtonElement.class).first().click();
        assertTrue("Rows should be selected after button click.",
                $(GridElement.class).first().getRow(0).isSelected());
    }
}
