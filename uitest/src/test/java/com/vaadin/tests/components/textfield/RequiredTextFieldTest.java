package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for .v-required style
 *
 * @author Vaadin Ltd
 */
public class RequiredTextFieldTest extends MultiBrowserTest {

    @Test
    public void testRequiredStyleName() {
        openTestURL();

        $(ButtonElement.class).first().click();

        assertTrue("Text field doesn't contain .v-required style",
                getStyles().contains("v-required"));

        $(ButtonElement.class).first().click();

        assertFalse(
                "Text field contains .v-required style for non-required field",
                getStyles().contains("v-required"));
    }

    private String getStyles() {
        return $(TextFieldElement.class).first().getAttribute("class");
    }

}
