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
package com.vaadin.tests.components.colorpicker;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ColorPickerGridUITest extends SingleBrowserTest {

    @Test
    public void testNoError() throws Exception {
        openTestURL();

        // find the color picker grid and click on the second color
        WebElement grid = getDriver()
                .findElement(By.className("v-colorpicker-grid"));
        // click on the second color
        grid.findElements(By.tagName("td")).get(1).click();

        // check that the color picker does not have component error set
        if (hasCssClass(grid, "v-colorpicker-grid-error")) {
            Assert.fail(
                    "ColorPickerGrid should not have an active component error");
        }
    }
}
