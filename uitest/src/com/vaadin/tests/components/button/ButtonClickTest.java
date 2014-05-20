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
package com.vaadin.tests.components.button;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ButtonClickTest extends MultiBrowserTest {

    @Test
    public void buttonMouseDownOutOverUp() {
        openTestURL();

        WebElement clickedButton = vaadinElement("/VVerticalLayout[0]/VButton[0]");
        WebElement visitedButton = vaadinElement("/VVerticalLayout[0]/VButton[1]");

        new Actions(driver).moveToElement(clickedButton).clickAndHold()
                .moveToElement(visitedButton).moveToElement(clickedButton)
                .release().perform();

        assertEquals(ButtonClick.SUCCESS_TEXT,
                vaadinElement("/VVerticalLayout[0]/VLabel[0]").getText());
    }
}
