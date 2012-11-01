package com.vaadin.tests.application;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.Page.FragmentChangedEvent;
import com.vaadin.server.Page.FragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Label;

@PreserveOnRefresh
public class RefreshStatePreserve extends AbstractTestUI {

    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        // Internal parameter sent by vaadinBootstrap.js,
        addComponent(new Label("window.name: " + request.getParameter("wn")));
        addComponent(new Label("UI id: " + getUIId()));
        addComponent(log);

        log.log("Initial fragment: " + getPage().getFragment());
        getPage().addFragmentChangedListener(new FragmentChangedListener() {
            @Override
            public void fragmentChanged(FragmentChangedEvent event) {
                log.log("Fragment changed to " + event.getFragment());
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