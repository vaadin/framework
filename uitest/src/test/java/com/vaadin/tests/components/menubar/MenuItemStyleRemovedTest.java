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
package com.vaadin.tests.components.menubar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuItemStyleRemovedTest extends MultiBrowserTest {

    @Test
    public void testCustomStyleShouldStayAfterMenuSelect() {
        openTestURL();

        $(ButtonElement.class).caption("Add styles").first().click();

        MenuBarElement menu = $(MenuBarElement.class).first();
        List<WebElement> elements = menu
                .findElements(By.className("custom-menu-item"));
        Assert.assertEquals(2, elements.size());

        menu.clickItem("first");
        menu.clickItem("second");
        elements = menu.findElements(By.className("custom-menu-item"));
        Assert.assertEquals(2, elements.size());
    }
}
