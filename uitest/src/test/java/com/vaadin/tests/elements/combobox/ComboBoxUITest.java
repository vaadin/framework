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

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxUITest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testMultipleSelectByTextOperationsAllowingNullSelection() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        testMultipleSelectByTextOperationsIn(cb);
    }

    @Test
    public void testMultipleSelectByTextOperationsForbiddingNullSelection() {
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);
        testMultipleSelectByTextOperationsIn(cb);
    }

    @Test
    public void testSelectByTextNotFound() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.selectByText("foobar");
    }

    @Test
    public void testSelectByTextWithIcons() {
        ComboBoxElement cb = $(ComboBoxElement.class).id("with-icons");
        cb.selectByText("GBP");
        assertEquals("GBP", cb.getValue());
        cb.selectByText("EUR");
        assertEquals("EUR", cb.getValue());
    }

    private void testMultipleSelectByTextOperationsIn(
            ComboBoxElement comboBox) {
        // Select all items from the menu
        for (String currency : ComboBoxUI.currencies) {
            comboBox.selectByText(currency);

            // Check that the value was selected as the input value of the CB
            WebElement input = comboBox.getInputField();
            assertEquals(currency, input.getAttribute("value"));
        }
    }
}
