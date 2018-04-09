package com.vaadin.tests.elements.abstracttextfield;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AbstractTextElementSetValueTest extends MultiBrowserTest {
    private static final String TYPED_STRING = "this is typed string";

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void textFieldSetValue() {
        checkType($(TextFieldElement.class).get(0),
                $(LabelElement.class).get(1));
    }

    @Test
    public void passwordFieldSetValue() {
        checkType($(PasswordFieldElement.class).get(0),
                $(LabelElement.class).get(2));
    }

    @Test
    public void textAreaSetValue() {
        checkType($(TextAreaElement.class).get(0),
                $(LabelElement.class).get(3));
    }

    @Test
    public void dateFieldSetValue() {
        DateFieldElement elem = $(DateFieldElement.class).get(0);
        LabelElement eventCount = $(LabelElement.class).get(4);
        // we can type any string in date field element
        elem.setValue(TYPED_STRING);
        // invalid values should stay unchanged
        assertEquals(TYPED_STRING, elem.getValue());
    }

    // helper methods
    // checks that setValue method works
    private void checkType(AbstractTextFieldElement elem,
            LabelElement eventCount) {
        // check first that the initial value is set
        assertEquals(AbstractTextElementSetValue.INITIAL_VALUE,
                elem.getValue());
        elem.setValue(TYPED_STRING);

        // check that typed value is the same
        assertEquals(TYPED_STRING, elem.getValue());

        // checks that there was only one change value event
        assertEquals("1", eventCount.getText());

    }
}
