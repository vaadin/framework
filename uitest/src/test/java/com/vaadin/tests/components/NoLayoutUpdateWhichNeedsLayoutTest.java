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
package com.vaadin.tests.components;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.customelements.CustomProgressBarElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NoLayoutUpdateWhichNeedsLayoutTest extends SingleBrowserTest {

    @Test
    public void layoutRunForNoLayoutUpdate() {
        openTestURL("debug");
        ButtonElement open = $(ButtonElement.class).id("openWindow");
        open.click();
        final CustomProgressBarElement progress = $(
                CustomProgressBarElement.class).first();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                double p = progress.getValue();
                return Math.abs(p - 0.5) < 0.01;
            }
        });

        ButtonElement close = $(ButtonElement.class).id("closeWindow");
        close.click();

        assertNoErrorNotifications();
    }
}
