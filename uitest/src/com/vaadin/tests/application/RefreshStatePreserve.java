package com.vaadin.tests.application;

import com.vaadin.Application;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.AbstractUIProvider;
import com.vaadin.server.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class RefreshStatePreserve extends AbstractTestApplication {
    @PreserveOnRefresh
    public static class RefreshStateUI extends UI {
        @Override
        public void init(WrappedRequest request) {
            getContent().addComponent(
                    new Label("window.name: "
                            + request.getBrowserDetails().getWindowName()));
            getContent().addComponent(new Label("UI id: " + getUIId()));
        }
    }

    @Override
    public void init() {
        super.init();
        addUIProvider(new AbstractUIProvider() {
            @Override
            public Class<? extends UI> getUIClass(Application application,
                    WrappedRequest request) {
                return RefreshStateUI.class;
            }
        });
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
