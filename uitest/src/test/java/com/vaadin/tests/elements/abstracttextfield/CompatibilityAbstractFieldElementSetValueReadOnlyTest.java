package com.vaadin.tests.elements.abstracttextfield;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CompatibilityAbstractFieldElementSetValueReadOnlyTest
        extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test(expected = ReadOnlyException.class)
    public void testNativeSelect() {
        NativeSelectElement elem = $(NativeSelectElement.class).first();
        elem.setValue("");
    }

    @Test(expected = ReadOnlyException.class)
    public void testOptionGroup() {
        OptionGroupElement elem = $(OptionGroupElement.class).first();
        elem.setValue("");
    }

    @Test(expected = ReadOnlyException.class)
    public void testTextField() {
        TextFieldElement elem = $(TextFieldElement.class).first();
        elem.setValue("");
    }

    @Test(expected = ReadOnlyException.class)
    public void testTextArea() {
        TextAreaElement elem = $(TextAreaElement.class).first();
        elem.setValue("");
    }

    @Test(expected = ReadOnlyException.class)
    public void testPasswordField() {
        PasswordFieldElement elem = $(PasswordFieldElement.class).first();
        elem.setValue("");
    }

    @Test(expected = ReadOnlyException.class)
    public void testDateField() {
        DateFieldElement elem = $(DateFieldElement.class).first();
        elem.setValue("");
    }
}
