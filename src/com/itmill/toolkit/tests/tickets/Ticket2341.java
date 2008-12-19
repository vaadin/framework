package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket2341 extends com.itmill.toolkit.Application {
    public void init() {
        Window main = new Window();
        setMainWindow(main);
        constructTables(main.getLayout());
    }

    private void constructTables(Layout layout) {

        Table t = createTable();
        layout.addComponent(t);
        t = createTable();
        Label l = new Label("A high label to enable scrollbars");
        l.setHeight("2000px");
        layout.addComponent(l);

    }

    private Table createTable() {
        Table t = new Table();
        t.addContainerProperty("test1", String.class, "");
        t.addContainerProperty("test2", String.class, "");
        t.addContainerProperty("test3", String.class, "");
        t.addContainerProperty("test4", String.class, "");
        t.setWidth("100%");
        t.setHeight("300px");
        for (int i = 0; i < 100; i++) {
            Item item = t.addItem(i);
            item.getItemProperty("test1").setValue("testing1 " + i);
            item.getItemProperty("test2").setValue("testing2 " + i);
            item.getItemProperty("test3").setValue("testing3 " + i);
            item.getItemProperty("test4").setValue("testing4 " + i);
        }

        return t;
    }

}
