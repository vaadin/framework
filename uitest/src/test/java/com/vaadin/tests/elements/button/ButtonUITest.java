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
package com.vaadin.tests.elements.button;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ButtonUITest extends MultiBrowserTest {
    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testButtonWithQUIETStyle() {
        ButtonElement button = $(ButtonElement.class)
                .id(ButtonUI.QUIET_BUTTON_ID);
        TextFieldElement field = $(TextFieldElement.class).first();
        button.click();
        Assert.assertEquals("Clicked", field.getValue());
    }

    @Test
    public void testButtonWithQUIETStyleNoCaption() {
        ButtonElement button = $(ButtonElement.class)
                .id(ButtonUI.QUIET_BUTTON_NO_CAPTION_ID);
        TextFieldElement field = $(TextFieldElement.class).first();
        button.click();
        Assert.assertEquals("Clicked", field.getValue());
    }

    @Test
    public void testButton_clickButtonWithSleep_TextFieldWorkAsExpected() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class)
                .id(ButtonUI.NORMAL_BUTTON_ID);
        TextFieldElement field = $(TextFieldElement.class)
                .id(ButtonUI.TEXT_FIELD_ID);
        button.click();
        Assert.assertEquals("Clicked", field.getValue());
    }

    @Test
    public void testButton_clickButtonWithSleep_LabelWorkAsExpected() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class)
                .id(ButtonUI.NORMAL_BUTTON_ID);
        LabelElement label = $(LabelElement.class).id(ButtonUI.LABEL_ID);
        button.click();

        Assert.assertEquals("Clicked", label.getText());
    }
}
