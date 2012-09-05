package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.LegacyApplication;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestCase extends LegacyApplication {

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = Application.getCurrent().getBrowser();
        return webBrowser;

    }
}
