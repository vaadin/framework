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
        Label touchDevice = new Label("Touch device? "
                + (browser.isTouchDevice() ? "YES" : "No"));
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
