/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.v7.tests.components.textfield;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TextChangeEventsTest extends SingleBrowserTest {

    @Test
    public void textAreaWaitsForTextChangeEvents() {
        openTestURL();

        TextAreaElement taDefault = $(TextAreaElement.class)
                .caption("Default text area").first();
        taDefault.sendKeys("abc");
        Assert.assertEquals(
                "1. Text change event for Default text area, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

        TextAreaElement taTimeout = $(TextAreaElement.class)
                .caption("Timeout 3s").first();
        taTimeout.sendKeys("abc");
        Assert.assertEquals(
                "2. Text change event for Timeout 3s, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

    }

    @Test
    public void textFieldWaitsForTextChangeEvents() {
        openTestURL();

        TextFieldElement tfDefault = $(TextFieldElement.class)
                .caption("Default").first();
        tfDefault.sendKeys("abc");
        Assert.assertEquals(
                "1. Text change event for Default, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

        TextFieldElement tfEager = $(TextFieldElement.class).caption("Eager")
                .first();
        tfEager.sendKeys("abc");
        Assert.assertEquals(
                "2. Text change event for Eager, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

        TextFieldElement tfTimeout = $(TextFieldElement.class)
                .caption("Timeout 3s").first();
        tfTimeout.sendKeys("abc");
        Assert.assertEquals(
                "3. Text change event for Timeout 3s, text content currently:'abc' Cursor at index:3",
                getLogRow(0));

    }
}
