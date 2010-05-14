package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Setting table height and setting column header mode as hidden leaves the body
 * height of the table as it would be with the headers visible and leaves an
 * empty area below the body.
 * 
 */
@SuppressWarnings("serial")
public class Ticket4507 extends Application {

    @Override
    public void init() {
        Window mainWindow = new Window();
        setMainWindow(mainWindow);

        final Table table = new Table("Test table");
        table.addContainerProperty("Name", String.class, null, "Name", null,
                null);
        table.setItemCaptionPropertyId("Name");
        table.setHeight("100px");
        table.setImmediate(true);

        table.addItem("1").getItemProperty("Name").setValue("Item 1");
        table.addItem("2").getItemProperty("Name").setValue("Item 2");

        CheckBox showHeaders = new CheckBox("Show headers",
                new CheckBox.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (event.getButton().booleanValue()) {
                            // table body height is now 77px, which together
                            // with header makes 100px
                            table
                                    .setColumnHeaderMode(Table.COLUMN_HEADER_MODE_EXPLICIT_DEFAULTS_ID);
                        } else {
                            table
                                    .setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
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
}
