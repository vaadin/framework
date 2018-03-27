/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * This class tests that MenuBar and its contents have all the required WAI-ARIA
 * attributes.
 *
 * @since
 * @author Vaadin Ltd
 */
public class MenuBarsWithWaiAriaTest extends MultiBrowserTest {
    private MenuBarElement firstMenuBar;

    @Override
    protected Class<?> getUIClass() {
        return MenuBarsWithNesting.class;
    }

    @Before
    public void init() {
        openTestURL();
        firstMenuBar = $(MenuBarElement.class).first();
    }

    @Test
    public void testMenuBar() {
        assertEquals("menubar", firstMenuBar.getAttribute("role"));
        assertNull(firstMenuBar.getAttribute("aria-haspopup"));
        assertNull(firstMenuBar.getAttribute("aria-disabled"));
        assertEquals("0", firstMenuBar.getAttribute("tabindex"));
    }

    @Test
    public void testSubMenu() {
        WebElement fileMenu = firstMenuBar.findElement(By.vaadin("#File"));
        fileMenu.click();
        WebElement submenu = findElement(By.className("v-menubar-submenu"));
        assertEquals("menu", submenu.getAttribute("role"));
        assertNull(submenu.getAttribute("aria-haspopup"));
        assertNull(submenu.getAttribute("aria-disabled"));
        assertEquals("-1", submenu.getAttribute("tabindex"));
    }

    @Test
    public void testEnabledMenuItems() {
        WebElement fileMenu = firstMenuBar.findElement(By.vaadin("#File"));
        assertEquals("menuitem", fileMenu.getAttribute("role"));
        assertEquals("true", fileMenu.getAttribute("aria-haspopup"));
        assertNull(fileMenu.getAttribute("aria-disabled"));
        assertEquals("-1", fileMenu.getAttribute("tabindex"));
        fileMenu.click();

        WebElement open = fileMenu.findElement(By.vaadin("#Open"));
        assertEquals("menuitem", open.getAttribute("role"));
        assertNull(open.getAttribute("aria-haspopup"));
        assertNull(open.getAttribute("aria-disabled"));
        assertEquals("-1", open.getAttribute("tabindex"));

        WebElement separator = findElement(By.className("v-menubar-separator"));
        assertEquals("separator", separator.getAttribute("role"));
        assertNull(separator.getAttribute("aria-haspopup"));
        assertNull(separator.getAttribute("aria-disabled"));
        assertEquals("-1", separator.getAttribute("tabindex"));
    }

    @Test
    public void testDisabledMenuItem() {
        WebElement disabledMenu = firstMenuBar
                .findElement(By.vaadin("#Disabled"));
        assertEquals("menuitem", disabledMenu.getAttribute("role"));
        assertEquals("true", disabledMenu.getAttribute("aria-haspopup"));
        assertEquals("true", disabledMenu.getAttribute("aria-disabled"));
        assertEquals("-1", disabledMenu.getAttribute("tabindex"));
    }
}
