package com.itmill.toolkit.tests.components.link;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Link;

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
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        addComponent(l);
    }

}
