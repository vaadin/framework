package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Table.CellStyleGenerator;
import com.itmill.toolkit.ui.Table.ColumnGenerator;

public class Ticket2125 extends Application {

    @Override
    public void init() {
        setMainWindow(new MainWindow("Ticket2125"));

    }

    class MainWindow extends Window {
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
                public Component generateCell(Table source, Object itemId,
                        Object columnId) {
                    return new Label("Item " + itemId);
                }
            });
            table.setCellStyleGenerator(new CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {
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
            Button b = new Button("editmode", new MethodProperty(table,
                    "editable"));
            b.setImmediate(true);
            addComponent(b);
        }
    }

}
