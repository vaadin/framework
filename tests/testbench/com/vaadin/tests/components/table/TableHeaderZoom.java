package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class TableHeaderZoom extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setHeight("100px");
        table.setWidth("200px");
        table.setEnabled(false);
        table.addContainerProperty("Column 1", String.class, "");

        Window main = getMainWindow();
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
