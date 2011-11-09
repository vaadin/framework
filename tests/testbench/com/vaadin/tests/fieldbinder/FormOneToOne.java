package com.vaadin.tests.fieldbinder;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Form;

public class FormOneToOne extends TestBase {

    @Override
    protected void setup() {
        final Form form = new Form();
        addComponent(form);
        form.setItemDataSource(createPersonItem());
    }

    protected BeanItem<Person> createPersonItem() {
        Person person = new Person("First", "Last", "foo@vaadin.com",
                "02-111 2222", "Ruukinkatu 2-4", 20540, "Turku");

        // TODO this should be made much easier!!!
        BeanItem<Person> item = new BeanItem<Person>(person);
        item.addItemProperty("streetAddress", new NestedMethodProperty<String>(
                person, "address.streetAddress"));
        item.addItemProperty("postalCode", new NestedMethodProperty<Integer>(
                person, "address.postalCode"));
        item.addItemProperty("city", new NestedMethodProperty<String>(person,
                "address.city"));
        item.removeItemProperty("address");

        return item;
    }

    @Override
    protected String getDescription() {
        return "Form where some properties come from a sub-object of the bean.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
