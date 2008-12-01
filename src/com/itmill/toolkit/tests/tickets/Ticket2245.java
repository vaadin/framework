package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Window;

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
