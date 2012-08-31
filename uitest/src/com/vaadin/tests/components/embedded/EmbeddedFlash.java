package com.vaadin.tests.components.embedded;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;

public class EmbeddedFlash extends TestBase {

    @Override
    protected String getDescription() {
        return "The embedded flash should have the movie parameter set to \"someRandomValue\" and an allowFullScreen parameter set to \"true\".";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3367;
    }

    @Override
    public void setup() {
        Embedded player = new Embedded();
        player.setType(Embedded.TYPE_OBJECT);
        player.setWidth("400px");
        player.setHeight("300px");
        player.setMimeType("application/x-shockwave-flash");
        String url = "http://www.youtube.com/v/qQ9N742QB4g&autoplay=1";
        player.setSource(new ExternalResource(url));
        player.setParameter("movie", "someRandomValue");
        player.setParameter("allowFullScreen", "true");

        addComponent(player);
    }

}
