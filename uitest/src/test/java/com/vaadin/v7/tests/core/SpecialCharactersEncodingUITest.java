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
package com.vaadin.v7.tests.core;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SpecialCharactersEncodingUITest extends SingleBrowserTest {

    @Test
    public void checkEncoding() {
        openTestURL();
        String textFieldValue = $(TextFieldElement.class).id("textfield")
                .getValue();
        Assert.assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                textFieldValue);
        LabelElement label = $(LabelElement.class).id("label");
        String labelValue = getHtml(label); // getText() strips some characters
        Assert.assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                labelValue);

        MenuBarElement menubar = $(MenuBarElement.class).first();
        WebElement menuItem = menubar
                .findElement(By.className("v-menubar-menuitem-caption"));
        Assert.assertEquals(SpecialCharactersEncodingUI.textWithZwnj,
                getHtml(menuItem));
    }

    private String getHtml(WebElement element) {
        return element.getAttribute("innerHTML");
    }
}
