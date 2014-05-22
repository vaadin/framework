/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutExpandRatioTest extends MultiBrowserTest {
    @Test
    public void gridLayoutExpandRatioTest() {
        openTestURL();
        GridLayoutElement gridLayout5x5 = $(GridLayoutElement.class).get(0);
        GridLayoutElement gridLayout4x4 = $(GridLayoutElement.class).get(1);
        ButtonElement hidingButton = $(ButtonElement.class).get(0);
        hidingButton.click();
        List<WebElement> slots5x5 = gridLayout5x5.findElements(By
                .className("v-gridlayout-slot"));
        List<WebElement> slots4x4 = gridLayout4x4.findElements(By
                .className("v-gridlayout-slot"));
        assertEquals("Different amount of slots", slots5x5.size(),
                slots4x4.size());
        for (int i = 0; i < slots5x5.size(); i++) {
            WebElement compared = slots5x5.get(i);
            WebElement actual = slots4x4.get(i);
            assertEquals("Different top coordinate for element " + i,
                    compared.getCssValue("top"), actual.getCssValue("top"));
            assertEquals("Different left coordinate for element " + i,
                    compared.getCssValue("left"), actual.getCssValue("left"));
        }
    }
}
