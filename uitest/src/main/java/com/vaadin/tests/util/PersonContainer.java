package com.vaadin.tests.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.vaadin.v7.data.util.BeanItemContainer;

@SuppressWarnings("serial")
public class PersonContainer extends BeanItemContainer<Person>
        implements Serializable {

    /**
     * Natural property order for Person bean. Used in tables and forms.
     */
    public static final Object[] NATURAL_COL_ORDER = new Object[] { "firstName",
            "lastName", "email", "phoneNumber", "address.streetAddress",
            "address.postalCode", "address.city" };

    /**
     * "Human readable" captions for properties in same order as in
     * NATURAL_COL_ORDER.
     */
    public static final String[] COL_HEADERS_ENGLISH = new String[] {
            "First name", "Last name", "Email", "Phone number",
            "Street Address", "Postal Code", "City" };

    public PersonContainer() {
        super(Person.class);
        addNestedContainerProperty("address.streetAddress");
        addNestedContainerProperty("address.postalCode");
        addNestedContainerProperty("address.city");
    }

    public static PersonContainer createWithTestData() {
        return createWithTestData(100);
    }

    public static PersonContainer createWithTestData(int size) {
        PersonContainer c = new PersonContainer();
        c.addAll(createTestData(size));
        return c;
    }

    public static Collection<Person> createTestData() {
        return createTestData(100);
    }

    public static Collection<Person> createTestData(int size) {
        Random r = new Random(0);
        ArrayList<Person> testData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Person p = new Person();
            p.setFirstName(TestDataGenerator.getFirstName(r));
            p.setLastName(TestDataGenerator.getLastName(r));
            p.getAddress().setCity(TestDataGenerator.getCity(r));
            p.setEmail(p.getFirstName().toLowerCase() + "."
                    + p.getLastName().toLowerCase() + "@vaadin.com");
            p.setPhoneNumber(TestDataGenerator.getPhoneNumber(r));

            p.getAddress().setPostalCode(TestDataGenerator.getPostalCode(r));
            p.getAddress()
                    .setStreetAddress(TestDataGenerator.getStreetAddress(r));
            testData.add(p);
        }
        return testData;
    }

}
