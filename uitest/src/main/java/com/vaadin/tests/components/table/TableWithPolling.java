package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table;

public class TableWithPolling extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "Polling shouldn't affect table column resizing in any way.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13432;
    }

    @Override
    protected void setup(VaadinRequest request) {

        Table table = new Table("This is my Table");

        table.addContainerProperty("First Name", String.class, null);
        table.addContainerProperty("Last Name", String.class, null);
        table.addContainerProperty("Year", Integer.class, null);

        table.addItem(
                new Object[] { "Nicolaus", "Copernicus", new Integer(1473) },
                new Integer(1));
        table.addItem(new Object[] { "Tycho", "Brahe", new Integer(1546) },
                new Integer(2));
        table.addItem(new Object[] { "Giordano", "Bruno", new Integer(1548) },
                new Integer(3));
        table.addItem(new Object[] { "Galileo", "Galilei", new Integer(1564) },
                new Integer(4));
        table.addItem(new Object[] { "Johannes", "Kepler", new Integer(1571) },
                new Integer(5));
        table.addItem(new Object[] { "Isaac", "Newton", new Integer(1643) },
                new Integer(6));

        addComponent(table);

        setPollInterval(1000);
    }
}
