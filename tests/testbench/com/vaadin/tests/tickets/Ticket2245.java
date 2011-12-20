package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Root.LegacyWindow;

public class Ticket2245 extends Application.LegacyApplication {

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow("The Main Window");
        main.getContent().setSizeFull();
        setMainWindow(main);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        main.addComponent(sp);
    }
}
