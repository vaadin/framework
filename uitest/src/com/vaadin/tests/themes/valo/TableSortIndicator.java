package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.*;
import com.vaadin.server.*;
import com.vaadin.tests.components.*;
import com.vaadin.ui.*;

@Theme("valo")
public class TableSortIndicator extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.addContainerProperty("Index", Integer.class, "");

        for (int i = 0; i < 10; i++) {
            table.addItem(new Object[] { i }, i);
        }

        table.setPageLength(0);

        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "For Valo, sorting indicators should point up when sorted asc "
                + "and down when sorted desc.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15123;
    }
}
