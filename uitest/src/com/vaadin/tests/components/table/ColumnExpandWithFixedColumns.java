package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class ColumnExpandWithFixedColumns extends TestBase {

    private Table createTable() {
        Table t = new Table();
        t.addContainerProperty("id", Integer.class, null);
        t.addContainerProperty("txt", Component.class, null);
        t.addContainerProperty("button", Button.class, null);
        t.setColumnWidth("id", 30);
        t.setColumnWidth("button", 200);
        t.setColumnExpandRatio("txt", 10);// This column should be 400px wide.
        t.setSelectable(true);
        t.setSizeFull();

        for (int i = 0; i < 10; i++) {
            t.addItem(new Object[] { i, new Label("test " + i),
                    new Button("Button " + i) }, i);
        }

        return t;

    }

    @Override
    protected String getDescription() {
        return "The second column has expand ratio and should use the maximum available space";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3143;
    }

    @Override
    protected void setup() {
        addComponent(createTable());
    }
}
