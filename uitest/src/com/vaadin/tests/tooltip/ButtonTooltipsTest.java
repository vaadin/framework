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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Tests that tooltip sizes do not change when moving between adjacent elements
 *
 * @author Vaadin Ltd
 */
public class ButtonTooltipsTest extends TooltipTest {

    @Test
    public void tooltipSizeWhenMovingBetweenElements() throws Exception {
        openTestURL();

        WebElement buttonOne = $(ButtonElement.class).caption("One").first();
        WebElement buttonTwo = $(ButtonElement.class).caption("Two").first();

        checkTooltip(buttonOne, ButtonTooltips.longDescription);
        int originalWidth = getTooltipElement().getSize().getWidth();
        int originalHeight = getTooltipElement().getSize().getHeight();

        clearTooltip();
        checkTooltip(buttonTwo, ButtonTooltips.shortDescription);
        moveMouseTo(buttonOne, 5, 5);
        sleep(100);
        assertThat(getTooltipElement().getSize().getWidth(), is(originalWidth));
        assertThat(getTooltipElement().getSize().getHeight(),
                is(originalHeight));
    }
}
