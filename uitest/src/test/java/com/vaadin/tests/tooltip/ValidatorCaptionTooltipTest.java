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
package com.vaadin.tests.tooltip;

import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Test to see if validators create error tooltips correctly.
 * 
 * @author Vaadin Ltd
 */
public class ValidatorCaptionTooltipTest extends TooltipTest {
    @Test
    public void validatorWithError() throws Exception {
        openTestURL();

        TextFieldElement field = $(TextFieldElement.class).get(0);
        String fieldValue = field.getAttribute("value");
        String expected = "Valid value is between 0 and 100. " + fieldValue
                + " is not.";
        checkTooltip(field, expected);
    }

    @Test
    public void validatorWithoutError() throws Exception {
        openTestURL();
        TextFieldElement field = $(TextFieldElement.class).get(1);
        checkTooltip(field, null);
    }
}
