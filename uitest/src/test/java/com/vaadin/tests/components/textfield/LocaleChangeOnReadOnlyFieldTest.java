package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LocaleChangeOnReadOnlyFieldTest extends MultiBrowserTest {

    @Test
    public void localeIsChangedOnReadOnlyField() {
        openTestURL();

        TextFieldElement textField = $(TextFieldElement.class).first();
        assertEquals("1,024,000", textField.getValue());

        $(ButtonElement.class).caption("Change Locale").first().click();
        assertEquals("1.024.000", textField.getValue());
    }

}
