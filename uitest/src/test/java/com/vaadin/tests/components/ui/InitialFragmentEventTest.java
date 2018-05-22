package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class InitialFragmentEventTest extends MultiBrowserTest {

    @Test
    public void testChangeFragmentOnServerToMatchClient() {
        openTestURL("#foo");

        assertEquals("Log should not contain any text initially", " ",
                getLogRow(0));

        $(ButtonElement.class).caption("Set fragment to 'foo'").first().click();

        assertEquals("Log should not contain any text after clicking button",
                " ", getLogRow(0));
    }
}
