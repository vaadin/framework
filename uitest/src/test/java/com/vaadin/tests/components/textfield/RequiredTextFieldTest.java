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
package com.vaadin.tests.components.textfield;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for .v-required style
 * 
 * @author Vaadin Ltd
 */
public class RequiredTextFieldTest extends MultiBrowserTest {

    @Test
    public void testRequiredStyleName() {
        openTestURL();

        $(ButtonElement.class).first().click();

        Assert.assertTrue("Text field doesn't contain .v-required style",
                getStyles().contains("v-required"));

        $(ButtonElement.class).first().click();

        Assert.assertFalse(
                "Text field contains .v-required style for non-required field",
                getStyles().contains("v-required"));
    }

    private String getStyles() {
        return $(TextFieldElement.class).first().getAttribute("class");
    }

}
