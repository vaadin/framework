package com.vaadin.tests.application;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.WrappedRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

@PreserveOnRefresh
public class RefreshStatePreserve extends AbstractTestUI {

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new Label("window.name: "
                + request.getBrowserDetails().getWindowName()));
        addComponent(new Label("UI id: " + getUIId()));
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
