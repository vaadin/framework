/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridApplyFilterWhenScrolledDownTest extends MultiBrowserTest {

    @Test
    public void scrolledCorrectly() throws InterruptedException {
        openTestURL();
        final GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(50);
        $(ButtonElement.class).first().click();
        final TestBenchElement gridBody = grid.getBody();
        // Can't use element API because it scrolls
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return gridBody.findElements(By.className("v-grid-row"))
                        .size() == 1;
            }
        });
        WebElement cell = gridBody.findElements(By.className("v-grid-cell"))
                .get(0);
        Assert.assertEquals("Test", cell.getText());

        int gridHeight = grid.getSize().getHeight();
        int scrollerHeight = grid.getVerticalScroller().getSize().getHeight();
        Assert.assertTrue(
                "Scroller height is " + scrollerHeight
                        + ", should be smaller than grid height: " + gridHeight,
                scrollerHeight < gridHeight);
    }
}
