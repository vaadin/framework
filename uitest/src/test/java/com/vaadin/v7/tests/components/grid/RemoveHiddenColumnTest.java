package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class RemoveHiddenColumnTest extends SingleBrowserTest {

    @Test
    public void removeHiddenColumnInEmptyGrid() {
        openTestURL("debug");
        removeColumns();
    }

    @Test
    public void removeHiddenColumnInPopulatedGrid() {
        openTestURL("debug");
        ButtonElement add = $(ButtonElement.class).id("add");
        add.click();
        removeColumns();

    }

    private void removeColumns() {
        ButtonElement remove = $(ButtonElement.class).id("remove");
        remove.click();
        assertEquals("1. Removed column 'First Name' (hidden)", getLogRow(0));
        assertNoErrorNotifications();

        remove.click();
        assertEquals("2. Removed column 'Last Name'", getLogRow(0));
        assertNoErrorNotifications();
        remove.click();
        assertEquals("3. Removed column 'Email' (hidden)", getLogRow(0));
        assertNoErrorNotifications();
        remove.click();
        assertEquals("4. Removed column 'Age'", getLogRow(0));
        assertNoErrorNotifications();

    }

}
