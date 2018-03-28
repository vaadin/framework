package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.components.FocusTest;

public class CheckBoxGroupFocusTest extends FocusTest {

    @Test
    public void focusOnInit() {
        openTestURL();
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .first();
        assertTrue(isFocusInsideElement(checkBoxGroup));
    }

    @Test
    public void moveFocusAfterClick() {
        openTestURL();
        $(ButtonElement.class).first().click();
        CheckBoxGroupElement checkBoxGroup = $(CheckBoxGroupElement.class)
                .last();
        assertTrue(isFocusInsideElement(checkBoxGroup));
    }

}
