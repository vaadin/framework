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
package com.vaadin.tests.components.orderedlayout;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CaptionLeakTest extends MultiBrowserTest {

    @Test
    public void testCaptionLeak() throws Exception {
        setDebug(true);
        openTestURL();

        openLog();

        // this should be present
        // 3 general non-connector elements, none accumulated on click
        checkConnectorCount();

        clearLog();

        $(ButtonElement.class).caption("Set leaky content").first().click();

        checkConnectorCount();

        // nothing accumulates over clicks
        clearLog();

        $(ButtonElement.class).caption("Set leaky content").first().click();
        checkConnectorCount();
    }

    @Test
    public void testNoCaptionLeak() throws Exception {
        setDebug(true);
        openTestURL();

        openLog();

        clearLog();
        $(ButtonElement.class).caption("Set non leaky content").first().click();
        // this should be present
        // 3 general non-connector elements, none accumulated on click
        checkConnectorCount();
    }

    private void openLog() {
        openDebugLogTab();
        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            openDebugLogTab();
        }
    }

    private void clearLog() {
        getDriver().findElement(By.xpath("//button[@title = 'Clear log']"))
                .click();
    }

    private void checkConnectorCount() {
        getDriver()
                .findElement(
                        By.xpath("//span[text() = 'Measured 3 non connector elements']"));
    }
}
