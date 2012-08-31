package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ColumnReorderEvent extends TestBase {

    private Label order = new Label("Column order");

    @Override
    protected void setup() {

        HorizontalLayout widths = new HorizontalLayout();
        widths.setSpacing(true);
        widths.setWidth("50%");

        order.setCaption("Column 1 width");
        widths.addComponent(order);

        addComponent(widths);

        Table table1 = initTable();
        addComponent(table1);

        order.setValue(aToString(table1.getVisibleColumns()));

    }

    private String aToString(Object[] visibleColumns) {
        StringBuilder sb = new StringBuilder();
        for (Object object : visibleColumns) {
            sb.append(object.toString());
            sb.append(" | ");
        }
        return sb.toString();
    }

    @Override
    protected String getDescription() {
        return "Test ColumnReorderEvents";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6283;
    }

    private static final int ROWS = 100;

    private Table initTable() {
        final Table table = new Table();
        table.setWidth("100%");
        table.setImmediate(true);

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

        idx.addContainerProperty("property", String.class, "foobar");

        table.setContainerDataSource(idx);

        table.setColumnHeader("firstname", "FirstName");
        table.setColumnHeader("lastname", "LastName");

        table.addListener(new Table.ColumnReorderListener() {
            @Override
            public void columnReorder(
                    com.vaadin.ui.Table.ColumnReorderEvent event) {
                order.setValue(aToString(table.getVisibleColumns()));
            }
        });

        table.setColumnReorderingAllowed(true);

        return table;
    }

}
