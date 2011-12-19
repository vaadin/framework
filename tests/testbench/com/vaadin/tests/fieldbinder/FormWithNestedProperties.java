package com.vaadin.tests.fieldbinder;

import com.vaadin.data.fieldbinder.BeanFieldBinder;
import com.vaadin.data.fieldbinder.FormBuilder;
import com.vaadin.data.fieldbinder.PropertyId;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class FormWithNestedProperties extends AbstractBeanFieldBinderTest {

    private Log log = new Log(5);

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

        setFieldBinder(new BeanFieldBinder<Person>(Person.class));
        getFieldBinder().bindFields(this);
        country = new FormBuilder(getFieldBinder()).buildAndBind("country",
                "address.country", NativeSelect.class);
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

        getFieldBinder().setItemDataSource(
                new Person("First", "Last", "Email", 52, Sex.FEMALE,
                        new Address("street address", 01234, "City",
                                Country.FINLAND)));

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
