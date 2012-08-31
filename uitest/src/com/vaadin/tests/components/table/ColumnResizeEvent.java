package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnResizeListener;

@SuppressWarnings("serial")
public class ColumnResizeEvent extends TestBase {

    private Label column1Width = new Label("Undefined");
    private Label column2Width = new Label("Undefined");
    private Label column3Width = new Label("Undefined");

    @Override
    protected void setup() {

        HorizontalLayout widths = new HorizontalLayout();
        widths.setSpacing(true);
        widths.setWidth("50%");

        column1Width.setCaption("Column 1 width");
        widths.addComponent(column1Width);

        column2Width.setCaption("Column 2 width");
        widths.addComponent(column2Width);

        column3Width.setCaption("Column 3 width");
        widths.addComponent(column3Width);

        addComponent(widths);

        Table table1 = initTable();
        addComponent(table1);

    }

    @Override
    protected String getDescription() {
        return "Table should update column size back to server";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2807;
    }

    private static final int ROWS = 100;

    private Table initTable() {
        Table table = new Table();
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

        idx.addContainerProperty("150pxfixedCol", String.class, "foobar");

        table.setContainerDataSource(idx);

        table.setColumnHeader("firstname", "FirstName");
        table.setColumnHeader("lastname", "LastName with long header");

        table.setColumnWidth("150pxfixedCol", 150);
        column3Width.setValue(table.getColumnWidth("150pxfixedCol") + "px");

        table.addListener(new ColumnResizeListener() {
            @Override
            public void columnResize(com.vaadin.ui.Table.ColumnResizeEvent event) {

                if (event.getPropertyId().equals("firstname")) {
                    column1Width.setValue(event.getCurrentWidth()
                            + "px (previously " + event.getPreviousWidth()
                            + "px)");
                } else if (event.getPropertyId().equals("lastname")) {
                    column2Width.setValue(event.getCurrentWidth()
                            + "px (previously " + event.getPreviousWidth()
                            + "px)");
                } else if (event.getPropertyId().equals("150pxfixedCol")) {
                    column3Width.setValue(event.getCurrentWidth()
                            + "px (previously " + event.getPreviousWidth()
                            + "px)");
                }
            }
        });

        return table;
    }

}
