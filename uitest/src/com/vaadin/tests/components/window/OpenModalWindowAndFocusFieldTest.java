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
package com.vaadin.tests.components.window;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OpenModalWindowAndFocusFieldTest extends MultiBrowserTest {

    @Test
    public void openModalAndFocusField() {
        openTestURL();
        $(ButtonElement.class).id("openFocus").click();
        TextAreaElement textArea = $(TextAreaElement.class).first();

        assertElementsEquals(textArea, getActiveElement());
    }

    @Test
    public void openModal() {
        openTestURL();
        $(ButtonElement.class).id("open").click();
        // WindowElement window = $(WindowElement.class).first();
        WebElement windowFocusElement = findElement(By
                .xpath("//div[@class='v-window-contents']/div[@class='v-scrollable']"));

        assertElementsEquals(windowFocusElement, getActiveElement());
    }

}
