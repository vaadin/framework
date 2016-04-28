package com.vaadin.tests.tickets;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class Ticket1995 extends LegacyApplication {

    private static final Object PROPERTY_1 = "Test";
    private Table table;

    @Override
    public void init() {
        final LegacyWindow mainWin = new LegacyWindow(getClass().getName());
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
        filterable.addContainerFilter(new SimpleStringFilter(PROPERTY_1, "Row",
                true, false));

        table.setColumnHeader(PROPERTY_1, "Test (filter: Row)");

        mainWin.addComponent(table);
        mainWin.addComponent(new Button("Add item",
                new com.vaadin.ui.Button.ClickListener() {

                    @Override
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

        filterable.addContainerFilter(new SimpleStringFilter(PROPERTY_1, "Row",
                true, false));

    }
}
