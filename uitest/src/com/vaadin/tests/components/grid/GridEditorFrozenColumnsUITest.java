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

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridEditorFrozenColumnsUITest extends MultiBrowserTest {

    @Test
    public void testEditorWithFrozenColumns() throws IOException {
        openTestURL();

        openEditor(10);

        compareScreen("noscroll");

        scrollGridHorizontallyTo(100);

        compareScreen("scrolled");
    }

    private void openEditor(int rowIndex) {
        GridElement grid = $(GridElement.class).first();

        GridCellElement cell = grid.getCell(rowIndex, 1);

        new Actions(driver).moveToElement(cell).doubleClick().build().perform();
    }

    private void scrollGridHorizontallyTo(double px) {
        executeScript("arguments[0].scrollLeft = " + px,
                getGridHorizontalScrollbar());
    }

    private Object executeScript(String script, WebElement element) {
        final WebDriver driver = getDriver();
        if (driver instanceof JavascriptExecutor) {
            final JavascriptExecutor je = (JavascriptExecutor) driver;
            return je.executeScript(script, element);
        } else {
            throw new IllegalStateException("current driver "
                    + getDriver().getClass().getName() + " is not a "
                    + JavascriptExecutor.class.getSimpleName());
        }
    }

    private WebElement getGridHorizontalScrollbar() {
        return getDriver()
                .findElement(
                        By.xpath("//div[contains(@class, \"v-grid-scroller-horizontal\")]"));
    }
}
