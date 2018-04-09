package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that column keeps its header, icon, alignment after toggling visibility
 * (#6245, #12303).
 *
 * @author Vaadin Ltd
 */
public class TableToggleColumnVisibilityTest extends MultiBrowserTest {

    @Test
    public void testColumnWidthRestoredAfterTogglingVisibility() {
        openTestURL();

        ButtonElement toggleVisibilityButton = $(ButtonElement.class)
                .id("visib-toggler");
        ButtonElement changeOrderButton = $(ButtonElement.class)
                .id("order-toggler");

        checkHeaderAttributes(1);

        toggleVisibilityButton.click(); // hide column #1
        assertEquals("One column should be visible",
                findElements(By.className("v-table-header-cell")).size(), 1);

        toggleVisibilityButton.click(); // restore column #1
        assertEquals("Two columns should be visible",
                findElements(By.className("v-table-header-cell")).size(), 2);
        checkHeaderAttributes(1);

        // change column order, column #1 now becomes column #0
        changeOrderButton.click();
        checkHeaderAttributes(0);
    }

    /*
     * Checks column header with number columnNumber.
     */
    private void checkHeaderAttributes(int columnNumber) {
        TableHeaderElement headerCell = $(TableElement.class).first()
                .getHeaderCell(columnNumber);

        assertTrue("Column header text should be custom",
                headerCell.getText().equalsIgnoreCase("Hello World"));

        assertFalse("Column should have an icon",
                headerCell.findElements(By.className("v-icon")).isEmpty());

        assertFalse("Column should have alignment to the right", headerCell
                .findElements(
                        By.className("v-table-caption-container-align-right"))
                .isEmpty());
    }
}
