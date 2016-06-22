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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that when closing the last tab on a TabSheet, another tab gets selected
 * with no error. Only the last tab should be visible, so the actual TabSheet
 * width should be small.
 * 
 * @author Vaadin Ltd
 */
public class TabSheetCloseTest extends MultiBrowserTest {

    private static final String TAB_CLOSE = "//span[@class = 'v-tabsheet-caption-close']";
    private static final String LAST_TAB = "//*[@id = 'tab2']/div/div";
    private static final String SCROLLER_NEXT = "//button[@class = 'v-tabsheet-scrollerNext']";
    private static final String FIRST_TAB = "//*[@id = 'tab0']";
    private static final String SECOND_TAB = "//*[@id = 'tab1']";

    @Test
    public void testClosingOfLastTab() throws Exception {
        openTestURL();

        // Click next button twice to get to the last tab
        findElement(By.xpath(SCROLLER_NEXT)).click();
        findElement(By.xpath(SCROLLER_NEXT)).click();

        findElement(By.xpath(LAST_TAB)).click();

        // Closing last tab will take back to the second tab. Closing that
        // will leave the first tab visible.
        findElements(By.xpath(TAB_CLOSE)).get(2).click();
        assertTrue(findElement(By.xpath(SECOND_TAB)).isDisplayed());
        findElements(By.xpath(TAB_CLOSE)).get(1).click();
        assertTrue(findElement(By.xpath(FIRST_TAB)).isDisplayed());
    }
}