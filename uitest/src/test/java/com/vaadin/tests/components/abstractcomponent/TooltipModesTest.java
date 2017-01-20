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
package com.vaadin.tests.components.abstractcomponent;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * @author Vaadin Ltd
 *
 */
public class TooltipModesTest extends TooltipTest {

    @Test
    public void checkTooltipModes() throws Exception {
        openTestURL();

        $(ButtonElement.class).first().showTooltip();

        // preformatted is default
        checkTooltip("<pre>Several\n lines\n tooltip</pre>");

        // Use html inside tooltip
        $(ButtonElement.class).get(1).click();

        $(ButtonElement.class).first().showTooltip();

        checkTooltip("<div>Html <b><span>tooltip</span></b></div>");

        // Use text inside tooltip
        $(ButtonElement.class).get(2).click();

        $(ButtonElement.class).first().showTooltip();
        checkTooltip("&lt;b&gt;tooltip&lt;/b&gt;");
    }

    @Override
    protected void checkTooltip(String tooltipText)
            throws InterruptedException {
        WebElement tooltip = getTooltip();
        WebElement tooltipContent = tooltip
                .findElement(By.className("v-tooltip-text"));
        Assert.assertEquals(tooltipText,
                tooltipContent.getAttribute("innerHTML"));
    }
}
