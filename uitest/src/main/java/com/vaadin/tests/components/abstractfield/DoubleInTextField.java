package com.vaadin.tests.components.abstractfield;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.TextField;

public class DoubleInTextField extends AbstractComponentDataBindingTest {

    @Override
    protected void createFields() {
        Person person = new Person("John", "Doe", "john@doe.com", 78, Sex.MALE,
                new Address("Dovestreet 12", 12233, "Johnston",
                        Country.SOUTH_AFRICA));

        TextField salary = new TextField("Vaadin 7 - TextField with Double");
        addComponent(salary);
        salary.setPropertyDataSource(new MethodProperty<Double>(person,
                "salaryDouble"));

        TextField salary6 = new TextField("Vaadin 6 - TextField with Double");
        addComponent(salary6);
        salary6.setPropertyDataSource(new MethodProperty<Double>(person,
                "salaryDouble"));
        salary6.setConverter(new Vaadin6ImplicitDoubleConverter());

    }

}
