package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class ColumnCollapsingAndColumnExpansion extends TestBase {

    @Override
    public void setup() {

        Table t = new Table();

        t.addContainerProperty("Col1", String.class, null);
        t.addContainerProperty("Col2", String.class, null);
        t.addContainerProperty("Col3", String.class, null);
        t.setColumnCollapsingAllowed(true);

        t.setSizeFull();

        for (int y = 1; y < 5; y++) {
            t.addItem(new Object[] { "cell " + 1 + "-" + y,
                    "cell " + 2 + "-" + y, "cell " + 3 + "-" + y, },
                    new Object());
        }

        addComponent(t);

    }

    @Override
    protected String getDescription() {
        return "After hiding column 2 the remaining columns (1 and 3) should use all available space in the table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3246;
    }
}
