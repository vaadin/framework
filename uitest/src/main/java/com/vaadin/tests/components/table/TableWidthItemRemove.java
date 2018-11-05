package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table;

/**
 * Test whether adding the first item to a table calculates the table width
 * correctly
 *
 * @author Vaadin Ltd
 */
public class TableWidthItemRemove extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table("My table");
        table.addContainerProperty("firstName", String.class, null);
        table.addContainerProperty("lastName", String.class, null);
        table.addContainerProperty("year", Integer.class, null);
        table.setColumnWidth("firstName", 200);
        table.setColumnWidth("lastName", 100);
        table.setColumnWidth("year", 50);

        addButton("Clean", event -> table.removeAllItems());

        addButton("Populate",
                event -> table.addItem(
                        new Object[] { "John", "Doe", new Integer(1980) },
                        Math.random() * 1000));

        addComponent(table);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "The table should retain the correct width on item remove and add.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13592;
    }

}
