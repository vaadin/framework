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
