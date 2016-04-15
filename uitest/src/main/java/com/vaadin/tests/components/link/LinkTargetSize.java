package com.vaadin.tests.components.link;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Link;

public class LinkTargetSize extends TestBase {

    @Override
    protected String getDescription() {
        return "This link should open a small window w/o decorations";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2524;
    }

    @Override
    protected void setup() {
        Link l = new Link("Try it!", new ExternalResource(
                "http://www.google.com/m"));
        l.setTargetName("_blank");
        l.setTargetWidth(300);
        l.setTargetHeight(300);
        l.setTargetBorder(BorderStyle.NONE);
        addComponent(l);
    }

}
