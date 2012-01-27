package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TableLastRowMissing extends TestBase {

    @Override
    protected String getDescription() {
        return "The table below should display 3 rows. Each with a textfield containing the row number.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2933;
    }

    @Override
    protected void setup() {
        Table t = new Table();
        addComponent(t);

        t.addContainerProperty("Name", TextField.class, null);

        for (int i = 0; i < 3; i++) {
            Item item = t.addItem(i);
            TextField tf = new TextField("", String.valueOf(i + 1));
            tf.setColumns(10);
            item.getItemProperty("Name").setValue(tf);
        }

    }
}
