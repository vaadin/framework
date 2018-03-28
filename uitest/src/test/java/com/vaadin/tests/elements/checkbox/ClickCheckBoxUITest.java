package com.vaadin.tests.elements.checkbox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Testcase used to validate {@link CheckBoxElement#click()} works as expected.
 * See #13763
 */
public class ClickCheckBoxUITest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testClickToggleCheckboxMark() {
        CheckBoxElement checkboxWithLabel = $(CheckBoxElement.class).first();
        CheckBoxElement checkboxWithoutLabel = $(CheckBoxElement.class).last();
        assertFalse(checkboxWithLabel.isChecked());
        assertFalse(checkboxWithoutLabel.isChecked());

        checkboxWithLabel.click();
        assertTrue(checkboxWithLabel.isChecked());
        checkboxWithoutLabel.click();
        assertTrue(checkboxWithoutLabel.isChecked());

        checkboxWithLabel.click();
        assertFalse(checkboxWithLabel.isChecked());
        checkboxWithoutLabel.click();
        assertFalse(checkboxWithoutLabel.isChecked());
    }
}
