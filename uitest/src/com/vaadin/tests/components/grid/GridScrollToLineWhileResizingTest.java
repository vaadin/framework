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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridScrollToLineWhileResizingTest extends MultiBrowserTest {

    @Test
    public void testScrollToLineWorksWhileMovingSplitProgrammatically() {
        openTestURL();

        $(GridElement.class).first().getCell(21, 0).click();

        List<WebElement> cells = findElements(By.className("v-grid-cell"));
        boolean foundCell21 = false;
        for (WebElement cell : cells) {
            if ("cell21".equals(cell.getText())) {
                foundCell21 = true;
            }
        }

        assertTrue(foundCell21);
    }
}