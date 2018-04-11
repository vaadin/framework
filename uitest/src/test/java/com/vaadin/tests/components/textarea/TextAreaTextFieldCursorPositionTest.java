package com.vaadin.tests.components.textarea;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TextAreaTextFieldCursorPositionTest extends SingleBrowserTest {

    @Test
    public void positionUpdatedWithoutTextChanges() {
        openTestURL();
        $(ButtonElement.class).id(TextAreaTextFieldCursorPosition.GET_POSITION)
                .click();
        assertEquals("2. TextField position: -1", getLogRow(0));
        assertEquals("1. TextArea position: -1", getLogRow(1));

        $(TextFieldElement.class).first().focus();
        $(TextAreaElement.class).first().focus();
        $(ButtonElement.class).id(TextAreaTextFieldCursorPosition.GET_POSITION)
                .click();
        assertTrue(getLogRow(0).startsWith("4. TextField position:"));
        assertNotEquals("4. TextField position: -1", getLogRow(0));
        assertNotEquals("3. TextArea position: -1", getLogRow(1));
    }
}
