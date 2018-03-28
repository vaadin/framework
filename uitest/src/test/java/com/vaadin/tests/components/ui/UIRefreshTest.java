package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIRefreshTest extends MultiBrowserTest {

    @Test
    public void testUIRefresh() {
        openTestURL();
        assertFalse(reinitLabelExists());
        // Reload the page; UI.refresh should be invoked
        openTestURL();
        assertTrue(reinitLabelExists());
    }

    private boolean reinitLabelExists() {
        return !getDriver().findElements(By.id(UIRefresh.REINIT_ID)).isEmpty();
    }
}
