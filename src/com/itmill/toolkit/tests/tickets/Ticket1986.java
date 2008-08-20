package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1986 extends Application {

    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);

        GridLayout layout = new GridLayout(2, 2);
        TextField f1 = new TextField("1");
        int index = 1;
        f1.setTabIndex(index++);
        TextField f2 = new TextField("2");
        f2.setTabIndex(index++);

        DateField f3 = new DateField("3");
        f3.setTabIndex(index++);
        ComboBox cb = new ComboBox("4");
        cb.setTabIndex(index++);

        layout.addComponent(cb);
        layout.addComponent(f3);
        layout.addComponent(f2);
        layout.addComponent(f1);

        w.setLayout(layout);

    }

}
