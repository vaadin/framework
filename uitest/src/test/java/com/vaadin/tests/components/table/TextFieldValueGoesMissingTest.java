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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a text field's value isn't cleared after a label in the same
 * layout is changed.
 * 
 * @since 7.3
 * @author Vaadin Ltd
 */
public class TextFieldValueGoesMissingTest extends MultiBrowserTest {

    /* This test was rewritten from a TB2 test. */
    @Test
    public void valueMissingTest() throws Exception {
        openTestURL();

        waitForElementVisible(By.className("v-textfield"));

        TextFieldElement textfield = $(TextFieldElement.class).first();
        textfield.focus();
        textfield.sendKeys("test");

        $(ButtonElement.class).first().click();

        new Actions(getDriver()).contextClick(textfield).perform();

        Assert.assertEquals("test", textfield.getValue());
    }
}
