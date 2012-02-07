package com.vaadin.tests.components.combobox;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.ComboBox;

public class ComboBoxIdenticalItems extends TestBase {

    private Log log = new Log(5);

    @Override
    public void setup() {
        final ComboBox select = new ComboBox("ComboBox");
        select.addContainerProperty("caption", String.class, null);
        Item item = select.addItem("one-1");
        item.getItemProperty("caption").setValue("One");
        item = select.addItem("one-2");
        item.getItemProperty("caption").setValue("One");
        item = select.addItem("two");
        item.getItemProperty("caption").setValue("Two");
        select.setItemCaptionPropertyId("caption");
        select.setNullSelectionAllowed(false);
        select.setImmediate(true);
        select.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -7932700771673919620L;

            public void valueChange(ValueChangeEvent event) {
                log.log("Item " + select.getValue() + " selected");

            }
        });

        addComponent(log);
        addComponent(select);
    }

    @Override
    protected String getDescription() {
        return "Keyboard selecting of a value is broken in combobox if two "
                + "items have the same caption. The first item's id is \"One-1\" "
                + "while the second one is \"One-2\". Selecting with mouse works "
                + "as expected but selecting with keyboard always returns the "
                + "object \"One-1\".";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
