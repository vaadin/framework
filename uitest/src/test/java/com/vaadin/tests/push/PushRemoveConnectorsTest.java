package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class PushRemoveConnectorsTest extends SingleBrowserTest {

    @Test
    public void testNoMemoryLeak() throws InterruptedException {
        openTestURL();
        $(ButtonElement.class).id(PushRemoveConnectors.START).click();
        Thread.sleep(5000);
        int last = getMemoryUsage();
        int i = 0;
        while (i++ < 10) {
            Thread.sleep(5000);
            int now = getMemoryUsage();
            System.out.println("Memory usage: " + now);
            if (last == now)
                break;

            last = now;
        }
        $(ButtonElement.class).id(PushRemoveConnectors.STOP).click();

        Assert.assertNotEquals(10, i);
    }

    private int getMemoryUsage() {
        return Integer.parseInt(
                getLogRow(0).replaceFirst(".*Serialized session size: ", ""));
    }
}
