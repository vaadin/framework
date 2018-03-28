package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ConnectorBundleStatusTest extends SingleBrowserTest {

    @Test
    public void testConnectorBundleLoading() {
        openTestURL();

        assertLoaded("__eager");

        $(ButtonElement.class).id("refresh").click();

        assertLoaded("__eager", "__deferred");

        $(ButtonElement.class).id("rta").click();
        $(ButtonElement.class).id("refresh").click();

        assertLoaded("__eager", "__deferred",
                "com.vaadin.client.ui.richtextarea.RichTextAreaConnector");
    }

    private void assertLoaded(String... expectedNames) {
        String bundleStatus = findElement(By.id("bundleStatus")).getText();
        assertEquals(Arrays.asList(expectedNames).toString(), bundleStatus);
    }
}
