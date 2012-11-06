package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnGenerator;

public class Ticket2208 extends LegacyApplication {

    private Table t;

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow();
        setMainWindow(mainWindow);

        t = new Table("A table");
        t.addContainerProperty("col 1 (red)", String.class, "");
        t.addContainerProperty("col 2", String.class, "");

        t.setHeight("150px");
        t.addGeneratedColumn("col 3 (green)", new ColumnGenerator() {

            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Item item = source.getItem(itemId);
                String col1 = (String) item.getItemProperty("col 1 (red)")
                        .getValue();
                String col2 = (String) item.getItemProperty("col 2").getValue();
                return new Label(col1 + "-" + col2);
            }
        });

        t.addContainerProperty("col 4", String.class, "");
        t.setCellStyleGenerator(new CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId,
                    Object propertyId) {
                if ("col 1 (red)".equals(propertyId)) {
                    return "red";
                }

                if ("col 3 (green)".equals(propertyId)) {
                    return "green";
                }

                return null;
            }
        });

        t.addItem(new Object[] { "Col 1-1", "Col 2-1", "Col 4-1" },
                new Object());
        t.addItem(new Object[] { "Col 1-2", "Col 2-2", "Col 4-2" },
                new Object());
        t.addItem(new Object[] { "Col 1-3", "Col 2-3", "Col 4-3" },
                new Object());

        t.setColumnReorderingAllowed(true);
        t.setColumnCollapsingAllowed(true);
        setTheme("tests-tickets");
        mainWindow.addComponent(t);

    }

}
