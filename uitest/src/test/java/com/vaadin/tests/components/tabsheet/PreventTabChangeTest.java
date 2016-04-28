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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PreventTabChangeTest extends MultiBrowserTest {
    @Test
    public void preventTabChange() throws Exception {
        openTestURL();

        clickTab(1);
        clickTab(2);
        Thread.sleep(2000);
        assertTabSelected(2);
        Assert.assertEquals("Tab 3 contents", getSelectedTabContent().getText());
        clickTab(0);
        clickTab(2);
        assertTabSelected(0);
        Assert.assertEquals("Tab 1 contents", getSelectedTabContent().getText());
    }

    private void assertTabSelected(int i) throws NoSuchElementException {
        WebElement tabItem = findTab(i).findElement(By.xpath(".."));
        Assert.assertTrue("Tab " + i + " should be selected but isn't", tabItem
                .getAttribute("class").contains("v-tabsheet-tabitem-selected"));
    }

    private void clickTab(int i) {
        findTab(i).click();
    }

    private WebElement findTab(int i) {
        return driver.findElement(com.vaadin.testbench.By
                .vaadin("//TabSheet#tab[" + i + "]"));
    }

    private WebElement getSelectedTabContent() {
        return driver.findElement(com.vaadin.testbench.By
                .vaadin("//TabSheet#tabpanel"));
    }

}
