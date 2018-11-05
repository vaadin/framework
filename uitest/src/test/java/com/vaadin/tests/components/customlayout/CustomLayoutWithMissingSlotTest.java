package com.vaadin.tests.components.customlayout;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CustomLayoutWithMissingSlotTest extends SingleBrowserTest {

    @Test
    public void ensureRenderedWithoutErrors() {
        setDebug(true);
        openTestURL();
        assertEquals("", getLogRow(0).trim());
        $(ButtonElement.class).first().click();
        assertNoDebugMessage(Level.SEVERE);
        assertEquals("1. Button clicked", getLogRow(0));
    }

}
