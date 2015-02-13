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
package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ErrorInUnloadEventTest extends MultiBrowserTest {

    @Test
    public void testError() {
        openTestURL();
        TextFieldElement user = $(TextFieldElement.class).id("user");
        user.focus();
        user.sendKeys("a");
        PasswordFieldElement pass = $(PasswordFieldElement.class).id("pwd");
        pass.focus();
        pass.sendKeys("d");
        ButtonElement button = $(ButtonElement.class).id("loginButton");
        button.click();

        assertEquals("label is incorrect, error while loading page",
                "...Title...", $(LabelElement.class).first().getText());

        openTestURL();
        // no errors and page fully reloaded
        assertTrue($(LabelElement.class).all().isEmpty());
    }

}
