package com.vaadin.tests.components.combobox;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.ComboBox;

public class ComboBoxIdenticalItems extends AbstractTestUI {

    private Log log = new Log(5);

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
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
        select.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Item " + select.getValue() + " selected");

            }
        });

        addComponent(log);
        addComponent(select);
    }

    @Override
    protected String getTestDescription() {
        return "Keyboard selecting of a value is broken in combobox if two "
                + "items have the same caption. The first item's id is \"One-1\" "
                + "while the second one is \"One-2\". Selecting with mouse works "
                + "as expected but selecting with keyboard always returns the "
                + "object \"One-1\".";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6125;
    }
}
