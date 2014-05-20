package com.vaadin.tests.application;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Label;

@PreserveOnRefresh
public class RefreshStatePreserveTitle extends AbstractTestUI {

    private Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        getPage().setTitle("TEST");
        addComponent(new Label(
                "Refresh the page and observe that window title 'TEST' is lost."));
    }

    @Override
    protected String getTestDescription() {
        return "Refreshing the browser window should preserve the window title";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11054);
    }
}
