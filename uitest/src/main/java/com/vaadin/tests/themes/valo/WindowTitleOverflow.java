package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Window;

public class WindowTitleOverflow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Open Resizable", event -> addWindow(getWindow(true, false)));

        addButton("Open Closable", event -> addWindow(getWindow(false, true)));

        addButton("Open Resizable and Closable",
                event -> addWindow(getWindow(true, true)));

        addButton("Open Non-Resizable and Non-Closable",
                event -> addWindow(getWindow(false, false)));
    }

    private Window getWindow(boolean resizable, boolean closable) {
        Window window = new Window();

        window.setModal(true);
        window.setResizable(resizable);
        window.setClosable(closable);
        window.setCaption("Long Foobar Foobar Foobar Foobar Foobar Foobar");

        return window;
    }

    @Override
    protected Integer getTicketNumber() {
        return 15408;
    }

    @Override
    protected String getTestDescription() {
        return "In Valo, header title should use the space of hidden buttons.";
    }
}
