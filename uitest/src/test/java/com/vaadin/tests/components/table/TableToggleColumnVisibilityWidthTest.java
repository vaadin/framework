package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that column keeps its width after it is made invisible and visible
 * again (#12303).
 *
 * @author Vaadin Ltd
 */
public class TableToggleColumnVisibilityWidthTest extends MultiBrowserTest {

    @Test
    public void testColumnWidthRestoredAfterTogglingVisibility() {
        openTestURL();

        int secondColumnWidthInitial = findElements(
                By.className("v-table-header-cell")).get(1).getSize()
                        .getWidth();
        ButtonElement toggleButton = $(ButtonElement.class).id("toggler");

        toggleButton.click();
        assertEquals("One column should be visible",
                findElements(By.className("v-table-header-cell")).size(), 1);

        toggleButton.click();
        assertEquals("Two columns should be visible",
                findElements(By.className("v-table-header-cell")).size(), 2);
        int secondColumnWidthRestored = findElements(
                By.className("v-table-header-cell")).get(1).getSize()
                        .getWidth();
        assertEquals("Column width should be the same as it was before hiding",
                secondColumnWidthInitial, secondColumnWidthRestored);

    }

}
