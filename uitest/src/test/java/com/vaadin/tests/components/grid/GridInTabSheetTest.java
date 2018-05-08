package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridInTabSheetTest extends MultiBrowserTest {

    @Test
    public void testRemoveAllRowsAndAddThreeNewOnes() {
        setDebug(true);
        openTestURL();

        for (int i = 0; i < 3; ++i) {
            removeGridRow();
        }

        for (int i = 0; i < 3; ++i) {
            addGridRow();
            assertEquals("" + (100 + i),
                    getGridElement().getCell(i, 1).getText());
        }

        assertNoNotification();
    }

    private void assertNoNotification() {
        assertFalse("There was an unexpected error notification",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void testAddManyRowsWhenGridIsHidden() {
        setDebug(true);
        openTestURL();

        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        for (int i = 0; i < 50; ++i) {
            addGridRow();
        }

        tabsheet.openTab("Grid");

        assertNoNotification();
    }

    @Test
    public void testAddCellStyleGeneratorWhenGridIsHidden() {
        setDebug(true);
        openTestURL();

        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        addCellStyleGenerator();

        tabsheet.openTab("Grid");

        assertNoNotification();
    }

    @Test
    public void testNoDataRequestFromClientWhenSwitchingTab() {
        setDebug(true);
        openTestURL();

        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        tabsheet.openTab("Grid");

        getLogs().forEach(logText -> assertTrue(
                "There should be no logged requests, was: " + logText,
                logText.trim().isEmpty()));
        assertNoNotification();
    }

    private void removeGridRow() {
        $(ButtonElement.class).caption("Remove row from Grid").first().click();
    }

    private void addGridRow() {
        $(ButtonElement.class).caption("Add row to Grid").first().click();
    }

    private void addCellStyleGenerator() {
        $(ButtonElement.class).caption("Add CellStyleGenerator").first()
                .click();
    }

    private GridElement getGridElement() {
        return $(GridElement.class).first();
    }
}
