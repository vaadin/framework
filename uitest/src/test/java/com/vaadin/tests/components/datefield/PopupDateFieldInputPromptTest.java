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
package com.vaadin.tests.components.datefield;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that state change doesn't set input prompt back to PopupDateField if
 * focus is still in the input field.
 * 
 * @author Vaadin Ltd
 */
public class PopupDateFieldInputPromptTest extends MultiBrowserTest {

    @Test
    public void testInputPrompt() {
        openTestURL();
        TextFieldElement textField = $(TextFieldElement.class).first();
        final PopupDateFieldElement dateField = $(PopupDateFieldElement.class)
                .first();

        // ensure initial state
        Assert.assertFalse("DateField required when it shouldn't be.",
                isRequired(dateField));
        WebElement input = dateField.findElement(By.className("v-textfield"));
        Assert.assertEquals("prompt", input.getAttribute("value"));

        // trigger ValueChange and move focus
        textField.sendKeys("foo", Keys.TAB);

        // wait for ValueChange to update DateField's state and the DateField to
        // gain focus.
        waitForElementRequiredAndFocused(dateField,
                By.className("v-textfield-focus"));

        // ensure prompt hasn't come back when field was set required
        Assert.assertNotEquals("prompt", input.getAttribute("value"));
    }

    private void waitForElementRequiredAndFocused(
            final PopupDateFieldElement dateField, final By locator) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver arg0) {
                if (isRequired(dateField)) {
                    List<WebElement> elements = dateField.findElements(locator);
                    return !elements.isEmpty();
                }

                return false;
            }

            @Override
            public String toString() {
                return "dateField to become required and presence of element located by: "
                        + locator;
            }
        });
    }

    private boolean isRequired(PopupDateFieldElement dateField) {
        return dateField.getAttribute("class").contains("v-required");
    }
}
