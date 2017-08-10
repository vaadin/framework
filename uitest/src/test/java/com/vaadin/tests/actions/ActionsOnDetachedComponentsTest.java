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
package com.vaadin.tests.actions;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class ActionsOnDetachedComponentsTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // focus the page to make shortcuts go to the right place
            getDriver().findElement(By.className("v-app")).click();
        }
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void shortcutActionOnDetachedComponentShouldNotBeHandled()
            throws InterruptedException {

        Actions k = new Actions(driver);
        k.sendKeys("a").perform();
        k.sendKeys("a").perform();
        sleep(500);

        assertElementNotPresent(By.id("layer-A"));
        assertElementPresent(By.id("layer-B"));
        assertThat(getLogRow(0), endsWith("btn-A"));
        assertThat(getLogRow(1), not(endsWith("btn-B")));

    }

    @Test
    public void actionOnDetachedComponentShouldNotBeHandled()
            throws InterruptedException {
        TableElement table = $(TableElement.class).first();
        table.getRow(0).contextClick();
        // Find the opened menu
        WebElement menu = findElement(By.className("v-contextmenu"));
        WebElement menuitem = menu
                .findElement(By.xpath("//*[text() = 'Table action']"));

        Actions doubleClick = new Actions(getDriver());
        doubleClick.doubleClick(menuitem).build().perform();

        assertElementNotPresent(By.id("layer-A"));
        assertElementPresent(By.id("layer-B"));
        assertThat(getLogRow(0), endsWith("tableAction"));
        assertThat(getLogRow(1), not(endsWith("tableAction")));

    }

}
