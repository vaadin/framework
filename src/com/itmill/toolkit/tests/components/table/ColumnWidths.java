package com.itmill.toolkit.tests.components.table;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;

public class ColumnWidths extends TestBase {

    @Override
    protected String getDescription() {
        return "On window resize undefined "
                + "columns (by server or user (dragged)) columns "
                + "must consume the excess space. Space is divided "
                + "by default according to natural widths of columns."
                + " Lastname should get 1/3 of excess spsace, the las column 2/3.";

    }

    @Override
    protected Integer getTicketNumber() {
        return 2804;
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

        idx.addContainerProperty("150pxfixedCol", String.class, "foobar");

        table.setContainerDataSource(idx);

        table.setColumnHeader("firstname", "FirstName");
        table.setColumnHeader("lastname", "LastName");

        table.setColumnWidth("150pxfixedCol", 150);

        return table;
    }

}
