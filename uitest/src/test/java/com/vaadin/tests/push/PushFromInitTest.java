package com.vaadin.tests.push;

import org.junit.Test;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class PushFromInitTest extends MultiBrowserTest {
    @Test
    public void testPushFromInit() {
        openTestURL();

        waitUntil(input -> ("3. " + PushFromInit.LOG_AFTER_INIT)
                .equals(getLogRow(0)));

    }
}
