package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2062 extends Application {
    private static final Object P1 = new Object();

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