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
package com.vaadin.tests.application;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;

public class CriticalNotificationsTest extends MultiBrowserThemeTest {

    @Override
    protected boolean useNativeEventsForIE() {
        if (BrowserUtil.isIE(getDesiredCapabilities(), 11)) {
            // Use JavaScript events only for IE11
            return false;
        } else {
            return true;
        }
    }

    @Test
    public void internalError() throws Exception {
        testCriticalNotification("Internal error");
    }

    @Test
    public void internalErrorDetails() throws Exception {
        testCriticalNotification("Internal error", true);
    }

    @Test
    public void custom() throws Exception {
        testCriticalNotification("Custom");
    }

    @Test
    public void sessionExpired() throws Exception {
        testCriticalNotification("Session expired");
    }

    @Test
    public void sessionExpiredDetails() throws Exception {
        testCriticalNotification("Session expired", true);
    }

    private void testCriticalNotification(String buttonCaption)
            throws Exception {
        testCriticalNotification(buttonCaption, false);
    }

    private void testCriticalNotification(String buttonCaption,
            boolean withDetails) throws Exception {
        openTestURL(); // "theme=" + getTheme());
        if (withDetails) {
            click($(CheckBoxElement.class).caption("Include details").first());
        }
        $(ButtonElement.class).caption(buttonCaption).first().click();

        // some critical notifications invalidate the session, and if a
        // screenshot does not match, waitForVaadin would cause the screenshot
        // comparison to crash because of a missing session
        testBench().disableWaitForVaadin();

        // Give the notification some time to animate
        sleep(1000);
        compareScreen($(NotificationElement.class).first(),
                "systemnotification");
    }

    @Override
    protected Class<?> getUIClass() {
        return CriticalNotifications.class;
    }

}
