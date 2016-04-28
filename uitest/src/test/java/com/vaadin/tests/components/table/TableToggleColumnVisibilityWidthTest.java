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
package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that column keeps its width after it is made invisible and visible
 * again (#12303).
 * 
 * @author Vaadin Ltd
 */
public class TableToggleColumnVisibilityWidthTest extends MultiBrowserTest {

    @Test
    public void testColumnWidthRestoredAfterTogglingVisibility() {
        openTestURL();

        int secondColumnWidthInitial = findElements(
                By.className("v-table-header-cell")).get(1).getSize()
                .getWidth();
        ButtonElement toggleButton = $(ButtonElement.class).id("toggler");

        toggleButton.click();
        Assert.assertEquals("One column should be visible",
                findElements(By.className("v-table-header-cell")).size(), 1);

        toggleButton.click();
        Assert.assertEquals("Two columns should be visible",
                findElements(By.className("v-table-header-cell")).size(), 2);
        int secondColumnWidthRestored = findElements(
                By.className("v-table-header-cell")).get(1).getSize()
                .getWidth();
        Assert.assertEquals(
                "Column width should be the same as it was before hiding",
                secondColumnWidthInitial, secondColumnWidthRestored);

    }

}
