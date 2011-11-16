package com.vaadin.tests.components.embedded;

import com.vaadin.terminal.ClassResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

public class EmbeddedPdf extends TestBase {

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
        player.setType(Embedded.TYPE_BROWSER);
        player.setWidth("400px");
        player.setHeight("300px");
        player.setSource(new ClassResource(getClass(), "test.pdf", this));
        addComponent(player);

        player.getRoot().addWindow(new Window("Testwindow"));
    }

}
