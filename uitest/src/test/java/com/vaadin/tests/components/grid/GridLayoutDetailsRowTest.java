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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that details row displays GridLayout contents properly.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridLayoutDetailsRowTest extends MultiBrowserTest {

    @Test
    public void testLabelHeights() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        GridElement grid = $(GridElement.class).first();

        grid.getRow(2).click(5, 5);
        waitForElementPresent(By.id("lbl2"));

        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        int gridLayoutHeight = gridLayout.getSize().height;

        // height should be divided equally
        double expectedHeight = gridLayoutHeight / 4;
        assertLabelHeight("lbl1", expectedHeight);
        assertLabelHeight("lbl2", expectedHeight);
        assertLabelHeight("lbl3", expectedHeight);
        assertLabelHeight("lbl4", expectedHeight);
    }

    private void assertLabelHeight(String id, double expectedHeight) {
        // 1px leeway for calculations
        assertThat("Unexpected label height.",
                (double) $(LabelElement.class).id(id).getSize().height,
                closeTo(expectedHeight, 1d));
    }
}
