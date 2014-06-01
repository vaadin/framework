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

package com.vaadin.tests.extensions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ResponsiveUITest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        // We need this in order to ensure that the initial width-range is
        // 401px-600px
        testBench().resizeViewPortTo(1024, 768);
    }

    // JQuery style selector
    private WebElement $(String cssSelector) {
        return getDriver().findElement(By.cssSelector(cssSelector));
    }

    @Test
    public void testResizingSplitPanelReflowsLayout() throws Exception {
        openTestURL();

        // IE sometimes has trouble waiting long enough.
        new WebDriverWait(getDriver(), 30).until(ExpectedConditions
                .presenceOfElementLocated(By
                        .cssSelector(".v-csslayout-grid.first")));

        assertEquals("401px-600px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        moveSplitter(200);

        assertEquals("601-800",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        moveSplitter(-350);

        assertEquals("201px-400px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("301px-400px",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        compareScreen("responsive");

        moveSplitter(-200);
        assertEquals("-200px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));

        moveSplitter(-100);
        assertEquals("0-100px",
                $(".v-csslayout-grid.second").getAttribute("width-range"));
    }

    private void moveSplitter(int xOffset) {
        new Actions(getDriver()).clickAndHold($(".v-splitpanel-hsplitter"))
                .moveByOffset(xOffset, 0).release().build().perform();
    }

    @Test
    public void testResizingWindowReflowsLayout() throws Exception {
        openTestURL();

        assertEquals("401px-600px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        testBench().resizeViewPortTo(1224, 768);

        assertEquals("601-800",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        testBench().resizeViewPortTo(674, 768);

        assertEquals("201px-400px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("301px-400px",
                $(".v-csslayout-grid.second").getAttribute("width-range"));
    }
}
