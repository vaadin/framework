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
package com.vaadin.client;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.client.componentlocator.LocatorUtil;

/*
 * Test LocatorUtil.isUIElement() & isNotificaitonElement methods
 */
public class LocatorUtilTest {

    @Test
    public void testIsUI1() {
        boolean isUI = LocatorUtil.isUIElement("com.vaadin.ui.UI");
        assertTrue(isUI);
    }

    @Test
    public void testIsUI2() {
        boolean isUI = LocatorUtil.isUIElement("/com.vaadin.ui.UI");
        assertTrue(isUI);
    }

    @Test
    public void testIsUI3() {
        boolean isUI = LocatorUtil
                .isUIElement("//com.vaadin.ui.UI[RandomString");
        assertTrue(isUI);
    }

    @Test
    public void testIsUI4() {
        boolean isUI = LocatorUtil.isUIElement("//com.vaadin.ui.UI[0]");
        assertTrue(isUI);
    }

    @Test
    public void testIsNotification1() {
        boolean isUI = LocatorUtil
                .isNotificationElement("com.vaadin.ui.VNotification");
        assertTrue(isUI);
    }

    @Test
    public void testIsNotification2() {
        boolean isUI = LocatorUtil
                .isNotificationElement("com.vaadin.ui.Notification");
        assertTrue(isUI);
    }

    @Test
    public void testIsNotification3() {
        boolean isUI = LocatorUtil
                .isNotificationElement("/com.vaadin.ui.VNotification[");
        assertTrue(isUI);
    }

    @Test
    public void testIsNotification4() {
        boolean isUI = LocatorUtil
                .isNotificationElement("//com.vaadin.ui.VNotification[0]");
        assertTrue(isUI);
    }
}
