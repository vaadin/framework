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
package com.vaadin.tests.tooltip;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Test if tooltips in subwindows behave correctly
 *
 * @author Vaadin Ltd
 */
public class TooltipInWindowTest extends TooltipTest {

    @Test
    public void testTooltipsInSubWindow() throws Exception {
        openTestURL();

        WebElement textfield = vaadinElementById("tf1");

        checkTooltip(textfield, "My tooltip");

        ensureVisibleTooltipPositionedCorrectly(textfield);

        clearTooltip();

        checkTooltip(textfield, "My tooltip");

        clearTooltip();
    }

    private WebElement getTooltipContainerElement() {
        return getDriver().findElement(By.className("v-tooltip"));
    }

    private void ensureVisibleTooltipPositionedCorrectly(WebElement textfield)
            throws InterruptedException {
        int tooltipX = getTooltip().getLocation().getX();
        int textfieldX = textfield.getLocation().getX();
        assertGreaterOrEqual("Tooltip should be positioned on the textfield ("
                + tooltipX + " < " + textfieldX + ")", tooltipX, textfieldX);
    }

    private void ensureHiddenTooltipPositionedCorrectly() {
        int tooltipX = getTooltipContainerElement().getLocation().getX();
        assertLessThanOrEqual(
                "Tooltip should be positioned outside of viewport (was at "
                        + tooltipX + ")", tooltipX, -1000);
    }
}
