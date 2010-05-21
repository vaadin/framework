package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class TableHeaderShifting extends TestBase {

    // COLS = 200; still ok
    // COLS = 210; header width begins shifting
    // COLS = 230; header text disappears
    static final int COLS = 210;

    @Override
    protected String getDescription() {
        return "The table header starts shifting when adding lots of columns";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5022;
    }

    @Override
    protected void setup() {
        final Table t = new Table();
        t.setSizeFull();
        t.addContainerProperty("name", String.class, "NA");

        for (Integer i = 0; i < COLS; i++) {
            t.addContainerProperty(i, Integer.class, Integer.valueOf(0));
        }
        t.addItem("1").getItemProperty("name").setValue("Ares");
        t.addItem("2").getItemProperty("name").setValue("Bob");
        t.addItem("3").getItemProperty("name").setValue("Coral");
        t.addItem("4").getItemProperty("name").setValue("David");
        t.addItem("5").getItemProperty("name").setValue("Emma");

        addComponent(t);
    }
}
