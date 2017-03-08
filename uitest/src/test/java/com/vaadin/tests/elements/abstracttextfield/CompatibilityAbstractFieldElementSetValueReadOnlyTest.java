/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractComponentElement.ReadOnlyException;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
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

    @Test(expected = ReadOnlyException.class)
    public void testCheckBox() {
        CheckBoxElement elem = $(CheckBoxElement.class).first();
        elem.click();
    }

    @Ignore("SliderElement.setValue does not exist")
    @Test(expected = ReadOnlyException.class)
    public void testSlider() {
        $(SliderElement.class).first();
    }

    @Test(expected = ReadOnlyException.class)
    public void testListSelect() {
        ListSelectElement elem = $(ListSelectElement.class).first();
        elem.selectByText("foo");
    }

    @Test(expected = ReadOnlyException.class)
    public void testListSelectDeselect() {
        ListSelectElement elem = $(ListSelectElement.class).first();
        elem.deselectByText("foo");
    }

    @Ignore("RichTextAreaElement does not have a setValue")
    @Test(expected = ReadOnlyException.class)
    public void testRichTextArea() {
        RichTextAreaElement elem = $(RichTextAreaElement.class).first();
    }

    @Test(expected = ReadOnlyException.class)
    public void testTwinColSelect() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).first();
        elem.selectByText("foo");
    }

    @Test(expected = ReadOnlyException.class)
    public void testTwinColSelectDeselect() {
        TwinColSelectElement elem = $(TwinColSelectElement.class).first();
        elem.deselectByText("foo");
    }

    @Test(expected = ReadOnlyException.class)
    public void testComboBox() {
        ComboBoxElement elem = $(ComboBoxElement.class).first();
        elem.selectByText("foo");
    }
}
