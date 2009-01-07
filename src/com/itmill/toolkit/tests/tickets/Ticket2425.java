package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;

public class Ticket2425 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        w.addComponent(new Label("No scrollbars should be visible anywhere"));
        TabSheet ts = new TabSheet();
        ts.addTab(new Panel(), "Panel 1", null);
        ts.addTab(new Panel(), "Panel 2", null);

        w.addComponent(ts);
    }

}
