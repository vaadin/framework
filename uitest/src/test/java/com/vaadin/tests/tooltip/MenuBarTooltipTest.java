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
package com.vaadin.tests.tooltip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.themes.ChameleonTheme;
import com.vaadin.v7.ui.themes.Reindeer;
import com.vaadin.v7.ui.themes.Runo;

public class MenuBarTooltipTest extends MultiBrowserTest {

    @Test
    public void toolTipShouldBeOnTopOfMenuItem() {
        String[] themes = new String[] { ValoTheme.THEME_NAME,
                Reindeer.THEME_NAME, Runo.THEME_NAME,
                ChameleonTheme.THEME_NAME };

        for (String theme : themes) {
            assertZIndices(theme);
        }
    }

    public void assertZIndices(String theme) {
        openTestURL("theme=" + theme);

        $(MenuBarElement.class).first().clickItem("Menu item");

        assertThat(String.format("Invalid z-index for theme %s.", theme),
                getZIndex("v-tooltip"),
                greaterThan(getZIndex("v-menubar-popup")));
    }

    private int getZIndex(String className) {
        return Integer.parseInt(
                findElement(By.className(className)).getCssValue("z-index"));
    }

}
