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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if Table appears to scroll up under an obscure set of conditions
 * (Scrolled down, set to expand, selecting updates a TextField that precedes
 * the Table in a VerticalLayout.) (#10106)
 * 
 * @author Vaadin Ltd
 */
public class TableScrollUpOnSelectTest extends MultiBrowserTest {

    @Test
    public void TestThatSelectingDoesntScroll() {
        openTestURL();

        // WebElement table = driver.findElement(By.vaadin("//Table"));
        WebElement row = $(TableElement.class).first().getCell(49, 0);
        final WebElement scrollPositionDisplay = getDriver().findElement(
                By.className("v-table-scrollposition"));
        waitUntilNot(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return scrollPositionDisplay.isDisplayed();
            }
        }, 10);

        int rowLocation = row.getLocation().getY();
        row.click();
        int newRowLocation = row.getLocation().getY();

        Assert.assertTrue("Table has scrolled.", rowLocation == newRowLocation);
    }
}
