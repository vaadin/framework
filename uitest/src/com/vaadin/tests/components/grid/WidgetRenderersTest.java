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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * TB tests for the various builtin widget-based renderers.
 * 
 * @since
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class WidgetRenderersTest extends MultiBrowserTest {

    @Test
    public void testProgressBarRenderer() {
        openTestURL();

        assertTrue(getGridCell(0, 0).isElementPresent(
                By.className("v-progressbar")));
    }

    @Test
    public void testButtonRenderer() {
        openTestURL();

        WebElement button = getGridCell(0, 1).findElement(
                By.className("gwt-Button"));

        button.click();

        assertEquals("Clicked!", button.getText());
    }

    @Test
    public void testButtonRendererAfterCellBeingFocused() {
        openTestURL();

        GridCellElement buttonCell = getGridCell(0, 1);
        assertFalse("cell should not be focused before focusing",
                buttonCell.isFocused());

        // avoid clicking on the button
        buttonCell.click(150, 5);
        assertTrue("cell should be focused after focusing",
                buttonCell.isFocused());

        WebElement button = buttonCell.findElement(By.className("gwt-Button"));
        assertNotEquals("Button should not be clicked before click",
                "Clicked!", button.getText());

        new Actions(getDriver()).moveToElement(button).click().perform();
        assertEquals("Button should be clicked after click", "Clicked!",
                button.getText());
    }

    @Test
    public void testImageRenderer() {
        openTestURL();

        WebElement image = getGridCell(0, 2).findElement(
                By.className("gwt-Image"));

        assertTrue(image.getAttribute("src").endsWith("window/img/close.png"));

        image.click();

        assertTrue(image.getAttribute("src")
                .endsWith("window/img/maximize.png"));
    }

    @Test
    public void testColumnReorder() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).caption("Change column order").first().click();

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));

        assertTrue(getGridCell(0, 0)
                .isElementPresent(By.className("gwt-Image")));
        assertTrue(getGridCell(0, 1).isElementPresent(
                By.className("v-progressbar")));
        assertTrue(getGridCell(0, 2).isElementPresent(
                By.className("gwt-Button")));
    }

    @Test
    public void testPropertyIdInEvent() {
        openTestURL();
        WebElement button = getGridCell(0, 3).findElement(
                By.className("gwt-Button"));
        button.click();
        assertEquals(WidgetRenderers.PROPERTY_ID, button.getText());
    }

    GridCellElement getGridCell(int row, int col) {
        return $(GridElement.class).first().getCell(row, col);
    }
}
