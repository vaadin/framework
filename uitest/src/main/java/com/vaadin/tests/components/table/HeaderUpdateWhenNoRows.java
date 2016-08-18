package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnHeaderMode;

public class HeaderUpdateWhenNoRows extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table("Test table");
        table.addContainerProperty("Name", String.class, null, "Name", null,
                null);
        table.setItemCaptionPropertyId("Name");
        table.setHeight("100px");
        table.setImmediate(true);

        final CheckBox showHeaders = new CheckBox("Show headers");
        showHeaders.addValueChangeListener(event -> {
            if (showHeaders.getValue()) {
                table.setColumnHeaderMode(
                        ColumnHeaderMode.EXPLICIT_DEFAULTS_ID);
            } else {
                table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
            }
        });

        showHeaders.setImmediate(true);
        showHeaders.setValue(true);

        addComponent(showHeaders);
        addComponent(table);
    }

    @Override
    public String getDescription() {
        return "The header should be updated when toggling column header mode";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2974;
    }

}
