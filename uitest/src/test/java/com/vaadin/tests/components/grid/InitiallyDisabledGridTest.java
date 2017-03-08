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
package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class InitiallyDisabledGridTest extends SingleBrowserTest {

    @Test
    public void columnsExpanded() {
        openTestURL();

        List<WebElement> cells = findElements(By.className("v-grid-cell"));
        WebElement col0 = cells.get(0);
        WebElement col1 = cells.get(1);
        Assert.assertTrue(col0.getSize().getWidth() > 250);
        Assert.assertTrue(col1.getSize().getWidth() > 250);
    }

    @Test
    public void worksWhenEnabled() {
        openTestURL();
        $(ButtonElement.class).first().click();

        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(80);
        GridCellElement col0 = grid.getCell(80, 0);
        Assert.assertEquals("First 80", col0.getText());
    }
}
