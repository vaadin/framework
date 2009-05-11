package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket2062 extends Application {
    private static final Object P1 = new Object();

    @Override
    public void init() {
        setMainWindow(new Window("Ticket2062"));
        getMainWindow().setSizeFull();

        SplitPanel p = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        p.setSizeFull();
        getMainWindow().setLayout(p);

        TextField tf1 = new TextField("Tab 1");
        tf1.setValue("Field 1");
        tf1.setSizeFull();

        Table t = new Table("Table");
        t.addContainerProperty(P1, String.class, "");
        t.setSizeFull();

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("300px");
        tabSheet.setHeight("300px");

        tabSheet.addComponent(tf1);
        tabSheet.addComponent(t);

        getMainWindow().addComponent(tabSheet);

    }

}