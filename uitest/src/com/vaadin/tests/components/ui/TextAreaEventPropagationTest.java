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
package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that the TextArea widget correctly stops ENTER events from propagating.
 * 
 * @author Vaadin Ltd
 */
public class TextAreaEventPropagationTest extends MultiBrowserTest {

    @Test
    public void textAreaEnterEventPropagation() throws InterruptedException {
        openTestURL();
        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.sendKeys(Keys.ENTER);
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        // Should not have triggered shortcut
        assertEquals(" ", getLogRow(0));
    }

    @Test
    public void testTextAreaEscapeEventPropagation()
            throws InterruptedException {
        openTestURL();
        WebElement textArea = $(TextAreaElement.class).first();
        Actions builder = new Actions(driver);
        builder.click(textArea);
        builder.sendKeys(textArea, "first line asdf");
        builder.sendKeys(Keys.ESCAPE);
        builder.sendKeys(textArea, "second line jkl;");
        builder.perform();

        assertEquals("1. Escape button pressed", getLogRow(0));
    }

    @Test
    public void testTextFieldEscapeEventPropagation() {
        openTestURL();
        WebElement textField = $(TextFieldElement.class).first();
        Actions builder2 = new Actions(driver);
        builder2.click(textField);

        builder2.sendKeys("third line");
        builder2.sendKeys(Keys.ENTER);
        builder2.sendKeys(Keys.ESCAPE);

        builder2.perform();

        assertEquals("1. Enter button pressed", getLogRow(1));
        assertEquals("2. Escape button pressed", getLogRow(0));
    }

    @Test
    public void testTextFieldEnterEventPropagation() {
        openTestURL();
        WebElement textField = $(TextFieldElement.class).first();
        Actions builder2 = new Actions(driver);
        builder2.click(textField);

        builder2.sendKeys("third line");
        builder2.sendKeys(Keys.ENTER);

        builder2.perform();

        assertEquals("1. Enter button pressed", getLogRow(0));
    }
}
