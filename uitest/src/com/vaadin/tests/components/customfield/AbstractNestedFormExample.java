package com.vaadin.tests.components.customfield;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Table;

/**
 * Demonstrate the use of a form as a custom field within another form.
 */
public abstract class AbstractNestedFormExample extends TestBase {
    private NestedPersonForm personForm;
    private boolean embeddedAddress;

    public void setup(boolean embeddedAddress) {
        this.embeddedAddress = embeddedAddress;

        addComponent(getPersonTable());
    }

    /**
     * Creates a table with two person objects
     */
    public Table getPersonTable() {
        Table table = new Table();
        table.setPageLength(5);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(true);
        table.addContainerProperty("Name", String.class, null);
        table.addListener(getTableValueChangeListener());
        Person person = new Person("Teppo", "Testaaja",
                "teppo.testaaja@example.com", "", "Ruukinkatu 2–4", 20540,
                "Turku");
        Person person2 = new Person("Taina", "Testaaja",
                "taina.testaaja@example.com", "", "Ruukinkatu 2–4", 20540,
                "Turku");
        Item item = table.addItem(person);
        item.getItemProperty("Name").setValue(
                person.getFirstName() + " " + person.getLastName());
        item = table.addItem(person2);
        item.getItemProperty("Name").setValue(
                person2.getFirstName() + " " + person2.getLastName());
        return table;
    }

    /**
     * Creates value change listener for the table
     */
    private Property.ValueChangeListener getTableValueChangeListener() {
        return new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (personForm != null) {
                    removeComponent(personForm);
                }
                if (event.getProperty().getValue() != null) {
                    personForm = new NestedPersonForm((Person) event
                            .getProperty().getValue(), embeddedAddress);
                    personForm.setWidth("350px");
                    addComponent(personForm);
                }
            }

        };
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
