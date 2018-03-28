package com.vaadin.tests.elements;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ElementExistsTest extends MultiBrowserTest {
    @Test
    public void testExistsWithoutUI() {
        // Test that an exists query does not throw an exception even when the
        // initialization of the UI has not been done (#14808).
        boolean buttonExists = $(ButtonElement.class).exists();
        assertFalse(
                "$(ButtonElement.class).exists() returned true, but there should be no buttons.",
                buttonExists);
        buttonExists = $(ButtonElement.class).caption("b").exists();
        assertFalse(
                "$(ButtonElement.class).caption(\"b\").exists() returned true, "
                        + "but there should be no buttons.",
                buttonExists);
    }

    @Test
    public void testExistsWithUI() {
        // Test the expected case where the UI has been properly set up.
        openTestURL();
        boolean buttonExists = $(ButtonElement.class).exists();
        assertTrue(
                "No button was found, although one should be present in the UI.",
                buttonExists);
        buttonExists = $(ButtonElement.class).caption("b").exists();
        assertTrue(
                "No button with caption 'b' was found, although one should be present in the UI.",
                buttonExists);
        buttonExists = $(ButtonElement.class).caption("Button 2").exists();
        assertFalse(
                "$(ButtonElement.class).caption(\"Button 2\") returned true, but "
                        + "there should be no button with that caption.",
                buttonExists);
    }
}
