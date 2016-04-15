package com.vaadin.tests.tickets;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnGenerator;

public class Ticket2125 extends LegacyApplication {

    @Override
    public void init() {
        setMainWindow(new MainWindow("Ticket2125"));

    }

    class MainWindow extends LegacyWindow {
        MainWindow(String caption) {
            super(caption);

            addComponent(new Label(
                    "Inspect w/ Firebug: row 5 should have a MYROW -style on the row, and MYCELL on all cells"));

            Table table = new Table();
            table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
            addComponent(table);
            for (int i = 0; i < 50; i++) {
                table.addItem(new Integer(i));
            }
            table.addContainerProperty("String", String.class, "a string");
            table.addContainerProperty("Boolean", Boolean.class, Boolean.TRUE);
            table.addGeneratedColumn("Generated", new ColumnGenerator() {
                @Override
                public Component generateCell(Table source, Object itemId,
                        Object columnId) {
                    return new Label("Item " + itemId);
                }
            });
            table.setCellStyleGenerator(new CellStyleGenerator() {
                @Override
                public String getStyle(Table source, Object itemId,
                        Object propertyId) {
                    if (new Integer(4).equals(itemId)) {
                        if (propertyId == null) {
                            return "MYROW";
                        } else {
                            return "MYCELL";
                        }
                    }
                    return null;
                }

            });
            CheckBox b = new CheckBox("editmode", new MethodProperty<Boolean>(
                    table, "editable"));
            b.setImmediate(true);
            addComponent(b);
        }
    }

}
