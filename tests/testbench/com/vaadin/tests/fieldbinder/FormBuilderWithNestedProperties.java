package com.vaadin.tests.fieldbinder;

import com.vaadin.data.fieldbinder.BeanFieldGroup;
import com.vaadin.data.fieldbinder.FieldGroup;
import com.vaadin.data.fieldbinder.FormBuilder;
import com.vaadin.data.fieldbinder.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.TextField;

public class FormBuilderWithNestedProperties extends TestBase {

    private TextField firstName;
    private TextField lastName;
    @PropertyId("address.streetAddress")
    private TextField streetAddress;

    @Override
    protected void setup() {
        FieldGroup fieldBinder = new BeanFieldGroup<Person>(Person.class);
        FormBuilder b = new FormBuilder(fieldBinder);
        b.buildAndBindFields(this);

        addComponent(firstName);
        addComponent(lastName);
        addComponent(streetAddress);

        fieldBinder.setItemDataSource(new BeanItem<Person>(new Person("Who",
                "me?", "email", 1, Sex.MALE, new Address("street name", 202020,
                        "City", Country.FINLAND))));
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
