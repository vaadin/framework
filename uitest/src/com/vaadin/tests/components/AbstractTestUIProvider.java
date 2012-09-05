package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.server.AbstractUIProvider;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestUIProvider extends AbstractUIProvider {
    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        WebBrowser webBrowser = Application.getCurrent().getBrowser();
        return webBrowser;
    }
}
