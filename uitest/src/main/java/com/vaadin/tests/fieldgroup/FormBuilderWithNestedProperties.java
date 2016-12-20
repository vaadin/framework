package com.vaadin.tests.fieldgroup;

import com.vaadin.annotations.PropertyId;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextField;

public class FormBuilderWithNestedProperties extends TestBase {

    private TextField firstName;
    private TextField lastName;
    @PropertyId("address.streetAddress")
    private TextField streetAddress;

    @Override
    protected void setup() {
        FieldGroup fieldGroup = new BeanFieldGroup<>(Person.class);
        fieldGroup.buildAndBindMemberFields(this);

        addComponent(firstName);
        addComponent(lastName);
        addComponent(streetAddress);

        fieldGroup.setItemDataSource(new BeanItem<>(new Person("Who", "me?",
                "email", 1, Sex.MALE,
                new Address("street name", 202020, "City", Country.FINLAND))));
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
