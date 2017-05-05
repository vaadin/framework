package com.vaadin.tests.components.tree;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeDefaultConstructorTest extends SingleBrowserTest {

    @Test
    public void default_constructor_no_exceptions() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
        assertFalse(isElementPresent(By.className("v-errorindicator")));
    }
}
