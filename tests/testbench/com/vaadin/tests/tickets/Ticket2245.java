package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Window;

public class Ticket2245 extends Application {

    @Override
    public void init() {
        Window main = new Window("The Main Window");
        main.getContent().setSizeFull();
        setMainWindow(main);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        main.addComponent(sp);
    }
}
