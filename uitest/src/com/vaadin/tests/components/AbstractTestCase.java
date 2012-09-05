package com.vaadin.tests.components;

import com.vaadin.LegacyApplication;
import com.vaadin.server.ApplicationContext;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestCase extends LegacyApplication {

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        ApplicationContext context = getContext();
        WebBrowser webBrowser = context.getBrowser();
        return webBrowser;

    }
}
