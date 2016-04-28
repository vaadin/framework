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
package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class CheckBoxRpcCountTest extends MultiBrowserTest {

    @Test
    public void numberOfRpcCallsIsEqualToClicks() {
        openTestURL();

        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        WebElement labelElem = checkBoxElement.findElement(By.tagName("label"));
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        final WebElement countElem = $(LabelElement.class).id("count-label");

        // Click on the actual checkbox.
        inputElem.click();
        // Have to use waitUntil to make this test more stable.
        waitUntilLabelIsUpdated(countElem, "1 RPC call(s) made.");

        // Click on the checkbox label.
        labelElem.click();
        waitUntilLabelIsUpdated(countElem, "2 RPC call(s) made.");

        // Again on the label.
        labelElem.click();
        waitUntilLabelIsUpdated(countElem, "3 RPC call(s) made.");
    }

    private void waitUntilLabelIsUpdated(final WebElement countElem,
            final String expectedText) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return countElem.getText().equals(expectedText);
            }
        }, 5);
    }
}
