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
package com.vaadin.v7.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridWidgetRendererChangeTest extends SingleBrowserTest {

    @Test
    public void testChangeWidgetRenderer() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "Change first renderer");

        assertNoErrorNotifications();

        selectMenuPath("Component", "Change first renderer");

        assertNoErrorNotifications();

        // First renderer OK

        selectMenuPath("Component", "Change second renderer");

        assertNoErrorNotifications();

        selectMenuPath("Component", "Change second renderer");

        assertNoErrorNotifications();

    }

    @Override
    protected void selectMenu(String menuCaption) {
        // GWT menu does not need to be clicked.
        selectMenu(menuCaption, false);
    }

    @Override
    protected WebElement getMenuElement(String menuCaption) {
        return getDriver()
                .findElement(By.xpath("//td[text() = '" + menuCaption + "']"));
    }

}
