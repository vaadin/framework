package com.vaadin.tests.components.embedded;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;

public class EmbeddedTooltip extends TestBase {

    @Override
    protected String getDescription() {
        return "The tooltip for an Embedded image should be visible also when hovering the image";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2853;
    }

    @Override
    protected void setup() {
        Embedded e = new Embedded("Embedded caption", new ThemeResource(
                "../runo/icons/64/ok.png"));
        e.setDescription("Embedded tooltip, only shown on caption, not on the image");
        addComponent(e);

    }
}
