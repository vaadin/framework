package com.vaadin.tests.components;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestCase extends LegacyApplication {

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = VaadinServiceSession.getCurrent().getBrowser();
        return webBrowser;

    }
}
