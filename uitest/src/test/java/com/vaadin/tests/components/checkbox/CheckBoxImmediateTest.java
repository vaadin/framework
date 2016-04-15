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
package com.vaadin.tests.components.checkbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxImmediateTest extends MultiBrowserTest {

    @Test
    public void testNonImmediateCheckBox() {
        openTestURL();

        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        final WebElement countElem = $(LabelElement.class).id("count");

        inputElem.click();
        assertEquals("Events received: 0", countElem.getText());
    }

    @Test
    public void testImmediateCheckBox() {
        openTestURL();

        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).get(1);
        WebElement inputElem = checkBoxElement.findElement(By.tagName("input"));
        final WebElement countElem = $(LabelElement.class).id("count");

        inputElem.click();
        assertEquals("Events received: 1", countElem.getText());
    }
}
