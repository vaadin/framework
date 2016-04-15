package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class TableWithManyColumns extends TestBase {

    private static final int ROWS = 20;
    private static final int COLS = 100;

    @Override
    protected void setup() {
        Table t = new Table();

        for (int i = 0; i < COLS; i++) {
            t.addContainerProperty("COLUMN_" + i, String.class, "");
        }
        for (int row = 0; row < ROWS; row++) {
            Item i = t.addItem(String.valueOf(row));
            for (int col = 0; col < COLS; col++) {
                Property<String> p = i.getItemProperty("COLUMN_" + col);
                p.setValue("item " + row + "/" + col);
            }
        }
        t.setFooterVisible(true);
        t.setSizeFull();
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        return "The footer, header and content cells should be as wide, even when the Table contains many columns";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5185;
    }

}
