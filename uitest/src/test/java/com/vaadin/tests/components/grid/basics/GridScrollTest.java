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
package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;

public class GridScrollTest extends GridBasicsTest {

    @Test
    public void workPendingWhileScrolling() {
        openTestURL("theme=valo");
        String script = "var c = window.vaadin.clients.runcomvaadintestscomponentsgridbasicsGridBasics;\n"
                // Scroll down and cause lazy loading
                + "c.getElementByPath(\"//Grid[0]#cell[21]\"); \n"
                + "return c.isActive();";

        Boolean active = (Boolean) executeScript(script);
        assertTrue("Grid should be marked to have workPending while scrolling",
                active);
    }

    @Test
    public void scrollIntoViewThroughSubPart() {
        openTestURL("theme=valo");
        GridElement grid = $(GridElement.class).first();
        assertEquals("(10, 0)", grid.getCell(10, 0).getText());
    }
}
