package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class TableHeaderZoom extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setHeight("400px");
        table.setWidth("400px");
        table.addContainerProperty("Column 1", String.class, "");
        table.addContainerProperty("Column 2", String.class, "");

        for (int i = 0; i < 100; ++i) {
            table.addItem(new Object[] { "" + i, "foo" }, i);
        }

        LegacyWindow main = getMainWindow();
        main.setContent(new CssLayout());
        main.addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Table header text/icon disappears when zooming out";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6870;
    }
}
