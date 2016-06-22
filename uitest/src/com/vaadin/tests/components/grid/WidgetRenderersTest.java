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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * TB tests for the various builtin widget-based renderers.
 * 
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class WidgetRenderersTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void testProgressBarRenderer() {
        assertTrue(getGridCell(0, 0).isElementPresent(
                By.className("v-progressbar")));
    }

    @Test
    public void testButtonRenderer() {
        WebElement button = getGridCell(0, 1).findElement(
                By.className("v-nativebutton"));

        button.click();

        waitUntilTextUpdated(button, "Clicked!");
    }

    @Test
    public void testButtonRendererAfterCellBeingFocused() {
        GridCellElement buttonCell = getGridCell(0, 1);
        assertFalse("cell should not be focused before focusing",
                buttonCell.isFocused());

        // avoid clicking on the button
        buttonCell.click(buttonCell.getSize().getWidth() - 10, 5);
        assertTrue("cell should be focused after focusing",
                buttonCell.isFocused());

        WebElement button = buttonCell.findElement(By
                .className("v-nativebutton"));
        assertNotEquals("Button should not be clicked before click",
                "Clicked!", button.getText());

        new Actions(getDriver()).moveToElement(button).click().perform();

        waitUntilTextUpdated(button, "Clicked!");
    }

    @Test
    public void testImageRenderer() {
        final WebElement image = getGridCell(0, 2).findElement(
                By.className("gwt-Image"));

        waitUntilmageSrcEndsWith(image, "window/img/close.png");

        image.click();

        waitUntilmageSrcEndsWith(image, "window/img/maximize.png");
    }

    private void waitUntilmageSrcEndsWith(final WebElement image,
            final String expectedText) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return image.getAttribute("src").endsWith(expectedText);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String
                        .format("image source to update. Supposed to end with '%s' (was: '%s').",
                                expectedText, image.getAttribute("src"));
            }
        });
    }

    @Test
    public void testColumnReorder() {
        $(ButtonElement.class).caption("Change column order").first().click();

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));

        assertTrue(getGridCell(0, 0)
                .isElementPresent(By.className("gwt-Image")));
        assertTrue(getGridCell(0, 1).isElementPresent(
                By.className("v-progressbar")));
        assertTrue(getGridCell(0, 2).isElementPresent(
                By.className("v-nativebutton")));
    }

    @Test
    public void testPropertyIdInEvent() {
        WebElement button = getGridCell(0, 3).findElement(
                By.className("v-nativebutton"));

        button.click();

        waitUntilTextUpdated(button, WidgetRenderers.PROPERTY_ID);
    }

    GridCellElement getGridCell(int row, int col) {
        return $(GridElement.class).first().getCell(row, col);
    }

    private void waitUntilTextUpdated(final WebElement button,
            final String expectedText) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return button.getText().equals(expectedText);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("button's text to become '%s' (was: '').",
                        expectedText, button.getText());
            }

        });
    }
}
