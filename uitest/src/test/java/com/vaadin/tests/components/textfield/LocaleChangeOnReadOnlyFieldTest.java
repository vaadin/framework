package com.vaadin.tests.components.textfield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.legacyelements.LegacyTextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LocaleChangeOnReadOnlyFieldTest extends MultiBrowserTest {

    @Test
    public void localeIsChangedOnReadOnlyField() {
        openTestURL();

        LegacyTextFieldElement textField = $(LegacyTextFieldElement.class)
                .first();
        assertThat(textField.getValue(), is("1,024,000"));

        $(ButtonElement.class).caption("Change Locale").first().click();
        assertThat(textField.getValue(), is("1.024.000"));
    }

}