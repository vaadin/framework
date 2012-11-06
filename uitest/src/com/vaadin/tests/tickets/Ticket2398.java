package com.vaadin.tests.tickets;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class Ticket2398 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow();
        setMainWindow(mainWin);

        Table t = new Table();

        IndexedContainer c = new IndexedContainer();

        c.addItem("foo");

        c.addContainerProperty("testcol1", Integer.class, new Integer(7));
        c.addContainerProperty("testcol2", String.class, "str");
        c.addContainerProperty("testcol3", String.class, null);

        c.addItem("bar");

        t.setContainerDataSource(c);

        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);

        mainWin.addComponent(new Label(
                "Both rows in table should have same data (default values)"));

        mainWin.addComponent(t);

    }

}
