package com.vaadin.tests.components.beanitemcontainer;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;

public class BeanItemContainerNullValues extends TestBase {

    private Table table;

    @Override
    protected String getDescription() {
        return "Null values should be sorted first (ascending sort) in a BeanItemContainer. Sort the 'country' column to see that the empty values come first.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2917;
    }

    @Override
    protected void setup() {
        table = new Table();
        table.setSortDisabled(false);
        table.setContainerDataSource(BeanItemContainerGenerator
                .createContainer(100));
        table.setColumnCollapsingAllowed(true);

        Button b = new Button("Disable sorting", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                table.setSortDisabled(!table.isSortDisabled());
                if (table.isSortDisabled()) {
                    event.getButton().setCaption("Enable sorting");
                } else {
                    event.getButton().setCaption("Disable sorting");
                }
            }

        });

        addComponent(table);
        addComponent(b);
    }

}
