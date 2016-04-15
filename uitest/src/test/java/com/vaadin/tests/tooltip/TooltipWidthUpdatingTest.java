/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.TooltipTest;

public class TooltipWidthUpdatingTest extends TooltipTest {

    @Test
    public void testTooltipWidthUpdating() {
        openTestURL();

        WebElement btnLongTooltip = vaadinElementById("longTooltip");
        WebElement btnShortTooltip = vaadinElementById("shortTooltip");

        moveMouseToTopLeft(btnLongTooltip);
        testBenchElement(btnLongTooltip).showTooltip();

        moveMouseToTopLeft(btnShortTooltip);
        testBenchElement(btnShortTooltip).showTooltip();

        assertThat(getDriver().findElement(By.className("popupContent"))
                .getSize().getWidth(), lessThan(TooltipWidthUpdating.MAX_WIDTH));
    }

}