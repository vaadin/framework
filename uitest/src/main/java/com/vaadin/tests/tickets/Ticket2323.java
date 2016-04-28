package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2323 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window subWindow = new Window("", layout);
        subWindow.setSizeUndefined();
        subWindow.getContent().setSizeUndefined();
        subWindow.center();
        subWindow.setContent(new RichTextArea());
        w.addWindow(subWindow);
    }

}
