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
package com.vaadin.tests.components.ui;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PollListeningTest extends MultiBrowserTest {

    @Test
    public void testReceivePollEvent() {
        openTestURL();
        waitUntilPollEventReceived();
    }

    private void waitUntilPollEventReceived() {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String expected = "PollEvent received";

            @Override
            public Boolean apply(WebDriver arg0) {
                return driver.getPageSource().contains(expected);
            }

            @Override
            public String toString() {
                return String.format("page to contain text '%s'", expected);
            }
        });
    }
}
