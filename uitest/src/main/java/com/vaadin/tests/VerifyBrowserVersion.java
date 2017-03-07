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
package com.vaadin.tests;

import com.vaadin.server.WebBrowser;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;

public class VerifyBrowserVersion extends TestBase {

    @Override
    protected void setup() {
        WebBrowser browser = getBrowser();
        Label userAgent = new Label(browser.getBrowserApplication());
        userAgent.setId("userAgent");
        addComponent(userAgent);
        Label touchDevice = new Label(
                "Touch device? " + (browser.isTouchDevice() ? "YES" : "No"));
        touchDevice.setId("touchDevice");
        addComponent(touchDevice);
    }

    @Override
    protected String getDescription() {
        return "Silly test just to get a screenshot of the browser's user agent string";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7655);
    }

}
