package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIRefreshTest extends MultiBrowserTest {

    @Test
    public void testUIRefresh() {
        openTestURL();
        Assert.assertFalse(reinitLabelExists());
        // Reload the page; UI.refresh should be invoked
        openTestURL();
        Assert.assertTrue(reinitLabelExists());
    }

    private boolean reinitLabelExists() {
        return !getDriver().findElements(By.id(UIRefresh.REINIT_ID)).isEmpty();
    }
}
