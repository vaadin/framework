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
package com.vaadin.tests.push;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ManualLongPollingPushUITest extends SingleBrowserTest {

    @Test
    public void doubleManualPushDoesNotFreezeApplication() {
        openTestURL();
        $(ButtonElement.class).caption("Double manual push after 1s").first()
                .click();
        waitUntilLogText("2. Second message logged after 1s, followed by manual push");
        $(ButtonElement.class).caption("Manual push after 1s").first().click();
        waitUntilLogText("3. Logged after 1s, followed by manual push");
    }

    private void waitUntilLogText(final String expected) {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String actual;

            @Override
            public Boolean apply(WebDriver arg0) {
                actual = getLogRow(0);
                return expected.equals(actual);
            }

            @Override
            public String toString() {
                return String.format("log text to become '%s' (was: '%s')",
                        expected, actual);
            }
        });
    }
}
