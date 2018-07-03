package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
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

    @Test
    public void focusDoesNotGoIntoWrapperElement() {
        openTestURL();
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertTrue("Focus not in the second radio button group.",
                isFocusInsideElement($(RadioButtonGroupElement.class).last()));
        assertEquals("Focus should not be in the wrapping div.", "input",
                getFocusedElement().getTagName());
    }
}
