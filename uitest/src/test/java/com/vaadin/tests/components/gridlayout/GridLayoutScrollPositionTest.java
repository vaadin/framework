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
package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutScrollPositionTest extends MultiBrowserTest {

    @Test
    public void testToggleChildComponents() throws Exception {

        final int SCROLLTOP = 100;

        openTestURL();

        WebDriver driver = getDriver();

        WebElement ui = driver.findElement(By.className("v-ui"));

        testBenchElement(ui).scroll(SCROLLTOP);

        driver.findElement(By.id("visibility-toggle"))
                .findElement(By.tagName("input")).click();

        assertEquals("UI scroll position", String.valueOf(SCROLLTOP),
                ui.getAttribute("scrollTop"));
    }
}
