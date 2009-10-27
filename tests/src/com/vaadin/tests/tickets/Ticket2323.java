package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Window;

public class Ticket2323 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        Window subWindow = new Window("");
        subWindow.setSizeUndefined();
        subWindow.getLayout().setSizeUndefined();
        subWindow.center();
        subWindow.addComponent(new RichTextArea());
        w.addWindow(subWindow);
    }

}
