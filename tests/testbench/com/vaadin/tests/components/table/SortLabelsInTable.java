/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class SortLabelsInTable extends TestBase {

    @Override
    protected void setup() {
        Table t = new Table("A table with a text column and a Label column");
        t.addContainerProperty("text", String.class, null);
        t.addContainerProperty("label", Label.class, null);

        for (int i = 0; i < 20; i++) {
            Item item = t.addItem("" + i);
            item.getItemProperty("text").setValue("Text " + i);
            item.getItemProperty("label").setValue(new Label("Label " + i));
        }
        addComponent(t);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 8845;
    }

}
