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
package com.vaadin.tests.components.table;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AddSelectionToRemovedRangeTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections.unmodifiableList(Arrays.asList(Browser.CHROME
                .getDesiredCapabilities()));
    }

    @Override
    protected DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities cap = new DesiredCapabilities(
                super.getDesiredCapabilities());
        cap.setCapability("requireWindowFocus", true);
        return cap;
    }

    @Test
    public void addAndRemoveItemToRemovedRange() throws IOException {
        openTestURL();
        List<WebElement> rows = driver.findElements(By
                .className("v-table-cell-wrapper"));
        WebElement rangeStart = rows.get(0);
        WebElement rangeEnd = rows.get(1);
        rangeStart.click();
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        rangeEnd.click();
        new Actions(driver).keyUp(Keys.SHIFT).perform();
        driver.findElement(By.className("v-button")).click();
        WebElement extraRow = driver.findElements(
                By.className("v-table-cell-wrapper")).get(1);
        new Actions(driver).keyDown(Keys.CONTROL).click(extraRow)
                .click(extraRow).keyUp(Keys.CONTROL).perform();
        driver.findElement(By.className("v-button")).click();
        try {
            driver.findElement(By.vaadin("Root/VNotification[0]"));
            Assert.fail("Notification is shown");
        } catch (NoSuchElementException e) {
            // All is well.
        }
    }
}
