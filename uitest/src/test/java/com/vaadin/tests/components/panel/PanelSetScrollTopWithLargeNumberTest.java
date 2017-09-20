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
package com.vaadin.tests.components.panel;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PanelSetScrollTopWithLargeNumberTest extends MultiBrowserTest {
    private PanelElement panel;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-panel"));
        panel = $(PanelElement.class).first();
    }

    @Test
    public void testSetScrollTopWithLargeNumber() {
        WebElement contentNode = panel
                .findElement(By.className("v-panel-content"));
        int panelContentScrollTop = ((Number) executeScript(
                "return arguments[0].scrollTop", contentNode)).intValue();
        assertGreater(
                "Panel should scroll when scrollTop is set to a number larger than panel height",
                panelContentScrollTop, 0);
    }

}
