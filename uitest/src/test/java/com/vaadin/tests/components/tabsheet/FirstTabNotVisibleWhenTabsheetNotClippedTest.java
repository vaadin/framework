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
package com.vaadin.tests.components.tabsheet;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FirstTabNotVisibleWhenTabsheetNotClippedTest
        extends MultiBrowserTest {
    @Test
    public void testNotClippedTabIsVisible() throws InterruptedException {
        openTestURL();

        ButtonElement toggleNotClipped = $(ButtonElement.class)
                .caption("Toggle first not clipped tab").first();

        toggleNotClipped.click();
        TabSheetElement notClippedTabSheet = $(TabSheetElement.class).get(0);
        WebElement firstTab = notClippedTabSheet
                .findElement(By.className("v-tabsheet-tabitemcell-first"));
        String caption = firstTab.findElement(By.className("v-captiontext"))
                .getText();
        Assert.assertEquals("Tab with -first style should be Tab 1", "Tab 1",
                caption);

        toggleNotClipped.click();
        firstTab = notClippedTabSheet
                .findElement(By.className("v-tabsheet-tabitemcell-first"));
        caption = firstTab.findElement(By.className("v-captiontext")).getText();
        Assert.assertEquals("Tab with -first style should be Tab 0", "Tab 0",
                caption);
    }

    @Test
    public void testShowPreviouslyHiddenTab() {
        openTestURL();

        $(ButtonElement.class).caption("show tab D").get(0).click();
        $(ButtonElement.class).caption("show tab C").get(0).click();

        WebElement firstTab = $(TabSheetElement.class).get(2)
                .findElement(By.className("v-tabsheet-tabitemcell-first"));
        String firstCaption = firstTab
                .findElement(By.className("v-captiontext")).getText();

        org.junit.Assert.assertEquals("tab C", firstCaption);

        $(ButtonElement.class).caption("show tab D").get(1).click();
        $(ButtonElement.class).caption("show tab C").get(1).click();

        WebElement secondTab = $(TabSheetElement.class).get(3)
                .findElement(By.className("v-tabsheet-tabitemcell-first"));
        String secondCaption = secondTab
                .findElement(By.className("v-captiontext")).getText();

        org.junit.Assert.assertEquals("tab C", secondCaption);
    }
}
