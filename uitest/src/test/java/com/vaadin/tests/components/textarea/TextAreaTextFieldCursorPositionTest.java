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
package com.vaadin.tests.components.textarea;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TextAreaTextFieldCursorPositionTest extends SingleBrowserTest {

    @Test
    public void positionUpdatedWithoutTextChanges() {
        openTestURL();
        $(ButtonElement.class).id(TextAreaTextFieldCursorPosition.GET_POSITION)
                .click();
        Assert.assertEquals("2. TextField position: -1", getLogRow(0));
        Assert.assertEquals("1. TextArea position: -1", getLogRow(1));

        $(TextFieldElement.class).first().focus();
        $(TextAreaElement.class).first().focus();
        $(ButtonElement.class).id(TextAreaTextFieldCursorPosition.GET_POSITION)
                .click();
        Assert.assertTrue(getLogRow(0).startsWith("4. TextField position:"));
        Assert.assertNotEquals("4. TextField position: -1", getLogRow(0));
        Assert.assertNotEquals("3. TextArea position: -1", getLogRow(1));
    }
}
