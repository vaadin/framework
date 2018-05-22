package com.vaadin.tests.components.treetable;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class MinimalWidthColumnsTest extends MultiBrowserTest {

    @Test
    public void testFor1pxDifference() throws Exception {
        openTestURL();
        sleep(500);
        compareScreen("onepixdifference");
    }

}
