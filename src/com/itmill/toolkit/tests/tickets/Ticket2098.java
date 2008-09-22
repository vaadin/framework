package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;

public class Ticket2098 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        TabSheet ts = new TabSheet();
        Label l1 = new Label("111");
        Label l2 = new Label("222");
        Label l3 = new Label("333");
        Label l4 = new Label("444");

        ts.addTab(l1, "1", null);
        ts.addTab(l2, "2", null);
        ts.addTab(l3, "3", null);
        ts.addTab(l4, "4", null);

        l1.setVisible(false);
        ts.setSelectedTab(l3);

        layout.addComponent(ts);
    }
}
