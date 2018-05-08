package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.components.FocusTest;

public class RadioButtonGroupFocusTest extends FocusTest {

    @Test
    public void focusOnInit() {
        openTestURL();
        RadioButtonGroupElement radioButtonGroup = $(
                RadioButtonGroupElement.class).first();
        assertTrue(isFocusInsideElement(radioButtonGroup));
    }

    @Test
    public void moveFocusAfterClick() {
        openTestURL();
        $(ButtonElement.class).first().click();
        RadioButtonGroupElement radioButtonGroup2 = $(
                RadioButtonGroupElement.class).last();
        assertTrue(isFocusInsideElement(radioButtonGroup2));
    }

}
