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
package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * ComboBox should clear its value when setting to null with new items.
 */
public class ComboBoxSetNullWhenNewItemsAllowedTest extends MultiBrowserTest {

    @Test
    public void testNewValueIsClearedAppropriately()
            throws InterruptedException {
        setDebug(true);
        openTestURL();

        WebElement element = $(ComboBoxElement.class).first().findElement(
                By.vaadin("#textbox"));
        ((TestBenchElementCommands) element).click(8, 7);
        element.clear();
        element.sendKeys("New value");
        assertEquals("New value", element.getAttribute("value"));
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
            Thread.sleep(500);
        } else {
            element.sendKeys(Keys.RETURN);
        }

        assertEquals("", element.getAttribute("value"));
    }
}
