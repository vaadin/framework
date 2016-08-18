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

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridRendererChangeTest extends MultiBrowserTest {

    @Test
    public void testChangeRenderer() {
        setDebug(true);
        openTestURL();

        GridCellElement cell = $(GridElement.class).first().getCell(0, 0);
        assertTrue("No button in the first cell.",
                cell.isElementPresent(By.tagName("button")));
        int width = cell.getSize().getWidth();

        List<ButtonElement> buttons = $(ButtonElement.class).all();
        Collections.reverse(buttons);

        // Order: TextRenderer, HTMLRenderer, ButtonRenderer
        for (ButtonElement button : buttons) {
            button.click();
            assertNoErrorNotifications();
            cell = $(GridElement.class).first().getCell(0, 0);
            assertEquals("Cell size changed", width, cell.getSize().getWidth());
        }

        assertTrue("No button in the first cell.",
                cell.isElementPresent(By.tagName("button")));
    }

}