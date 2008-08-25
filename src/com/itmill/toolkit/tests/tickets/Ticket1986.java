package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.ListSelect;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.TwinColSelect;
import com.itmill.toolkit.ui.Window;

public class Ticket1986 extends Application {

    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);

        int index = 1;

        GridLayout layout = new GridLayout(2, 2);
        TextField f1 = new TextField("1");
        f1.setTabIndex(index++);
        TextField f2 = new TextField("2");
        f2.setTabIndex(index++);

        DateField f3 = new DateField("3");
        f3.setTabIndex(index++);
        ComboBox cb = new ComboBox("4");
        cb.setTabIndex(index++);

        ListSelect lss = new ListSelect("5");
        lss.addItem("foo");
        lss.addItem("Bar");
        lss.setTabIndex(index++);

        NativeSelect ns = new NativeSelect("6");
        ns.addItem("foo");
        ns.addItem("bar");
        ns.setTabIndex(index++);

        OptionGroup og = new OptionGroup("7");
        og.addItem("foo");
        og.addItem("bar");
        og.setTabIndex(index++);

        OptionGroup ogm = new OptionGroup("7");
        ogm.setMultiSelect(true);
        ogm.addItem("foo");
        ogm.addItem("bar");
        ogm.setTabIndex(index++);

        TwinColSelect ts = new TwinColSelect("8");
        ts.addItem("Foo");
        ts.addItem("Bar");
        ts.setTabIndex(index++);

        Button b = new Button("9");
        b.setTabIndex(index++);

        layout.addComponent(b);
        layout.addComponent(ts);
        layout.addComponent(ogm);
        layout.addComponent(og);
        layout.addComponent(ns);
        layout.addComponent(lss);
        layout.addComponent(cb);
        layout.addComponent(f3);
        layout.addComponent(f2);
        layout.addComponent(f1);

        w.setLayout(layout);

    }

}
