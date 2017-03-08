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
package com.vaadin.tests.themes.valo;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableSortIndicatorTest extends MultiBrowserTest {

    private void clickOnCellHeader() {
        clickElementByClass("v-table-header-cell");
    }

    @Test
    public void ascendingIndicatorIsShown() throws IOException {
        openTestURL();

        clickOnCellHeader();

        compareScreen("ascending");
    }

    @Test
    public void descendingIndicatorIsShown() throws IOException {
        openTestURL();

        clickOnCellHeader();
        clickOnSortIndicator();

        compareScreen("descending");
    }

    private void clickOnSortIndicator() {
        clickElementByClass("v-table-sort-indicator");
    }

    private void clickElementByClass(String className) {
        findElementByClass(className).click();
    }

    private WebElement findElementByClass(String className) {
        return driver.findElement(By.className(className));
    }
}
