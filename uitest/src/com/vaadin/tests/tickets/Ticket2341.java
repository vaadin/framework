package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class Ticket2341 extends com.vaadin.server.LegacyApplication {
    @Override
    public void init() {
        LegacyWindow main = new LegacyWindow();
        setMainWindow(main);
        constructTables((Layout) main.getContent());
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
