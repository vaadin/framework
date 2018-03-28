package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public abstract class IdlePushChannelTest extends MultiBrowserTest {

    private static final int SEVEN_MINUTES_IN_MS = 7 * 60 * 1000;

    @Test
    public void longWaitBetweenActions() throws Exception {
        openTestURL();
        BasicPushTest.getIncrementButton(this).click();
        assertEquals(1, BasicPushTest.getClientCounter(this));
        sleep(SEVEN_MINUTES_IN_MS);
        BasicPushTest.getIncrementButton(this).click();
        assertEquals(2, BasicPushTest.getClientCounter(this));
    }

}
