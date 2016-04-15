/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.form;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.FormElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.TooltipTest;

public class FormTooltipsTest extends TooltipTest {

    @Test
    public void testTooltipConfiguration() throws Exception {
        openTestURL();
        // first name tooltip

        WebElement fieldElement = $(FormElement.class).first()
                .$(TextFieldElement.class).first();
        checkTooltip(fieldElement, "Fields own tooltip");
        clearTooltip();
        checkTooltipNotPresent();

        // first name caption tooltip
        checkTooltip(
                $(FormElement.class).first().findElement(
                        By.className("v-caption")), "Fields own tooltip");

        clearTooltip();
        checkTooltipNotPresent();

        // Form should not have a description tooltip
        checkTooltip($(FormElement.class).first(), null);

        // Form error message should not have a tooltip
        checkTooltip(By.className("v-form-errormessage"), null);

        // last name should have no tooltip
        checkTooltip($(TextFieldElement.class).get(1), null);

        // last name caption should have no tooltip
        checkTooltip(
                $(FormElement.class).first()
                        .findElements(By.className("v-caption")).get(1), null);
    }

}