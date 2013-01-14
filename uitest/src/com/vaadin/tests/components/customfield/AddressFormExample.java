package com.vaadin.tests.components.customfield;

import java.util.Locale;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Address;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;

/**
 * Demonstrate a custom field which is a form, and contains another custom field
 * for the selection of a city.
 */
public class AddressFormExample extends TestBase {

    @Override
    protected void setup() {
        getMainWindow().setLocale(Locale.ENGLISH);
        Address address = new Address("Ruukinkatu 2-4", 20540, "Turku");
        final AddressField field = new AddressField();
        field.setValue(address);
        field.setRequired(true);

        addComponent(field);

        Button commitButton = new Button("Save", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                field.commit();
                Address address = field.getValue();
                Notification.show("Address saved: "
                        + address.getStreetAddress() + ", "
                        + address.getPostalCode() + ", " + address.getCity());
            }
        });
        addComponent(commitButton);
    }

    @Override
    protected String getDescription() {
        return "Custom field for editing an Address";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
