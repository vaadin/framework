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
package com.vaadin.tests.themes.valo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for the built-in reponsive ("RWD") styles in Valo.
 */
public class ResponsiveStylesTest extends MultiBrowserTest {

    private static final String MENU_STYLENAME = "valo-menu";
    private static final int NARROW_ELEMENT_INDEX = 0;
    private static final int NARROW_WIDTH = 112;
    private static final int WIDE_ELEMENT_INDEX = 1;
    private static final int WIDE_WIDTH = 146;

    /**
     * Tests that valo-menu-responsive can be used in any element on the page,
     * not just as top-level component.
     * 
     * @throws Exception
     */
    @Test
    public void testValoMenuResponsiveParentSize() throws Exception {
        openTestURL();

        List<WebElement> menus = findElements(
                com.vaadin.testbench.By.className(MENU_STYLENAME));

        WebElement narrowMenu = menus.get(NARROW_ELEMENT_INDEX);
        int narrowWidth = narrowMenu.getSize().width;
        assertThat(narrowWidth, equalTo(NARROW_WIDTH));

        WebElement wideMenu = menus.get(WIDE_ELEMENT_INDEX);
        int wideWidth = wideMenu.getSize().width;
        assertThat(wideWidth, equalTo(WIDE_WIDTH));

        compareScreen("defaultMenuWidths");
    }
}
