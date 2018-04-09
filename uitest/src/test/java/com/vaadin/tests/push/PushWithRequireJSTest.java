package com.vaadin.tests.push;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class PushWithRequireJSTest extends SingleBrowserTest {

    @Test
    public void testPushWithRequireJS() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
    }

}
