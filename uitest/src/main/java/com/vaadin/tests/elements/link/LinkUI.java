package com.vaadin.tests.elements.link;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Link;

public class LinkUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Link link = new Link("server root", new ExternalResource("/"));
        addComponent(link);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on a link should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15357;
    }
}
