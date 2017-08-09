/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.v7.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that details row resizes along with the contents properly.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridLayoutDetailsRowResizeTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsersToTest = super.getBrowsersToTest();
        // for some reason PhantomJS doesn't find the label even if it detects
        // the presence
        browsersToTest.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return browsersToTest;
    }

    @Test
    public void testLabelHeights() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));

        GridElement grid = $(GridElement.class).first();

        grid.getRow(2).click();
        waitForElementPresent(By.id("lbl2"));

        VerticalLayoutElement layout = $(VerticalLayoutElement.class)
                .id("details");
        int layoutHeight = layout.getSize().height;

        ButtonElement button = $(ButtonElement.class).id("btn");
        int buttonHeight = button.getSize().height;

        // height should be divided equally
        double expectedLabelHeight = (layoutHeight - buttonHeight) / 3;
        assertLabelHeight("lbl1", expectedLabelHeight);
        assertLabelHeight("lbl2", expectedLabelHeight);
        assertLabelHeight("lbl3", expectedLabelHeight);

        assertDetailsRowHeight(layoutHeight);

        // ensure fourth label isn't present yet
        assertElementNotPresent(By.id("lbl4"));

        button.click();
        waitForElementPresent(By.id("lbl4"));

        // get layout height after the new label has been added
        layoutHeight = layout.getSize().height;

        expectedLabelHeight = (layoutHeight - buttonHeight) / 4;
        assertLabelHeight("lbl1", expectedLabelHeight);
        assertLabelHeight("lbl2", expectedLabelHeight);
        assertLabelHeight("lbl3", expectedLabelHeight);
        assertLabelHeight("lbl4", expectedLabelHeight);

        assertDetailsRowHeight(layoutHeight);
    }

    private void assertLabelHeight(String id, double expectedHeight) {
        // 1px leeway for calculations
        assertThat("Unexpected label height.",
                (double) $(LabelElement.class).id(id).getSize().height,
                closeTo(expectedHeight, 1d));
    }

    private void assertDetailsRowHeight(int layoutHeight) {
        // check that details row height matches layout height (1px leeway)
        WebElement detailsRow = findElement(By.className("v-grid-spacer"));
        assertThat("Unexpected details row height", (double) layoutHeight,
                closeTo(detailsRow.getSize().height, 1d));
    }
}
