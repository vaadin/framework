package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Table;

public class ShowLastItem extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.setHeight("210px");

        table.addContainerProperty("Col", String.class, "");

        for (int i = 0; i < 20; i++) {
            table.addItem(i).getItemProperty("Col")
                    .setValue("row " + String.valueOf(i));
        }

        Button addItemBtn = new Button("Add item", event -> {
            Object itemId = "row " + table.getItemIds().size();

            table.addItem(itemId).getItemProperty("Col")
                    .setValue(String.valueOf(itemId));

            table.setCurrentPageFirstItemIndex(table.getItemIds().size() - 1);
        });

        addComponent(table);
        addComponent(addItemBtn);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Show last item in Table by using setCurrentPageFirstItemId";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12407;
    }

}
