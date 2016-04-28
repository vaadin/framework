package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class ColumnExpandRatio extends TestBase {

    @Override
    protected String getDescription() {
        return "Column expand ratios can be used to adjust the way "
                + "how excess horizontal space is divided among columns.";

    }

    @Override
    protected Integer getTicketNumber() {
        return 2806;
    }

    private static final int ROWS = 100;

    @Override
    public void setup() {
        Table table1 = initTable();
        addComponent(new Label("Plain table"));
        addComponent(table1);

    }

    private Table initTable() {
        Table table = new Table();
        table.setWidth("100%");

        IndexedContainer idx = new IndexedContainer();
        idx.addContainerProperty("firstname", String.class, null);
        idx.addContainerProperty("lastname", String.class, null);
        Item i = idx.addItem(1);
        i.getItemProperty("firstname").setValue("John");
        i.getItemProperty("lastname").setValue("Johnson");

        i = idx.addItem(2);
        i.getItemProperty("firstname").setValue("Jane");
        i.getItemProperty("lastname").setValue("Janeine");

        for (int index = 3; index < ROWS; index++) {
            i = idx.addItem(index);
            i.getItemProperty("firstname").setValue("Jane");
            i.getItemProperty("lastname").setValue("Janeine");
        }

        idx.addContainerProperty("fixed 50px column", String.class, "");

        idx.addContainerProperty("Expanded with 2", String.class, "foobar");

        table.setContainerDataSource(idx);

        table.setColumnHeader("firstname", "FirstName");
        table.setColumnHeader("lastname", "LastName (1)");

        table.setColumnWidth("fixed 50px column", 50);
        table.setColumnExpandRatio("Expanded with 2", 2);
        table.setColumnExpandRatio("lastname", 1);

        return table;
    }

}
