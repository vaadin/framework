package com.vaadin.tests.application;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserThemeTest;

public class CriticalNotificationsTest extends MultiBrowserThemeTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Arrays.asList(Browser.CHROME.getDesiredCapabilities(),
                Browser.FIREFOX.getDesiredCapabilities());
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
