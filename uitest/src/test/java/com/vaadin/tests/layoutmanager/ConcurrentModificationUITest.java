package com.vaadin.tests.layoutmanager;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ConcurrentModificationUITest extends SingleBrowserTest {

    @Test
    public void noExceptionWhenEnlarging() {
        testBench().resizeViewPortTo(100, 100);
        openTestURL("debug");
        testBench().resizeViewPortTo(200, 200);
        assertNoErrorNotifications();
    }
}
