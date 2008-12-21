package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket2398 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window();
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
