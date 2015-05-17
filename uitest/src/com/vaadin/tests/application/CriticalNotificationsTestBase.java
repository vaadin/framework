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
import com.vaadin.tests.tb3.MultiBrowserThemeTest;

public abstract class CriticalNotificationsTestBase extends
        MultiBrowserThemeTest {

    public static class ValoCriticalNotificationsTest extends
            CriticalNotificationsTestBase {
        @Override
        protected String getTheme() {
            return "valo";
        }
    }

    public static class ReindeerCriticalNotificationsTest extends
            CriticalNotificationsTestBase {
        @Override
        protected String getTheme() {
            return "reindeer";
        }
    }

    public static class RunoCriticalNotificationsTest extends
            CriticalNotificationsTestBase {
        @Override
        protected String getTheme() {
            return "runo";
        }
    }

    public static class ChameleonCriticalNotificationsTest extends
            CriticalNotificationsTestBase {
        @Override
        protected String getTheme() {
            return "chameleon";
        }
    }

    public static class BaseCriticalNotificationsTest extends
            CriticalNotificationsTestBase {
        @Override
        protected String getTheme() {
            return "base";
        }
    }

    @Test
    public void authenticationError() throws Exception {
        testCriticalNotification("Authentication error");
    }

    @Test
    public void communicationError() throws Exception {
        testCriticalNotification("Communication error");
    }

    @Test
    public void internalError() throws Exception {
        testCriticalNotification("Internal error");
    }

    @Test
    public void cookiesDisabled() throws Exception {
        testCriticalNotification("Cookies disabled");
    }

    @Test
    public void custom() throws Exception {
        testCriticalNotification("Custom");
    }

    @Test
    public void sessionExpired() throws Exception {
        testCriticalNotification("Session expired");
    }

    private void testCriticalNotification(String buttonCaption)
            throws Exception {
        openTestURL(); // "theme=" + getTheme());
        $(ButtonElement.class).caption(buttonCaption).first().click();
        // Give the notification some time to animate
        sleep(1000);
        compareScreen("notification");
    }

    @Override
    protected Class<?> getUIClass() {
        return CriticalNotifications.class;
    }

}
