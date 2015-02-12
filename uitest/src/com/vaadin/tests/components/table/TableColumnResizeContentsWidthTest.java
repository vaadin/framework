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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

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

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE8();
    }

    @Test
    public void testResizing() throws InterruptedException {
        openTestURL();

        List<ButtonElement> buttons = $(ButtonElement.class).all();

        WebElement resizer = getTable().findElement(
                By.className("v-table-resizer"));

        assertEquals(100, getTextFieldWidth());

        moveResizer(resizer, -20);
        assertEquals(80, getTextFieldWidth());

        moveResizer(resizer, 40);
        assertEquals(120, getTextFieldWidth());

        // click the button for decreasing size
        buttons.get(1).click();
        waitUntilTextFieldWidthIs(80);

        // click the button for increasing size
        buttons.get(0).click();
        waitUntilTextFieldWidthIs(100);
    }

    private void waitUntilTextFieldWidthIs(final int width) {
        waitUntil(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver input) {
                return getTextFieldWidth() == width;
            }
        });
    }

    private int getTextFieldWidth() {
        TableElement table = getTable();
        final WebElement textField = table.findElement(By
                .className("v-textfield"));

        return textField.getSize().width;
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void moveResizer(WebElement resizer, int offset) {
        new Actions(driver).clickAndHold(resizer).moveByOffset(offset, 0)
                .release().perform();
    }
}
