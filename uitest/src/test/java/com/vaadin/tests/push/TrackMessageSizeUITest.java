package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class TrackMessageSizeUITest extends MultiBrowserTest {
    @Test
    public void runTests() {
        openTestURL();
        Assert.assertEquals("1. All tests run",
                vaadinElementById("Log_row_0").getText());
    }
}
