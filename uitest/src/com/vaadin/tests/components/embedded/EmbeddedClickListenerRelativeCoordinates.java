package com.vaadin.tests.components.embedded;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;

public class EmbeddedClickListenerRelativeCoordinates extends TestBase {

    @Override
    protected void setup() {
        Embedded e = new Embedded("Embedded caption", new ThemeResource(
                "../runo/icons/64/ok.png"));
        e.addListener(new ClickListener() {

            @Override
            public void click(ClickEvent event) {
                getMainWindow()
                        .showNotification(
                                "" + event.getRelativeX() + ", "
                                        + event.getRelativeY());
            }
        });
        addComponent(e);
    }

    @Override
    protected String getDescription() {
        return "Click the image to get coordinates relative to the top-left corder of the embedded image.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
