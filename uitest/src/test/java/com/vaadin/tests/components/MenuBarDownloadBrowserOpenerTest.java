package com.vaadin.tests.components;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MenuBarDownloadBrowserOpenerTest extends SingleBrowserTest {
    @Test
    public void testTriggerExtension() {
        MenuBarElement first = $(MenuBarElement.class).first();
        first.clickItem("TestExtension", "RunMe");
        Assert.assertEquals("TRIGGERED!",getFullLog());

        first.clickItem("TestExtension", "AddTrigger");
        first.clickItem("TestExtension", "RunMe");
        Assert.assertEquals("TRIGGERED!;TRIGGERED!;TRIGGERED!",getFullLog());

        first.clickItem("TestExtension", "RemoveTrigger");
        first.clickItem("TestExtension", "RunMe");
        Assert.assertEquals("TRIGGERED!;TRIGGERED!;TRIGGERED!;TRIGGERED!",getFullLog());

    }

    protected String getFullLog() {
        return getLogs().stream().collect(Collectors.joining(";"));
    }
}
