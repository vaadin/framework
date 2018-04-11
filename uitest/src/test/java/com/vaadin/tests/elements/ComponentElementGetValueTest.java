package com.vaadin.tests.elements;

import static org.junit.Assert.assertEquals;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.testbench.elements.RichTextAreaElement;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComponentElementGetValueTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void checkComboBox() {
        ComboBoxElement elem = $(ComboBoxElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkListSelect() {
        ListSelectElement elem = $(ListSelectElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkNativeSelect() {
        NativeSelectElement elem = $(NativeSelectElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkCheckBoxGroup() {
        CheckBoxGroupElement elem = $(CheckBoxGroupElement.class).get(0);
        List<String> expected = Collections
                .singletonList(ComponentElementGetValue.TEST_STRING_VALUE);
        List<String> actual = elem.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkRadioButtonGroup() {
        RadioButtonGroupElement elem = $(RadioButtonGroupElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkTwinColSelect() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).get(0);
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkTextField() {
        TextFieldElement elem = $(TextFieldElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkTextArea() {
        TextAreaElement elem = $(TextAreaElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkPassword() {
        PasswordFieldElement elem = $(PasswordFieldElement.class).get(0);
        checkValue(elem);
    }

    @Test
    public void checkCheckBox() {
        CheckBoxElement cb = $(CheckBoxElement.class).get(0);
        String expected = "checked";
        String actual = cb.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkDateField() {
        DateFieldElement df = $(DateFieldElement.class).get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expected = formatter
                .format(ComponentElementGetValue.TEST_DATE_VALUE);
        String actual = df.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void checkRichTextArea() {
        RichTextAreaElement elem = $(RichTextAreaElement.class).first();
        String expected = ComponentElementGetValue.TESTGET_STRING_VALUE_RICHTEXTAREA;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }

    private void checkValue(AbstractTextFieldElement elem) {
        String expected = ComponentElementGetValue.TEST_STRING_VALUE;
        String actual = elem.getValue();
        assertEquals(expected, actual);
    }
}
