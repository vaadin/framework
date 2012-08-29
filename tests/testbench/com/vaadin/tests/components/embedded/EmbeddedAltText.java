package com.vaadin.tests.components.embedded;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;

public class EmbeddedAltText extends TestBase {
    @Override
    protected String getDescription() {
        return "It should be possible to set an alternative text on an image or object.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2853;
    }

    @Override
    protected void setup() {
        final Embedded e = new Embedded("Caption", new ThemeResource(
                "../runo/icons/64/ok.png"));
        e.setAlternateText("Alt text of the image");
        addComponent(e);
        final Embedded player = new Embedded();
        player.setAlternateText("Alt text of the object");
        player.setType(Embedded.TYPE_OBJECT);
        player.setWidth("400px");
        player.setHeight("300px");
        player.setMimeType("application/x-shockwave-flash");
        String url = "http://www.youtube.com/v/qQ9N742QB4g&autoplay=1";
        player.setSource(new ExternalResource(url));
        player.setParameter("movie", "someRandomValue");
        player.setParameter("allowFullScreen", "true");
        addComponent(player);

        Button changeAltTexts = new Button("Change alt texts",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        e.setAlternateText("New alt text of the image!");
                        player.setAlternateText("New alt text of the object!");
                    }
                });
        addComponent(changeAltTexts);
    }
}
