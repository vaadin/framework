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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDetailsDetachTest extends MultiBrowserTest {

    @Test
    public void testDetachGridWithDetailsOpen() {
        setDebug(true);
        openTestURL();

        $(GridElement.class).first().getCell(3, 0).click();
        $(GridElement.class).first().getCell(5, 0).click();

        assertNoErrorNotifications();

        $(ButtonElement.class).first().click();

        assertNoErrorNotifications();
    }

    @Test
    public void testDetachAndReattachGridWithDetailsOpen() {
        setDebug(true);
        openTestURL();

        $(GridElement.class).first().getCell(3, 0).click();
        $(GridElement.class).first().getCell(5, 0).click();

        assertNoErrorNotifications();

        $(ButtonElement.class).first().click();

        assertNoErrorNotifications();

        $(ButtonElement.class).get(1).click();

        assertNoErrorNotifications();

        List<WebElement> spacers = findElements(By.className("v-grid-spacer"));
        Assert.assertEquals("Not enough spacers in DOM", 2, spacers.size());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 3", spacers.get(0).getText());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 5", spacers.get(1).getText());
    }

    @Test
    public void testDetachAndImmediateReattach() {
        setDebug(true);
        openTestURL();

        $(GridElement.class).first().getCell(3, 0).click();
        $(GridElement.class).first().getCell(5, 0).click();

        assertNoErrorNotifications();

        // Detach and Re-attach Grid
        $(ButtonElement.class).get(1).click();

        assertNoErrorNotifications();

        List<WebElement> spacers = findElements(By.className("v-grid-spacer"));
        Assert.assertEquals("Not enough spacers in DOM", 2, spacers.size());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 3", spacers.get(0).getText());
        Assert.assertEquals("Spacer content not visible",
                "Extra data for Bean 5", spacers.get(1).getText());
    }

}
