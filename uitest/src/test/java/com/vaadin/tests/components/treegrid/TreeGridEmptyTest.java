package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridEmptyTest extends SingleBrowserTest {

    @Test
    public void empty_treegrid_initialized_correctly() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
        assertFalse(isElementPresent(By.className("v-errorindicator")));
    }
}
