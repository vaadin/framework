package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;

public class HeaderUpdateWhenNoRows extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table("Test table");
        table.addContainerProperty("Name", String.class, null, "Name", null,
                null);
        table.setItemCaptionPropertyId("Name");
        table.setHeight("100px");
        table.setImmediate(true);

        final CheckBox showHeaders = new CheckBox("Show headers");
        showHeaders.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (showHeaders.getValue()) {
                    table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID);
                } else {
                    table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
                }
            }
        });

        showHeaders.setImmediate(true);
        showHeaders.setValue(true);

        addComponent(showHeaders);
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "The header should be updated when toggling column header mode";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2974;
    }

}
