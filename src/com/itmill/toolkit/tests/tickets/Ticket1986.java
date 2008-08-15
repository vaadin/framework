package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1986 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout layout = new GridLayout(2, 2);
        TextField f1 = new TextField("1");
        int index = 0;
        f1.setTabIndex(index++);
        TextField f2 = new TextField("2");
        f2.setTabIndex(index++);
        TextField f3 = new TextField("3");
        f3.setTabIndex(index++);
        TextField f4 = new TextField("4");
        f4.setTabIndex(index++);

        layout.addComponent(f4);
        layout.addComponent(f3);
        layout.addComponent(f2);
        layout.addComponent(f1);

        w.setLayout(layout);

    }

}
