package com.vaadin.tests.components;

import com.vaadin.server.AbstractUIProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestUIProvider extends AbstractUIProvider {
    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser;
    }
}
