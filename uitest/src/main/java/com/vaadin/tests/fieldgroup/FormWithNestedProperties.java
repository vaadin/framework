package com.vaadin.tests.fieldgroup;

import com.vaadin.annotations.PropertyId;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("deprecation")
public class FormWithNestedProperties
        extends AbstractBeanFieldGroupTest<Person> {

    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private TextField age = new TextField("Age");

    @PropertyId("address.streetAddress")
    private TextField streetAddress = new TextField("Street address");
    private NativeSelect country;

    private CheckBox deceased = new CheckBox("Deceased");

    @Override
    protected void setup() {
        super.setup();

        setFieldBinder(new BeanFieldGroup<>(Person.class));
        country = getFieldBinder().buildAndBind("country", "address.country",
                NativeSelect.class);
        getFieldBinder().bindMemberFields(this);
        addComponent(firstName);
        addComponent(lastName);
        addComponent(streetAddress);
        addComponent(country);
        addComponent(email);
        addComponent(age);
        addComponent(deceased);
        addComponent(getCommitButton());
        addComponent(getDiscardButton());
        addComponent(getShowBeanButton());

        getFieldBinder().setItemDataSource(new Person("First", "Last", "Email",
                52, Sex.FEMALE,
                new Address("street address", 01234, "City", Country.FINLAND)));

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
