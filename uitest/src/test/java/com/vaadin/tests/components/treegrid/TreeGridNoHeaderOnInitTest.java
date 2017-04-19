package com.vaadin.tests.components.treegrid;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridNoHeaderOnInitTest extends SingleBrowserTest {

    @Test
    public void no_exception_thrown() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
    }
}
