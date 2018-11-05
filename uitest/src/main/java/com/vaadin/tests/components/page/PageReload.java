package com.vaadin.tests.components.page;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;

public class PageReload extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Press to reload", event -> getPage().reload());
        log("UI id: " + getUIId());
    }

    @Override
    protected String getTestDescription() {
        return "Tests Page.reload(). Click button to refresh the page.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10250;
    }

}
