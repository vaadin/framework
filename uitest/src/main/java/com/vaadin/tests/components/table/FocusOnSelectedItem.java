package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table;

/**
 * Test to see if the correct row gets the focus when the row is selected from
 * the serverside and forces the table to scroll down
 *
 * @author Vaadin Ltd
 */
public class FocusOnSelectedItem extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.setSelectable(true);
        table.setImmediate(true);

        table.addContainerProperty("Property", String.class, null);

        for (int i = 0; i < 200; i++) {
            table.addItem(new String[] { "Item " + i }, "Item " + i);
        }
        addComponent(table);

        addButton("Select", event -> {
            table.setValue("Item 198");
            table.setCurrentPageFirstItemId("Item 198");
            table.focus();
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test whether the selected row retains focus.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 10522;
    }

}
