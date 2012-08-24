package com.vaadin.tests.components.root;

import com.vaadin.Application;
import com.vaadin.UIRequiresMoreInformationException;
import com.vaadin.terminal.AbstractUIProvider;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class RootsInMultipleTabs extends AbstractTestApplication {
    private int numberOfRootsOpened;

    public static class TabRoot extends UI {
        @Override
        protected void init(WrappedRequest request) {
            RootsInMultipleTabs application = (RootsInMultipleTabs) getApplication();
            String message = "This is root number "
                    + ++application.numberOfRootsOpened;

            addComponent(new Label(message));
        }
    }

    public RootsInMultipleTabs() {
        addUIProvider(new AbstractUIProvider() {
            @Override
            public Class<? extends UI> getUIClass(Application application,
                    WrappedRequest request)
                    throws UIRequiresMoreInformationException {
                return TabRoot.class;
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
