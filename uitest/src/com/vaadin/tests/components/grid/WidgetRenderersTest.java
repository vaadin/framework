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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.grid.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * TB tests for the various builtin widget-based renderers.
 * 
 * @since
 * @author Vaadin Ltd
 */
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
    public void testImageRenderer() {
        openTestURL();

        WebElement image = getGridCell(0, 2).findElement(
                By.className("gwt-Image"));

        assertTrue(image.getAttribute("src").endsWith("window/img/close.png"));

        image.click();

        assertTrue(image.getAttribute("src")
                .endsWith("window/img/maximize.png"));
    }

    GridCellElement getGridCell(int row, int col) {
        return $(GridElement.class).first().getCell(row, col);
    }
}
