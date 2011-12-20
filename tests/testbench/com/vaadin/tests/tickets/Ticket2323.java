package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.Window;

public class Ticket2323 extends Application.LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        Window subWindow = new Window("");
        subWindow.setSizeUndefined();
        subWindow.getContent().setSizeUndefined();
        subWindow.center();
        subWindow.addComponent(new RichTextArea());
        w.addWindow(subWindow);
    }

}
