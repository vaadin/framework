package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.LegacyWindow;

public class Ticket2245 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow("The Main Window");
        main.getContent().setSizeFull();
        setMainWindow(main);
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        main.addComponent(sp);
    }
}
