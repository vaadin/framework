package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.server.AbstractUIProvider;
import com.vaadin.server.ApplicationContext;
import com.vaadin.server.WebBrowser;

public abstract class AbstractTestUIProvider extends AbstractUIProvider {
    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        ApplicationContext context = Application.getCurrent().getContext();
        WebBrowser webBrowser = context.getBrowser();
        return webBrowser;
    }
}
