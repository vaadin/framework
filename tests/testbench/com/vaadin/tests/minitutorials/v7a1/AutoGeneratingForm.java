/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Auto%20generating%20
 * a%20form%20based%20on%20a%20bean%20-%20Vaadin%206%20style%20Form
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class AutoGeneratingForm extends Root {

    @Override
    protected void init(WrappedRequest request) {
        FieldGroup fieldGroup = new BeanFieldGroup<Person>(Person.class);

        // We need an item data source before we create the fields to be able to
        // find the properties, otherwise we have to specify them by hand
        fieldGroup.setItemDataSource(new BeanItem<Person>(new Person("John",
                "Doe", 34)));

        // Loop through the properties, build fields for them and add the fields
        // to this root
        for (Object propertyId : fieldGroup.getUnboundPropertyIds()) {
            addComponent(fieldGroup.buildAndBind(propertyId));
        }
    }

}

class Person {
    private String firstName, lastName;
    private int age;

    // + setters and getters

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
