package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

/**
 * Setting table height and setting column header mode as hidden leaves the body
 * height of the table as it would be with the headers visible and leaves an
 * empty area below the body.
 * 
 */
@SuppressWarnings("serial")
public class TableHeightWhenHidingHeaders extends AbstractTestCase {

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow();
        setMainWindow(mainWindow);

        final Table table = new Table("Test table");
        table.addContainerProperty("Name", String.class, null, "Name", null,
                null);
        table.setItemCaptionPropertyId("Name");
        table.setHeight("100px");
        table.setImmediate(true);

        table.addItem("1").getItemProperty("Name").setValue("Item 1");
        table.addItem("2").getItemProperty("Name").setValue("Item 2");

        CheckBox showHeaders = new CheckBox("Show headers");
        showHeaders.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if ((Boolean) event.getProperty().getValue()) {
                    // table body height is now 77px, which together
                    // with header makes 100px
                    table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID);
                } else {
                    table.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
                    // header disappears, but table body height stays at
                    // 77px
                    // and below the body is an empty area (same height
                    // as header would
                    // have)

                }
            }
        });
        showHeaders.setValue(true);
        showHeaders.setImmediate(true);

        mainWindow.addComponent(showHeaders);
        mainWindow.addComponent(table);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 4507;
    }
}
