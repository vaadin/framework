package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Root;

public class Ticket2245 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root main = new Root("The Main Window");
        main.getContent().setSizeFull();
        setMainWindow(main);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        main.addComponent(sp);
    }
}
