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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class CustomRendererTest extends MultiBrowserTest {
    @Test
    public void testIntArrayIsRendered() throws Exception {
        openTestURL();

        GridElement grid = findGrid();
        assertEquals("1 :: 1 :: 2 :: 3 :: 5 :: 8 :: 13", grid.getCell(0, 0)
                .getText());
    }

    @Test
    public void testRowAwareRenderer() throws Exception {
        openTestURL();

        GridElement grid = findGrid();
        assertEquals("Click me!", grid.getCell(0, 1).getText());
        assertEquals(CustomRenderer.INIT_DEBUG_LABEL_CAPTION, findDebugLabel()
                .getText());

        grid.getCell(0, 1).click();
        assertEquals("row: 0, key: 0", grid.getCell(0, 1).getText());
        assertEquals("key: 0, itemId: " + CustomRenderer.ITEM_ID,
                findDebugLabel().getText());
    }

    @Test
    public void testBeanRenderer() throws Exception {
        openTestURL();

        assertEquals("SimpleTestBean(42)", findGrid().getCell(0, 2).getText());
    }

    private GridElement findGrid() {
        List<GridElement> elements = $(GridElement.class).all();
        return elements.get(0);
    }

    private LabelElement findDebugLabel() {
        return $(LabelElement.class).id(CustomRenderer.DEBUG_LABEL_ID);
    }
}
