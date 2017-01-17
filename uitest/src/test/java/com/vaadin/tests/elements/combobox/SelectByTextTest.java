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
package com.vaadin.tests.elements.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Validates ComboBox.selectByText(String s) works properly if input String s
 * contains parentheses
 */
public class SelectByTextTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    private void selectAndAssertValue(String text) {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        comboBox.selectByText(text);

        assertEquals(text, getComboBoxValue());
        assertEquals("Value is now '" + text + "'",
                $(LabelElement.class).last().getText());
    }

    @Test
    public void selectByParenthesesOnly() {
        selectAndAssertValue("(");
    }

    @Test
    public void selectByStartingParentheses() {
        selectAndAssertValue("(Value");
    }

    @Test
    public void selectByFinishingParentheses() {
        selectAndAssertValue("Value(");
    }

    @Test
    public void selectByRegularParentheses() {
        selectAndAssertValue("Value(i)");
    }

    @Test
    public void selectByComplexParenthesesCase() {
        selectAndAssertValue(
                "((Test ) selectByTest() method(with' parentheses)((");
    }

    private String getComboBoxValue() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        WebElement textbox = comboBox.findElement(By.vaadin("#textbox"));
        return textbox.getAttribute("value");
    }

    @Test
    public void selectSharedPrefixOption() {
        for (String text : new String[] { "Value 2", "Value 22",
                "Value 222" }) {
            selectAndAssertValue(text);
        }
    }

}
