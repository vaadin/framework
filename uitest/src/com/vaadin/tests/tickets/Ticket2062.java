package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class Ticket2062 extends LegacyApplication {
    private static final Object P1 = new Object();

    @Override
    public void init() {
        setMainWindow(new LegacyWindow("Ticket2062"));
        getMainWindow().setSizeFull();

        HorizontalSplitPanel p = new HorizontalSplitPanel();
        p.setSizeFull();
        getMainWindow().setContent(p);

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
