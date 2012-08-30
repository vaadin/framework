package com.vaadin.tests.components;

import com.vaadin.Application;
import com.vaadin.server.AbstractWebApplicationContext;
import com.vaadin.server.WebBrowser;
import com.vaadin.service.ApplicationContext;

public abstract class AbstractTestApplication extends Application {
    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        ApplicationContext context = getContext();
        if (context instanceof AbstractWebApplicationContext) {
            WebBrowser webBrowser = ((AbstractWebApplicationContext) context)
                    .getBrowser();
            return webBrowser;
        }

        return null;
    }
}
