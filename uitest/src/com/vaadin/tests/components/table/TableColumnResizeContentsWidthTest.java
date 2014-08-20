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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that components within table cells get resized when their column gets
 * resized.
 * 
 * @author Vaadin Ltd
 */
public class TableColumnResizeContentsWidthTest extends MultiBrowserTest {

    @Test
    public void testResizing() throws InterruptedException {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        List<ButtonElement> buttons = $(ButtonElement.class).all();

        WebElement textField = table.findElement(By.className("v-textfield"));
        WebElement resizer = table.findElement(By.className("v-table-resizer"));

        assertEquals(100, textField.getSize().width);

        // drag resizer to left
        new Actions(getDriver()).moveToElement(resizer).clickAndHold()
                .moveByOffset(-20, 0).release().perform();

        assertEquals(80, textField.getSize().width);

        // drag resizer to right
        new Actions(getDriver()).moveToElement(resizer).clickAndHold()
                .moveByOffset(40, 0).release().perform();

        assertEquals(120, textField.getSize().width);

        // click the button for decreasing size
        buttons.get(1).click();
        sleep(50);

        assertEquals(80, textField.getSize().width);

        // click the button for increasing size
        buttons.get(0).click();
        sleep(50);

        assertEquals(100, textField.getSize().width);
    }

}
