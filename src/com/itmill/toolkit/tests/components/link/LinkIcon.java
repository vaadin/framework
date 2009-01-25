package com.itmill.toolkit.tests.components.link;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Link;

public class LinkIcon extends TestBase {

    @Override
    protected String getDescription() {
        return "The icon of a Link component should have the same cursor as the text and should be clickable";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setup() {
        Link l = new Link("www.google.com", new ExternalResource(
                "http://www.itmill.com/"));
        l.setIcon(new ThemeResource("icons/32/calendar.png"));

        addComponent(l);
    }

}
