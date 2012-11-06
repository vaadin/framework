package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class Ticket2051 extends LegacyApplication {

    private static final Object P1 = new Object();
    private static final Object P2 = new Object();

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
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
