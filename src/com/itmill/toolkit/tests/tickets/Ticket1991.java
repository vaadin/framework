package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket1991 extends com.itmill.toolkit.Application {

    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        Table t = new Table("Test table");

        t.addContainerProperty(" ", CheckBox.class, "");
        t.addContainerProperty("Col1", String.class, "");
        t.addContainerProperty("Col2", String.class, "");

        t.setPageLength(5);

        t.addItem(new Object[] { new CheckBox(), "Foo", "Bar" }, "1");
        t.addItem(new Object[] { new CheckBox(), "Foo", "Bar" }, "2");

        main.addComponent(t);
    }
}