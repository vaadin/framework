package com.vaadin.tests;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;

public class VerifyBrowserVersion extends TestBase {

    @Override
    protected void setup() {
        WebApplicationContext context = (WebApplicationContext) getContext();
        WebBrowser browser = context.getBrowser();
        addComponent(new Label(browser.getBrowserApplication()));
        addComponent(new Label("Touch device? "
                + (browser.isTouchDevice() ? "YES" : "No")));
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
