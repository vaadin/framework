package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class WideSelectableTable extends TestBase {

    @Override
    protected void setup() {
        final int NUMBER_OF_COLS = 50;

        // mainWindow.setSizeFull();
        // setMainWindow(mainWindow);

        Table ptable = new Table();
        for (int colcount = 0; colcount < NUMBER_OF_COLS; colcount++) {
            String col = "COL_" + colcount + "";
            ptable.addContainerProperty(col, String.class, "--");
            ptable.addItem(colcount + "-").getItemProperty(col)
                    .setValue("--" + colcount + "");
        }
        ptable.setSelectable(true);
        ptable.setMultiSelect(true);
        ptable.setColumnReorderingAllowed(false);
        ptable.setImmediate(true);

        ptable.setWidth("100%");
        ptable.setPageLength(5);

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(ptable);
        addComponent(vl);
    }

    @Override
    protected String getDescription() {
        return "A wide table scrolls to the beginning when sorting a column at  the beginning when sorting a column at the end";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6788;
    }
}
