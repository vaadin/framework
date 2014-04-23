/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
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
        Thread.sleep(1000);

        WebElement element = findElement();
        ((TestBenchElementCommands) element).click(8, 7);
        element.clear();
        element.sendKeys("New value");
        assertEquals("New value", element.getAttribute("value"));
        element.sendKeys(Keys.RETURN);
        assertEquals("", element.getAttribute("value"));
    }

    private WebElement findElement() {
        return getDriver()
                .findElement(
                        By.vaadin("runcomvaadintestscomponentscomboboxComboBoxSetNullWhenNewItemsAllowed::/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VFilterSelect[0]#textbox"));
    }

}
