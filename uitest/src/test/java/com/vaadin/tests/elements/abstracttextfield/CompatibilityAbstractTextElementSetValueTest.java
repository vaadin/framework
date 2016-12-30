/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.elements.abstracttextfield;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CompatibilityAbstractTextElementSetValueTest
        extends MultiBrowserTest {
    private final static String TYPED_STRING = "this is typed string";

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
        Assert.assertEquals(TYPED_STRING, elem.getValue());
        Assert.assertEquals("1", eventCount.getText());
    }

    // helper methods
    // checks that setValue method works
    private void checkType(AbstractTextFieldElement elem,
            LabelElement eventCount) {
        // check first that the initial value is set
        Assert.assertEquals(
                CompatibilityAbstractTextElementSetValue.INITIAL_VALUE,
                elem.getValue());
        elem.setValue(TYPED_STRING);

        // check that typed value is the same
        Assert.assertEquals(TYPED_STRING, elem.getValue());

        // checks that there was only one change value event
        Assert.assertEquals("1", eventCount.getText());

    }
}
