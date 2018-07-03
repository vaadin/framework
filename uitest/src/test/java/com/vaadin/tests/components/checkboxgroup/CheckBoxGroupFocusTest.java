package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

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

    @Test
    public void focusDoesNotGoIntoWrapperElement() {
        openTestURL();
        new Actions(getDriver()).sendKeys(Keys.TAB, Keys.TAB, Keys.TAB)
                .perform();
        assertTrue("Focus not in the second check box group.",
                isFocusInsideElement($(CheckBoxGroupElement.class).last()));
        assertEquals("Focus should not be in the wrapping div.", "input",
                getFocusedElement().getTagName());
    }

}
