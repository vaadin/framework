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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class SpaceMenuBarNavigationTest extends MultiBrowserTest {

    @Test
    public void testEnableParentLayout() {
        openTestURL();

        MenuBarElement menu = $(MenuBarElement.class).get(0);
        menu.focus();
        menu.sendKeys(Keys.ARROW_RIGHT);
        menu.sendKeys(Keys.ENTER);

        List<WebElement> captions = driver.findElements(By
                .className("v-menubar-menuitem-caption"));
        boolean found = false;

        for (WebElement caption : captions) {
            if ("subitem".equals(caption.getText())) {
                found = true;
            }
        }
        Assert.assertTrue("Sub menu is not opened on ENTER key", found);

        menu.sendKeys(Keys.SPACE);

        Assert.assertTrue("No result of action triggered by SPACE key",
                isElementPresent(By.className("action-result")));
    }

}
