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
package com.vaadin.tests.themes.valo;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for non-collapsible column opacity for item in column configuration
 * menu.
 *
 * @author Vaadin Ltd
 */
public class CollapsibleTableColumnTest extends MultiBrowserTest {

    @Test
    public void nonCollapsibleColumnOpacity() {
        openTestURL();

        // click on the header to make the column selector visible
        $(TableElement.class).first().getHeaderCell(0).click();
        waitForElementVisible(By.className("v-table-column-selector"));

        findElement(By.className("v-table-column-selector")).click();

        List<WebElement> items = findElements(By.className("v-on"));
        String collapsibleColumnOpacity = items.get(0).getCssValue("opacity");
        String nonCollapsibleColumnOpacity = items.get(1)
                .getCssValue("opacity");

        Assert.assertNotEquals(
                "Opacity value is the same for collapsible "
                        + "and non-collapsible column item",
                collapsibleColumnOpacity, nonCollapsibleColumnOpacity);
    }
}
