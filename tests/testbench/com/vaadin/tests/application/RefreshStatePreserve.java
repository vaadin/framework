package com.vaadin.tests.application;

import com.vaadin.annotations.RootInitRequiresBrowserDetails;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

public class RefreshStatePreserve extends AbstractTestApplication {
    @RootInitRequiresBrowserDetails
    public static class RefreshStateRoot extends Root {
        @Override
        public void init(WrappedRequest request) {
            getContent().addComponent(
                    new Label("window.name: "
                            + request.getBrowserDetails().getWindowName()));
            getContent().addComponent(new Label("Root id: " + getRootId()));
        }
    }

    @Override
    public void init() {
        super.init();
        setRootPreserved(true);
    }

    @Override
    protected String getRootClassName(WrappedRequest request) {
        return RefreshStateRoot.class.getName();
    }

    @Override
    protected String getTestDescription() {
        return "Refreshing the browser window should preserve the state";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8068);
    }
}
