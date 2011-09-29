package com.vaadin.tests.components.embedded;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;

public class FlashIsVisible extends TestBase {

    @Override
    protected void setup() {
        Embedded player = new Embedded();
        player.setType(Embedded.TYPE_OBJECT);
        player.setMimeType("application/x-shockwave-flash");
        player.setWidth("400px");
        player.setHeight("300px");
        String url = "http://www.youtube.com/v/qQ9N742QB4g&autoplay=0";
        player.setSource(new ExternalResource(url));
        addComponent(player);
    }

    @Override
    protected String getDescription() {
        return "Flash plugin should load and be visible on all browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6501;
    }

}
