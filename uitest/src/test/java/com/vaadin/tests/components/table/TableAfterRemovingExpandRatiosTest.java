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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests checks that column width is restored after removing expand ratios.
 *
 * @author Vaadin Ltd
 */
public class TableAfterRemovingExpandRatiosTest extends MultiBrowserTest {

    private WebElement initialHeader;
    private WebElement expandedHeader;

    private WebElement expandButton;
    private WebElement unExpandButton;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();

        List<WebElement> tables = driver.findElements(By.className("v-table"));

        initialHeader = tables.get(0)
                .findElement(By.className("v-table-header-cell"));
        expandedHeader = tables.get(1)
                .findElement(By.className("v-table-header-cell"));

        expandButton = getDriver().findElement(By.id("expand-button"));
        unExpandButton = getDriver().findElement(By.id("unexpand-button"));
    }

    @Test
    public void testRemovingExpandRatios() {

        clickAndWait(expandButton);
        assertNotEquals("Column widths should not be equal after expanding",
                expandedHeader.getSize().getWidth(),
                initialHeader.getSize().getWidth());

        clickAndWait(unExpandButton);
        assertEquals("Column widths should be equal after unexpanding",
                expandedHeader.getSize().getWidth(),
                initialHeader.getSize().getWidth());
    }

    @Test
    public void testRemovingExpandRatiosAfterAddingNewItem() {

        WebElement addItemButton = getDriver().findElement(By.id("add-button"));

        clickAndWait(expandButton);
        clickAndWait(addItemButton);
        clickAndWait(unExpandButton);
        assertEquals(
                "Column widths should be equal after adding item and unexpanding",
                expandedHeader.getSize().getWidth(),
                initialHeader.getSize().getWidth());
    }

    private void clickAndWait(WebElement elem) {
        elem.click();
        testBench(driver).waitForVaadin();
    }

}
