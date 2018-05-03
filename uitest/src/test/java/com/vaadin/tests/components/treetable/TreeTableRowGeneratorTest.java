package com.vaadin.tests.components.treetable;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeTableRowGeneratorTest extends SingleBrowserTest {

    @Test
    public void testNoExceptionOnRender() {
        setDebug(true);
        openTestURL();

        assertNoErrorNotifications();
    }
}
