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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridDisabledSideBarTest extends GridBasicClientFeaturesTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    private void makeColumnHidable() {
        selectMenuPath("Component", "Columns", "Column 0", "Hidable");
    }

    private void toggleSideBarMenuAndDisable() {
        selectMenuPath("Component", "Sidebar", "Open sidebar and disable grid");
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return !findElement(By.className("v-grid-sidebar-button"))
                        .isEnabled();
            }
        });
    }

    private void clickSideBarButton() {
        findElement(By.cssSelector(".v-grid-sidebar-button")).click();
    }

    private void toggleEnabled() {
        selectMenuPath("Component", "State", "Enabled");
    }

    private void assertSideBarContainsClass(String cssClass) {
        assertThat(findElement(By.cssSelector(".v-grid-sidebar"))
                .getAttribute("class"), containsString(cssClass));
    }

    @Test
    public void sidebarButtonIsDisabledOnCreation() {
        selectMenuPath("Component", "State", "Enabled");
        makeColumnHidable();

        clickSideBarButton();

        assertSideBarContainsClass("closed");
    }

    @Test
    public void sidebarButtonCanBeEnabled() {
        makeColumnHidable();

        clickSideBarButton();

        assertSideBarContainsClass("open");
    }

    @Test
    public void sidebarButtonCanBeDisabled() {
        makeColumnHidable();
        toggleEnabled();

        clickSideBarButton();

        assertSideBarContainsClass("closed");
    }

    @Test
    public void sidebarIsClosedOnDisable() {
        makeColumnHidable();

        toggleSideBarMenuAndDisable();

        assertSideBarContainsClass("closed");
    }
}
