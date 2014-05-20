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
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if the correct row gets the focus when the row is selected from
 * the serverside and forces the table to scroll down
 * 
 * @author Vaadin Ltd
 */
public class FocusOnSelectedItemTest extends MultiBrowserTest {

    @Test
    public void selectAndScrollFocusesSelectedRow() {
        openTestURL();

        WebElement selectButton = $(ButtonElement.class).caption("Select")
                .first();
        selectButton.click();
        WebElement supposedlyFocusedRow = null;
        WebElement selectedRow = null;
        WebElement focusedStyleRow = null;

        Assert.assertTrue("No row was selected",
                isElementPresent(By.className("v-selected")));

        selectedRow = getDriver().findElement(By.className("v-selected"));

        supposedlyFocusedRow = $(TableElement.class).first().getCell(198, 0);

        Assert.assertTrue("Incorrect row was selected", selectedRow
                .getLocation().getY() == supposedlyFocusedRow.getLocation()
                .getY());

        Assert.assertTrue("No row had the focused style.",
                isElementPresent(By.className("v-table-focus")));

        focusedStyleRow = getDriver()
                .findElement(By.className("v-table-focus"));
        Assert.assertTrue("Incorrect row has the focused style.", selectedRow
                .getLocation().getY() == focusedStyleRow.getLocation().getY());

    }
}
