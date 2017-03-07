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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridRowHeightChangeTest extends MultiBrowserTest {

    private final List<String> themes = Arrays.asList("valo", "reindeer",
            "runo", "chameleon", "base");

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void changeThemeAndMeasureGridHeight() {
        for (String theme : themes) {
            // select theme
            $(NativeSelectElement.class).first().selectByText(theme);

            GridElement grid = $(GridElement.class).first();

            int gridHeight = grid.getSize().getHeight();
            int tabsheetHeight = findElements(
                    By.className("v-tabsheet-content")).get(0).getSize()
                            .getHeight();

            assertEquals("Grid's visible height should be equal to Grid height",
                    gridHeight, tabsheetHeight, 1);
        }
    }
}
