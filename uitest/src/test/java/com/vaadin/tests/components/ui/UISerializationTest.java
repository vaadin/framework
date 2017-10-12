package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UISerializationTest extends SingleBrowserTest {

    @Test
    public void uiIsSerialized() throws Exception {
        openTestURL();

        serialize();

        assertTrue(getLogRow(0).startsWith("3. Diff states match, size: "));
        assertTrue(getLogRow(1).startsWith("2. Deserialized UI in "));
        assertTrue(getLogRow(2).startsWith("1. Serialized UI in"));
        assertTrue(getLogRow(2).contains(" into "));
        assertTrue(getLogRow(2).endsWith(" bytes"));
    }

    private void serialize() {
        $(ButtonElement.class).first().click();
    }
}
