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
package com.vaadin.tests.components.combobox;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class ComboBoxResetValueTest extends MultiBrowserTest {

    private ComboBoxElement comboBoxWithNullSelectionItemId;
    private ComboBoxElement comboBoxWithoutNullSelectionItemId;
    private ComboBoxElement comboBoxWithNullNotAllowed;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();

        comboBoxWithNullSelectionItemId = $(ComboBoxElement.class).id(
                ComboBoxResetValue.WITH_SET_NULL_SELECTION_ITEM_ID);

        comboBoxWithoutNullSelectionItemId = $(ComboBoxElement.class).id(
                ComboBoxResetValue.WITHOUT_NULL_SELECTION_ITEM_ID);

        comboBoxWithNullNotAllowed = $(ComboBoxElement.class).id(
                ComboBoxResetValue.NULL_SELECTION_NOT_ALLOWED);

        clickResetButton();
    }

    @Test
    public void testNullSelectionAllowedAndSetNullSelectionItemId() {
        comboBoxWithNullSelectionItemId.openPopup();

        assertThatNullSelectionItemSelected(comboBoxWithNullSelectionItemId);
    }

    @Test
    public void testFilterNullSelectionAllowedAndSetNullSelectionItemId() {
        comboBoxWithNullSelectionItemId.sendKeys("foo", Keys.TAB);

        assertThatNullSelectionItemSelected(comboBoxWithNullSelectionItemId);
    }

    @Test
    public void testNullSelectionAllowedWithoutNullSelectionItemId() {
        comboBoxWithoutNullSelectionItemId.openPopup();

        assertThatSelectionIsEmpty(comboBoxWithoutNullSelectionItemId);
    }

    @Test
    public void testFilterNullSelectionAllowedWithoutNullSelectionItemId() {
        comboBoxWithoutNullSelectionItemId.sendKeys("foo", Keys.TAB);

        assertThatSelectionIsEmpty(comboBoxWithoutNullSelectionItemId);
    }

    @Test
    public void testNullSelectionNotAllowed() {
        comboBoxWithNullNotAllowed.openPopup();

        assertThatSelectionIsEmpty(comboBoxWithNullNotAllowed);
    }

    @Test
    public void testFilterNullSelectionNotAllowed() {
        comboBoxWithNullNotAllowed.sendKeys("1", Keys.TAB);
        comboBoxWithNullNotAllowed.sendKeys(Keys.BACK_SPACE, Keys.TAB);

        assertThat("Selection changed when it shouldn't have.",
                comboBoxWithNullNotAllowed.getText(), is("1"));
    }

    private void assertThatNullSelectionItemSelected(ComboBoxElement comboBox) {
        assertThat("Null selection item not selected.", comboBox.getText(),
                is(ComboBoxResetValue.EMPTY_VALUE));
    }

    private void assertThatSelectionIsEmpty(ComboBoxElement comboBox) {
        assertThat("Something selected when should be empty.",
                comboBox.getText(), is(""));
    }

    private void clickResetButton() {
        ButtonElement resetButton = $(ButtonElement.class).first();
        resetButton.click();
    }
}
