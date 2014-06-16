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
package com.vaadin.tests.components.orderedlayout;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.HorizontalLayoutElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.TooltipTest;

public class TooltipOnRequiredIndicatorTest extends TooltipTest {

    @Test
    public void testTooltipOnRequiredIndicator() throws Exception {
        openTestURL();

        // gwt-uid-* are not stable across browsers etc. so need to look them up

        // caption
        checkTooltip(
                $(VerticalLayoutElement.class).get(1).findElement(
                        By.className("v-captiontext")),
                "Vertical layout tooltip");
        // required indicator
        checkTooltip(By.className("v-required-field-indicator"),
                "Vertical layout tooltip");

        // caption
        checkTooltip(
                $(HorizontalLayoutElement.class).first().findElement(
                        By.className("v-captiontext")),
                "Horizontal layout tooltip");
        // required indicator
        checkTooltip(
                $(HorizontalLayoutElement.class).first().findElement(
                        By.className("v-required-field-indicator")),
                "Horizontal layout tooltip");
    }
}