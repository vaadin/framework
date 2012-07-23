package com.vaadin.tests.components.beanitemcontainer;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

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
                .createContainer(100, 1));
        table.setColumnCollapsingAllowed(true);

        Button b = new Button("Disable sorting", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setSortEnabled(!table.isSortEnabled());
                if (table.isSortEnabled()) {
                    event.getButton().setCaption("Disable sorting");
                } else {
                    event.getButton().setCaption("Enable sorting");
                }
            }

        });

        addComponent(table);
        addComponent(b);
    }

}
