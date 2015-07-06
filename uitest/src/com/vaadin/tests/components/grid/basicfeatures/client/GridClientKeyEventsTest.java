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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridClientKeyEventsTest extends GridBasicClientFeaturesTest {

    private List<String> eventOrder = Arrays.asList("Down", "Up", "Press");

    @Test
    public void testBodyKeyEvents() throws IOException {
        openTestURL();

        getGridElement().getCell(2, 2).click();

        new Actions(getDriver()).sendKeys("a").perform();

        for (int i = 0; i < 3; ++i) {
            assertEquals("Body key event handler was not called.",
                    "(2, 2) event: GridKey" + eventOrder.get(i) + "Event:["
                            + (eventOrder.get(i).equals("Press") ? "a" : 65)
                            + "]",
                    findElements(By.className("v-label")).get(i * 3).getText());

            assertTrue("Header key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText().isEmpty());
            assertTrue("Footer key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 2)
                            .getText().isEmpty());
        }

    }

    @Test
    public void testHeaderKeyEvents() throws IOException {
        openTestURL();

        getGridElement().getHeaderCell(0, 2).click();

        new Actions(getDriver()).sendKeys("a").perform();

        for (int i = 0; i < 3; ++i) {
            assertEquals("Header key event handler was not called.",
                    "(0, 2) event: GridKey" + eventOrder.get(i) + "Event:["
                            + (eventOrder.get(i).equals("Press") ? "a" : 65)
                            + "]",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText());

            assertTrue("Body key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3).getText()
                            .isEmpty());
            assertTrue("Footer key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 2)
                            .getText().isEmpty());
        }
    }

    @Test
    public void selectAllUsingKeyboard() {
        openTestURL();

        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");
        selectMenuPath("Component", "State", "Selection mode", "multi");

        // Focus cell above select all checkbox
        getGridElement().getHeaderCell(0, 0).click();
        Assert.assertFalse(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform();
        Assert.assertFalse(isRowSelected(1));

        // Move down to select all checkbox cell
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        Assert.assertFalse(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform(); // select all
        Assert.assertTrue(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform(); // deselect all
        Assert.assertFalse(isRowSelected(1));

        // Move down to header below select all checkbox cell
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        Assert.assertFalse(isRowSelected(1));
        new Actions(getDriver()).sendKeys(" ").perform(); // deselect all
        Assert.assertFalse(isRowSelected(1));

    }

    @Test
    public void testFooterKeyEvents() throws IOException {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");
        getGridElement().getFooterCell(0, 2).click();

        new Actions(getDriver()).sendKeys("a").perform();

        for (int i = 0; i < 3; ++i) {
            assertEquals("Footer key event handler was not called.",
                    "(0, 2) event: GridKey" + eventOrder.get(i) + "Event:["
                            + (eventOrder.get(i).equals("Press") ? "a" : 65)
                            + "]",
                    findElements(By.className("v-label")).get(i * 3 + 2)
                            .getText());

            assertTrue("Body key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3).getText()
                            .isEmpty());
            assertTrue("Header key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText().isEmpty());

        }
    }

    @Test
    public void testNoKeyEventsFromWidget() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 2", "Header Type",
                "Widget Header");
        GridCellElement header = getGridElement().getHeaderCell(0, 2);
        header.findElement(By.tagName("button")).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        for (int i = 0; i < 3; ++i) {
            assertTrue("Header key event handler got called unexpectedly.",
                    findElements(By.className("v-label")).get(i * 3 + 1)
                            .getText().isEmpty());

        }
    }

}
