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
package com.vaadin.tests.components.accordion;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Accordion: tabs should stay selectable after remove tab.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionRemoveTabTest extends MultiBrowserTest {

    @Test
    public void testRemoveTab() {
        openTestURL();

        WebElement button = driver.findElement(By.className("v-button"));
        button.click();

        checkFirstItemHeight("On second tab");

        button.click();

        checkFirstItemHeight("On third tab");
    }

    @Test
    public void testConsoleErrorOnSwitch() {
        setDebug(true);
        openTestURL();
        WebElement firstItem = driver.findElement(By
                .className("v-accordion-item-first"));
        WebElement caption = firstItem.findElement(By
                .className("v-accordion-item-caption"));
        caption.click();
        Assert.assertEquals("Errors present in console", 0,
                findElements(By.className("SEVERE")).size());
    }

    private void checkFirstItemHeight(String text) {
        WebElement firstItem = driver.findElement(By
                .className("v-accordion-item-first"));
        WebElement label = firstItem.findElement(By.className("v-label"));
        Assert.assertEquals("Unexpected text in first item", text,
                label.getText());
        int height = firstItem.getSize().getHeight();
        WebElement accordion = driver.findElement(By.className("v-accordion"));
        Assert.assertTrue("First item in accordion has unexpected height",
                height > accordion.getSize().getHeight() / 2);
    }

}
