package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.Window;

public class Ticket2245 extends Application {

    @Override
    public void init() {
        Window main = new Window("The Main Window");
        main.getLayout().setSizeFull();
        setMainWindow(main);
        SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);
        main.addComponent(sp);
    }
}
