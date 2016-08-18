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

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for top level menu item which should close its sub-menus each time when
 * it's clicked. Also it checks sub-menu item which should not close its
 * sub-menus if they are opened on click.
 * 
 * @author Vaadin Ltd
 */
public class MenuBarClickOpenedMenuTest extends MultiBrowserTest {

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        expand("v-menubar-menuitem-first-level");
        expand("v-menubar-menuitem-second-level");
        expand("v-menubar-menuitem-third-level");
        checkPresence("v-menubar-menuitem-leaf", true);
    }

    @Test
    public void testTopLevelMenuClickClosesSubMenus() {
        click("v-menubar-menuitem-first-level");
        checkSubMenus(false);
    }

    @Test
    public void testSubMenuClickDoesNotCloseSubMenus() {
        click("v-menubar-menuitem-second-level");
        checkSubMenus(true);
    }

    private void expand(String menuItemClassName) {
        checkPresence(menuItemClassName, true);
        click(menuItemClassName);
    }

    private void click(String menuItemClassName) {
        findElement(By.className(menuItemClassName)).click();
    }

    private void checkSubMenus(boolean present) {
        checkPresence("v-menubar-menuitem-second-level", present);
        checkPresence("v-menubar-menuitem-third-level", present);
        checkPresence("v-menubar-menuitem-leaf", present);
    }

    private void checkPresence(final String menuItemClassName,
            final boolean present) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return isElementPresent(
                        By.className(menuItemClassName)) == present;
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return menuItemClassName + " to " + (present ? "" : "not ")
                        + "be present";
            }
        });
    }
}
