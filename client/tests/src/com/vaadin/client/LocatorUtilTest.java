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
package com.vaadin.client;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.client.componentlocator.LocatorUtil;

/*
 * Test LocatorUtil.isUIElement() & isNotificaitonElement methods
 */
public class LocatorUtilTest extends TestCase {

    public void testIsUI1() {
        boolean isUI = LocatorUtil.isUIElement("com.vaadin.ui.UI");
        Assert.assertTrue(isUI);
    }

    public void testIsUI2() {
        boolean isUI = LocatorUtil.isUIElement("/com.vaadin.ui.UI");
        Assert.assertTrue(isUI);
    }

    public void testIsUI3() {
        boolean isUI = LocatorUtil
                .isUIElement("//com.vaadin.ui.UI[RandomString");
        Assert.assertTrue(isUI);
    }

    public void testIsUI4() {
        boolean isUI = LocatorUtil.isUIElement("//com.vaadin.ui.UI[0]");
        Assert.assertTrue(isUI);
    }

    public void testIsNotification1() {
        boolean isUI = LocatorUtil
                .isNotificationElement("com.vaadin.ui.VNotification");
        Assert.assertTrue(isUI);
    }

    public void testIsNotification2() {
        boolean isUI = LocatorUtil
                .isNotificationElement("com.vaadin.ui.Notification");
        Assert.assertTrue(isUI);
    }

    public void testIsNotification3() {
        boolean isUI = LocatorUtil
                .isNotificationElement("/com.vaadin.ui.VNotification[");
        Assert.assertTrue(isUI);
    }

    public void testIsNotification4() {
        boolean isUI = LocatorUtil
                .isNotificationElement("//com.vaadin.ui.VNotification[0]");
        Assert.assertTrue(isUI);
    }
}
