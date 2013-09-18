package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

public class ShowLastItem extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setHeight("210px");

        table.addContainerProperty("Col", String.class, "");

        for (int i = 0; i < 20; i++) {
            table.addItem(i).getItemProperty("Col")
                    .setValue("row " + String.valueOf(i));
        }

        Button addItemBtn = new Button("Add item", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                Object itemId = "row " + table.getItemIds().size();

                table.addItem(itemId).getItemProperty("Col")
                        .setValue(String.valueOf(itemId));

                table.setCurrentPageFirstItemIndex(table.getItemIds().size() - 1);
            }
        });

        addComponent(table);
        addComponent(addItemBtn);

    }

    @Override
    protected String getDescription() {
        return "Show last item in Table by using setCurrentPageFirstItemId";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
