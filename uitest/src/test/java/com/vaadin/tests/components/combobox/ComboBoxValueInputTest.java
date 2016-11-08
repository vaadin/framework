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
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

/**
 * Tests ComboBox inputs and selections.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxValueInputTest extends MultiBrowserTest {

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    };

    private void sendKeysToComboBox(ComboBoxElement comboBox,
            CharSequence keys) {
        getComboBoxInput(comboBox).sendKeys(keys);
    }

    private void removeFocusFromComboBoxes() {
        $(VerticalLayoutElement.class).first().click();
    }

    private ComboBoxElement getComboBox(String id) {
        return $(ComboBoxElement.class).id(id);
    }

    private String getComboBoxValue(ComboBoxElement comboBox) {
        return getComboBoxInput(comboBox).getAttribute("value");
    }

    private WebElement getComboBoxInput(ComboBoxElement comboBox) {
        return comboBox.findElement(By.tagName("input"));
    }

    @Test
    public void defaultComboBoxClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("default");

        assertThat(getComboBoxValue(comboBox), is(""));

        comboBox.selectByText("Value 1");
        sendKeysToComboBox(comboBox, "abc");

        removeFocusFromComboBoxes();

        assertThat(getComboBoxValue(comboBox), is("Value 1"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    private void assertThatComboBoxSuggestionsAreHidden(
            ComboBoxElement comboBox) {
        assertThat(comboBox.isElementPresent(By.vaadin("#popup")), is(false));
    }

    @Test
    public void comboBoxWithPromptClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("default-prompt");

        assertThat(getComboBoxValue(comboBox), is("Please select"));

        comboBox.selectByText("Value 2");
        sendKeysToComboBox(comboBox, "def");

        removeFocusFromComboBoxes();

        assertThat(getComboBoxValue(comboBox), is("Value 2"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    @Test
    public void comboBoxWithNullItemClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("null");

        assertThat(getComboBoxValue(comboBox), is("Null item"));

        sendKeysToComboBox(comboBox, "ghi");

        removeFocusFromComboBoxes();

        assertThat(getComboBoxValue(comboBox), is("Null item"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    @Test
    public void comboBoxWithNullItemAndPromptClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("null-prompt");

        assertThat(getComboBoxValue(comboBox), is("Null item"));

        sendKeysToComboBox(comboBox, "jkl");

        removeFocusFromComboBoxes();

        assertThat(getComboBoxValue(comboBox), is("Null item"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);

    }

    @Test
    public void comboBoxWithFilteringOffClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("filtering-off");

        assertThat(getComboBoxValue(comboBox), is(""));

        // selectByText doesn't work when filtering is off.
        comboBox.openPopup();
        List<WebElement> filteredItems = findElements(
                By.className("gwt-MenuItem"));
        filteredItems.get(1).click();

        sendKeysToComboBox(comboBox, "mnop");

        removeFocusFromComboBoxes();

        assertThat(getComboBoxValue(comboBox), is("Value 1"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }
}
