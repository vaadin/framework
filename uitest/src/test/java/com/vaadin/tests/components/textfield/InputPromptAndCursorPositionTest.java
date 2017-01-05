package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class InputPromptAndCursorPositionTest extends MultiBrowserTest {

    @Test
    public void verifyDatePattern() {
        openTestURL();

        // Clear the current value and reveal the input prompt.
        TextFieldElement textFieldElement = $(TextFieldElement.class).get(0);
        textFieldElement.clear();

        // Update cursor position.
        $(ButtonElement.class).get(0).click();

        // The cursor position should now be zero (not the input prompt length).
        LabelElement cursorPosLabel = $(LabelElement.class).get(1);
        assertEquals("Cursor position: 0", cursorPosLabel.getText());
    }
}
