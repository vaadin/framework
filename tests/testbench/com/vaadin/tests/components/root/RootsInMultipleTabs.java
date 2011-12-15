package com.vaadin.tests.components.root;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

public class RootsInMultipleTabs extends AbstractTestApplication {
    private int numberOfRootsOpened;

    public static class TabRoot extends Root {
        @Override
        protected void init(WrappedRequest request) {
            RootsInMultipleTabs application = (RootsInMultipleTabs) getApplication();
            String message = "This is root number "
                    + ++application.numberOfRootsOpened;

            addComponent(new Label(message));
        }
    }

    @Override
    protected String getRootClassName(WrappedRequest request) {
        return TabRoot.class.getName();
    }

    @Override
    protected String getTestDescription() {
        return "Opening the same application again (e.g. in a new tab) should create a new Root.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7894);
    }
}
