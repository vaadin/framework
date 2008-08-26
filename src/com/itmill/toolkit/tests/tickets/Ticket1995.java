package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Container.Filterable;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1995 extends Application {

    private static final Object PROPERTY_1 = "Test";
    private Table table;

    public void init() {
        final Window mainWin = new Window(getClass().getName());
        setMainWindow(mainWin);

        table = new Table();
        table.addContainerProperty(PROPERTY_1, String.class, "");
        table.setPageLength(4);

        Item item = table.addItem("1");
        item.getItemProperty(PROPERTY_1).setValue("Row 1");
        item = table.addItem("2");
        item.getItemProperty(PROPERTY_1).setValue("Row 2");

        Filterable filterable = (Container.Filterable) table
                .getContainerDataSource();
        filterable.addContainerFilter(PROPERTY_1, "Row", true, false);

        table.setColumnHeader(PROPERTY_1, "Test (filter: Row)");

        mainWin.addComponent(table);
        mainWin.addComponent(new Button("Add item",
                new com.itmill.toolkit.ui.Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        addItem();
                    }
                }));
    }

    protected void addItem() {
        Filterable filterable = (Container.Filterable) table
                .getContainerDataSource();

        Item i = table.addItem("abc");
        String res = "";
        if (i == null) {
            res = "FAILED";
        } else {
            res = "OK!";
        }

        getMainWindow().showNotification("Tried to add item 'abc', " + res);

        filterable.addContainerFilter(PROPERTY_1, "Row", true, false);

    }
}
