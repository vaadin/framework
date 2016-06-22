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
package com.vaadin.tests.components.menubar;

import static com.vaadin.tests.components.menubar.MenuBarsWithNesting.itemNames;
import static com.vaadin.tests.components.menubar.MenuBarsWithNesting.nestedItemnames;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * This class tests the method VMenuBar.getSubPartElement(String) by using
 * Vaadin locators for finding the items of a MenuBar.
 * 
 * @author Vaadin Ltd
 */
public class MenuBarsWithNestingTest extends MultiBrowserTest {
    private MenuBarElement firstMenuBar, secondMenuBar;
    private LabelElement label;

    @Before
    public void init() {
        openTestURL();
        firstMenuBar = $(MenuBarElement.class).first();
        secondMenuBar = $(MenuBarElement.class).get(1);
        label = $(LabelElement.class).get(1);
    }

    @Test
    public void testMenuWithoutIcons() {
        WebElement fileMenu = firstMenuBar.findElement(By.vaadin("#File"));
        fileMenu.click();
        WebElement exportMenu = fileMenu.findElement(By.vaadin("#Export.."));
        exportMenu.click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By
                .xpath(".//*[text() = 'As PDF...']")));
    }

    @Test
    public void testMenuWithIcons() throws InterruptedException {
        // There is a separate test for the last item of the second menu.
        for (int i = 0; i < itemNames.length - 1; i++) {
            String itemName = itemNames[i];
            secondMenuBar.findElement(By.vaadin("#" + itemName)).click();
            waitUntil(ExpectedConditions.textToBePresentInElement(
                    label.getWrappedElement(), itemName));
        }
    }

    @Test
    public void testNestedMenuWithIcons() throws InterruptedException {
        String selection = itemNames[itemNames.length - 1];
        for (String itemName : nestedItemnames) {
            WebElement lastMenuItem = secondMenuBar.findElement(By.vaadin("#"
                    + selection));
            lastMenuItem.click();
            lastMenuItem.findElement(By.vaadin("#" + itemName)).click();
            waitUntil(ExpectedConditions.textToBePresentInElement(
                    label.getWrappedElement(), itemName));
        }
    }
}