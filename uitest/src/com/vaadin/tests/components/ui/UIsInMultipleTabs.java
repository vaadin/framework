package com.vaadin.tests.components.ui;

import com.vaadin.Application;
import com.vaadin.server.AbstractUIProvider;
import com.vaadin.server.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class UIsInMultipleTabs extends AbstractTestApplication {
    private int numberOfUIsOpened;

    public static class TabUI extends UI {
        @Override
        protected void init(WrappedRequest request) {
            UIsInMultipleTabs application = (UIsInMultipleTabs) getApplication();
            String message = "This is UI number "
                    + ++application.numberOfUIsOpened;

            addComponent(new Label(message));
        }
    }

    public UIsInMultipleTabs() {
        addUIProvider(new AbstractUIProvider() {
            @Override
            public Class<? extends UI> getUIClass(Application application,
                    WrappedRequest request) {
                return TabUI.class;
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Opening the same application again (e.g. in a new tab) should create a new UI.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7894);
    }
}
