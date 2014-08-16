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
package com.vaadin.tests.components.tabsheet;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check that TabsheetBaseConnector reacts on disabling its parent.
 * 
 * @author Vaadin Ltd
 */
public class TabSheetInDisabledParentTest extends MultiBrowserTest {

    @Test
    public void testTabsheetInDisabledParent() {
        openTestURL();

        WebElement button = getDriver().findElement(By.className("v-button"));
        // disable parent
        button.click();

        List<WebElement> tabHeaders = getDriver().findElements(
                By.className("v-tabsheet-tabitemcell"));
        tabHeaders.get(1).findElement(By.className("v-captiontext")).click();

        Assert.assertFalse(
                "It's possible to activate TabSheet tab when its parent is disabled",
                tabHeaders.get(1).getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-selected"));

        // enable parent back
        button.click();

        // selected tab is still the same
        tabHeaders = getDriver().findElements(
                By.className("v-tabsheet-tabitemcell"));
        Assert.assertTrue(
                "Tabsheet has wrong selected tab after enabling its parent",
                tabHeaders.get(0).getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-selected"));

        // click to the second tab
        tabHeaders.get(1).findElement(By.className("v-captiontext")).click();
        // check the second tab is selected
        Assert.assertTrue(
                "Second tab is not activated in the Tabsheet after clicking on it",
                tabHeaders.get(1).getAttribute("class")
                        .contains("v-tabsheet-tabitemcell-selected"));
    }

}
