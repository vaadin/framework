package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Table;

public class TableRepaintWhenMadeVisibile extends TestBase {

    @Override
    public void setup() {
        final Table table = new Table();
        table.addContainerProperty("sth", String.class, null);
        table.addItem(new Object[] { "something" }, 1);
        addComponent(table);

        Button show = new Button("show", event -> table.setVisible(true));
        addComponent(show);
        Button hide = new Button("hide", event -> table.setVisible(false));
        addComponent(hide);
    }

    @Override
    protected String getDescription() {
        return "A Table should be rendered correctly when made visible again after being initially rendered invisible. Click 'hide', refresh the application and then click 'show'";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7986;
    }
}
