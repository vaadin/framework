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

package com.vaadin.tests.extensions;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ResponsiveLayoutUpdateTest extends MultiBrowserTest {

    @Test
    public void testWidthAndHeightRanges() throws Exception {
        openTestURL();

        final PanelElement panelElement = $(PanelElement.class).first();
        // I currently have no idea why PhantomJS wants a click here to work
        // properly
        panelElement.click();
        waitForElementVisible(By.cssSelector(".layout-update"));

        compareScreen("large");

        // Resize below 600px width breakpoint
        testBench().resizeViewPortTo(400, 768);

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return panelElement.getSize().getWidth() < 500;
            }
        });
        compareScreen("small");
    }
}
