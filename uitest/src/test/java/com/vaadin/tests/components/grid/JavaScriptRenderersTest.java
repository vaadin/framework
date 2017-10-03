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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class JavaScriptRenderersTest extends MultiBrowserTest {

    @Test
    public void testJavaScriptRenderer() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement cell_1_1 = grid.getCell(1, 1);

        GridCellElement cell_2_2 = grid.getCell(2, 2);

        // Verify render functionality
        assertEquals("Bean(2, 0)", cell_1_1.getText());

        assertEquals("string2", cell_2_2.getText());

        // Verify init functionality
        assertEquals("1", cell_1_1.getAttribute("column"));

        // Verify onbrowserevent
        cell_1_1.click();
        assertTrue(cell_1_1.getText().startsWith("Clicked 1 with key 2 at"));
    }

    @Test
    public void testJavaScriptRendererDestroy() {
        openTestURL("debug");
        // make sure the log tab is open
        openDebugLogTab();
        waitForDebugMessage(
                "Your JavaScript connector (com_vaadin_tests_components_grid_JavaScriptStringRendererWithDestoryMethod) has a typo. The destory method should be renamed to destroy.");

        $(ButtonElement.class).first().click();

        WebElement log = findElement(By.id("clientLog"));
        String text = log.getText();
        assertTrue(text.contains("destory: 19/3"));
        assertTrue(text.contains("destroy: 19/2"));
        assertTrue(text.contains("destroy: 0/2"));
        assertTrue(text.contains("destory: 0/3"));
    }
}
