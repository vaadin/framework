package com.vaadin.tests.components;

import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestUIProvider extends UIProvider {
    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser;
    }
}
