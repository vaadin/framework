package com.vaadin.tests.integration;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;

public abstract class AbstractServletIntegrationTest
        extends AbstractIntegrationTest {

    @Test
    public void runTest() throws Exception {
        // make sure no fading progress indicator from table update is lingering
        Thread.sleep(2000);
        compareScreen("initial");
        $(GridElement.class).first().getCell(0, 1).click();
        // without this, table fetch might have a fading progress indicator
        Thread.sleep(2000);
        compareScreen("finland");
    }

}
