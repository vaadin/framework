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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class JavaScriptRenderersTest extends MultiBrowserTest {

    @Test
    public void testJavaScriptRenderer() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement cell_1_2 = grid.getCell(1, 2);

        // Verify render functionality
        Assert.assertEquals("Bean(2, 0)", cell_1_2.getText());

        // Verify init functionality
        Assert.assertEquals("2", cell_1_2.getAttribute("column"));

        // Verify onbrowserevent
        cell_1_2.click();
        Assert.assertTrue(cell_1_2.getText().startsWith(
                "Clicked 1 with key 1 at"));
    }
}
