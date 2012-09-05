package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestCase extends Application {

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser;

    }
}
