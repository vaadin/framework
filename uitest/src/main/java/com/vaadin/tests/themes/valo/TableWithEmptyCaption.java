package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Table;

public class TableWithEmptyCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.addContainerProperty("first", String.class, "");
        table.addContainerProperty("last", String.class, "");
        table.addContainerProperty("actions", Component.class, null);

        table.addItem(new Object[] { "Teemu", "Test", new Button("Edit") }, 1);
        table.addItem(new Object[] { "Dummy", "Test", new Button("Edit") }, 2);

        table.setPageLength(0);
        table.setColumnHeaders("First Name", "Last Name", "");

        table.setFooterVisible(true);
        table.setColumnFooter("first", "Footer");
        table.setColumnFooter("last", "");
        table.setColumnFooter("actions", "");
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Test that column headers (and footers) work properly with empty captions.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14812;
    }
}
