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
package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.AbstractLayoutElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ColorPickerElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.elements.TreeElement;

/**
 *
 * Test class which have test methods for all components added in the testUI
 * class. Open TestURL is called only once before tests. Parent class should
 * override protected Class<?> getUIClass() to specify which testUI should be
 * used
 */

public abstract class CompatibilityElementComponentGetCaptionBaseTest
        extends MultiBrowserTest {
    AbstractLayoutElement mainLayout;

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void getComboboxCaptionTest() {
        ComboBoxElement elem = mainLayout.$(ComboBoxElement.class).get(0);
        testCaption(elem, 0);
    }

    @Test
    public void getTableCaptionTest() {
        TableElement elem = mainLayout.$(TableElement.class).get(0);
        testCaption(elem, 1);
    }

    @Test
    public void getTreeTableCaptionTest() {
        TreeTableElement elem = mainLayout.$(TreeTableElement.class).get(0);
        testCaption(elem, 2);
    }

    @Test
    public void getTreeCaptionTest() {
        TreeElement elem = mainLayout.$(TreeElement.class).get(0);
        testCaption(elem, 3);
    }

    @Test
    public void getTwinColSelectCaptionTest() {
        TwinColSelectElement elem = mainLayout.$(TwinColSelectElement.class)
                .get(0);
        testCaption(elem, 4);
    }

    @Test
    public void getOptionGroupCaptionTest() {
        OptionGroupElement elem = mainLayout.$(OptionGroupElement.class).get(0);
        testCaption(elem, 5);
    }

    @Test
    public void getListSelectCaptionTest() {
        ListSelectElement elem = mainLayout.$(ListSelectElement.class).get(0);
        testCaption(elem, 6);
    }

    @Test
    public void getColorPickerCaptionTest() {
        ColorPickerElement elem = mainLayout.$(ColorPickerElement.class).get(0);
        testCaption(elem, 7);
    }

    @Test
    public void getCheckBoxCaptionTest() {
        CheckBoxElement elem = mainLayout.$(CheckBoxElement.class).get(0);
        testCaption(elem, 8);
    }

    @Test
    public void getTextFieldCaptionTest() {
        TextFieldElement elem = mainLayout.$(TextFieldElement.class).get(0);
        testCaption(elem, 9);
    }

    @Test
    public void getTextAreaCaptionTest() {
        TextAreaElement elem = mainLayout.$(TextAreaElement.class).get(0);
        testCaption(elem, 10);
    }

    @Test
    public void getDateFieldCaptionTest() {
        DateFieldElement elem = mainLayout.$(DateFieldElement.class).get(0);
        testCaption(elem, 11);
    }

    private void testCaption(AbstractComponentElement elem, int caption_index) {
        String actual = elem.getCaption();
        String expected = CompatibilityElementComponentGetCaptionBase.DEFAULT_CAPTIONS[caption_index];
        Assert.assertTrue("Error with class:" + elem.getAttribute("class"),
                expected.equals(actual));
    }

}
