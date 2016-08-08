package com.vaadin.tests.fieldgroup;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;

public class FormWithNestedProperties extends AbstractBeanFieldGroupTest {

    private Log log = new Log(5);

    private LegacyTextField firstName = new LegacyTextField("First name");
    private LegacyTextField lastName = new LegacyTextField("Last name");
    private LegacyTextField email = new LegacyTextField("Email");
    private LegacyTextField age = new LegacyTextField("Age");

    @PropertyId("address.streetAddress")
    private LegacyTextField streetAddress = new LegacyTextField("Street address");
    private NativeSelect country;

    private CheckBox deceased = new CheckBox("Deceased");

    @Override
    protected void setup() {
        super.setup();

        setFieldBinder(new BeanFieldGroup<Person>(Person.class));
        country = (NativeSelect) getFieldBinder().buildAndBind("country",
                "address.country", NativeSelect.class);
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
