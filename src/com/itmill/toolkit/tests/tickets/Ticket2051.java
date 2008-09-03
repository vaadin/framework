package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2051 extends Application {

    private static final Object P1 = new Object();
    private static final Object P2 = new Object();

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Table t = new Table("This is a table");
        t.addContainerProperty(P1, Component.class, null);
        t.addContainerProperty(P2, Component.class, null);
        t.setColumnHeaders(new String[] { "Col1", "Col2" });

        Item i = t.addItem("1");
        i.getItemProperty(P1).setValue(new TextField("abc"));
        i.getItemProperty(P2).setValue(new Label("label"));
        Item i2 = t.addItem("2");
        i2.getItemProperty(P1).setValue(new Button("def"));
        i2.getItemProperty(P2).setValue(new DateField());

        layout.addComponent(t);
    }
}
