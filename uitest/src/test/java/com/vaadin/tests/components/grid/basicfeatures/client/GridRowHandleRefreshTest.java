package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridRowHandleRefreshTest extends GridBasicClientFeaturesTest {

    @Test
    public void testRefreshingThroughRowHandle() {
        openTestURL();

        assertEquals("Unexpected initial state", "(0, 0)",
                getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "State", "Edit and refresh Row 0");
        assertEquals("Cell contents did not update correctly", "Foo",
                getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testDelayedRefreshingThroughRowHandle()
            throws InterruptedException {
        openTestURL();

        assertEquals("Unexpected initial state", "(0, 0)",
                getGridElement().getCell(0, 0).getText());
        selectMenuPath("Component", "State", "Delayed edit of Row 0");
        // Still the same data
        assertEquals("Cell contents did not update correctly", "(0, 0)",
                getGridElement().getCell(0, 0).getText());
        sleep(5000);
        // Data should be updated
        assertEquals("Cell contents did not update correctly", "Bar",
                getGridElement().getCell(0, 0).getText());
    }

    @Test
    public void testRefreshingWhenNotInViewThroughRowHandle() {
        openTestURL();

        assertEquals("Unexpected initial state", "(0, 0)",
                getGridElement().getCell(0, 0).getText());
        getGridElement().scrollToRow(100);
        selectMenuPath("Component", "State", "Edit and refresh Row 0");
        assertEquals("Cell contents did not update correctly", "Foo",
                getGridElement().getCell(0, 0).getText());
    }
}
