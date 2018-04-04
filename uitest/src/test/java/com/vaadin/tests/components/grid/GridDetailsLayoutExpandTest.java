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

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests the layouting of Grid's details row when it contains a HorizontalLayout
 * with expand ratios.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridDetailsLayoutExpandTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsersToTest = super.getBrowsersToTest();
        // for some reason PhantomJS doesn't find the label even if it detects
        // the presence
        browsersToTest.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return browsersToTest;
    }

    @Test
    public void testLabelWidths() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        GridElement grid = $(GridElement.class).first();
        int gridWidth = grid.getSize().width;

        grid.getRow(2).click();
        waitForElementPresent(By.id("lbl2"));

        // space left over from first label should be divided equally
        double expectedWidth = (double) (gridWidth - 200) / 2;
        assertLabelWidth("lbl2", expectedWidth);
        assertLabelWidth("lbl3", expectedWidth);
    }

    private void assertLabelWidth(String id, double expectedWidth) {
        // 1px leeway for calculations
        assertThat("Unexpected label width.",
                (double) $(LabelElement.class).id(id).getSize().width,
                closeTo(expectedWidth, 1d));
    }
}
