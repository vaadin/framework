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
package com.vaadin.v7.tests.components.grid.basicfeatures;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@SuppressWarnings("all")
@TestCategory("grid")
public class GridClientHeightByRowOnInitTest extends MultiBrowserTest {
    @Test
    public void gridHeightIsMoreThanACoupleOfRows() {
        openTestURL();
        int height = findElement(By.className("v-grid")).getSize().getHeight();
        assertGreater(
                "Grid should be much taller than 150px (was " + height + "px)",
                height, 150);
    }
}
