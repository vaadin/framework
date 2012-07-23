package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

public class TableModifcationsWhenScrolledRight extends TestBase {

    @Override
    protected void setup() {
        final Table t = new Table();
        Button btn = new Button("Add row");
        Integer row = 1;

        t.setPageLength(5);
        t.setWidth("400px");
        t.addContainerProperty("name", String.class, "NA");
        t.setColumnCollapsingAllowed(true);

        for (Integer col = 0; col < 10; col++) {
            t.addContainerProperty(col, Integer.class, col);
            t.setColumnWidth(col, 50);
        }
        t.addItem(row).getItemProperty("name").setValue("Row" + row);

        btn.addListener(new ClickListener() {
            Integer row = 2;

            @Override
            public void buttonClick(ClickEvent event) {
                t.addItem(row).getItemProperty("name").setValue("Row" + row);
                row++;
            }
        });

        addComponent(t);
        addComponent(btn);
    }

    @Override
    protected String getDescription() {
        return "Scroll right and then click \"Add row\". The table will scroll back left and the headers should also.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5382;
    }

}
