package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

public class NestedMethodPropertyTest {

    public static class Address implements Serializable {
        private String street;
        private int postalCodePrimitive;
        private Integer postalCodeObject;

        public Address(String street, int postalCode) {
            this.street = street;
            postalCodePrimitive = postalCode;
            postalCodeObject = postalCode;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }

        public void setPostalCodePrimitive(int postalCodePrimitive) {
            this.postalCodePrimitive = postalCodePrimitive;
        }

        public int getPostalCodePrimitive() {
            return postalCodePrimitive;
        }

        public void setPostalCodeObject(Integer postalCodeObject) {
            this.postalCodeObject = postalCodeObject;
        }

        public Integer getPostalCodeObject() {
            return postalCodeObject;
        }

        // read-only boolean property
        public boolean isBoolean() {
            return true;
        }
    }

    public static class Person implements Serializable {
        private String name;
        private Address address;
        private int age;

        public Person(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public Person(String name, Address address, int age) {
            this.name = name;
            this.address = address;
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Team implements Serializable {
        private String name;
        private Person manager;

        public Team(String name, Person manager) {
            this.name = name;
            this.manager = manager;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setManager(Person manager) {
            this.manager = manager;
        }

        public Person getManager() {
            return manager;
        }
    }

    private Address oldMill;
    private Person joonas;
    private Team vaadin;

    @Before
    public void setUp() {
        oldMill = new Address("Ruukinkatu 2-4", 20540);
        joonas = new Person("Joonas", oldMill);
        vaadin = new Team("Vaadin", joonas);
    }

    @Test
    public void testSingleLevelNestedSimpleProperty() {
        NestedMethodProperty<String> nameProperty = new NestedMethodProperty<String>(
                vaadin, "name");

        assertEquals(String.class, nameProperty.getType());
        assertEquals("Vaadin", nameProperty.getValue());
    }

    @Test
    public void testSingleLevelNestedObjectProperty() {
        NestedMethodProperty<Person> managerProperty = new NestedMethodProperty<Person>(
                vaadin, "manager");

        assertEquals(Person.class, managerProperty.getType());
        assertEquals(joonas, managerProperty.getValue());
    }

    @Test
    public void testMultiLevelNestedProperty() {
        NestedMethodProperty<String> managerNameProperty = new NestedMethodProperty<String>(
                vaadin, "manager.name");
        NestedMethodProperty<Address> addressProperty = new NestedMethodProperty<Address>(
                vaadin, "manager.address");
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        NestedMethodProperty<Integer> postalCodePrimitiveProperty = new NestedMethodProperty<Integer>(
                vaadin, "manager.address.postalCodePrimitive");
        NestedMethodProperty<Integer> postalCodeObjectProperty = new NestedMethodProperty<Integer>(
                vaadin, "manager.address.postalCodeObject");
        NestedMethodProperty<Boolean> booleanProperty = new NestedMethodProperty<Boolean>(
                vaadin, "manager.address.boolean");

        assertEquals(String.class, managerNameProperty.getType());
        assertEquals("Joonas", managerNameProperty.getValue());

        assertEquals(Address.class, addressProperty.getType());
        assertEquals(oldMill, addressProperty.getValue());

        assertEquals(String.class, streetProperty.getType());
        assertEquals("Ruukinkatu 2-4", streetProperty.getValue());

        assertEquals(Integer.class, postalCodePrimitiveProperty.getType());
        assertEquals(Integer.valueOf(20540),
                postalCodePrimitiveProperty.getValue());

        assertEquals(Integer.class, postalCodeObjectProperty.getType());
        assertEquals(Integer.valueOf(20540),
                postalCodeObjectProperty.getValue());

        assertEquals(Boolean.class, booleanProperty.getType());
        assertEquals(Boolean.TRUE, booleanProperty.getValue());
    }

    @Test
    public void testEmptyPropertyName() {
        try {
            new NestedMethodProperty<Object>(vaadin, "");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }

        try {
            new NestedMethodProperty<Object>(vaadin, " ");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
    }

    @Test
    public void testInvalidPropertyName() {
        try {
            new NestedMethodProperty<Object>(vaadin, ".");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, ".manager");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, "manager.");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, "manager..name");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
    }

    @Test
    public void testInvalidNestedPropertyName() {
        try {
            new NestedMethodProperty<Object>(vaadin, "member");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }

        try {
            new NestedMethodProperty<Object>(vaadin, "manager.pet");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }

        try {
            new NestedMethodProperty<Object>(vaadin, "manager.address.city");
            fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
    }

    @Test
    public void testNullNestedProperty() {
        NestedMethodProperty<String> managerNameProperty = new NestedMethodProperty<String>(
                vaadin, "manager.name");
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");

        joonas.setAddress(null);
        assertNull(streetProperty.getValue());

        vaadin.setManager(null);
        assertNull(managerNameProperty.getValue());
        assertNull(streetProperty.getValue());

        vaadin.setManager(joonas);
        assertEquals("Joonas", managerNameProperty.getValue());
        assertNull(streetProperty.getValue());

    }

    @Test
    public void testMultiLevelNestedPropertySetValue() {
        NestedMethodProperty<String> managerNameProperty = new NestedMethodProperty<String>(
                vaadin, "manager.name");
        NestedMethodProperty<Address> addressProperty = new NestedMethodProperty<Address>(
                vaadin, "manager.address");
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        NestedMethodProperty<Integer> postalCodePrimitiveProperty = new NestedMethodProperty<Integer>(
                vaadin, "manager.address.postalCodePrimitive");
        NestedMethodProperty<Integer> postalCodeObjectProperty = new NestedMethodProperty<Integer>(
                vaadin, "manager.address.postalCodeObject");

        managerNameProperty.setValue("Joonas L");
        assertEquals("Joonas L", joonas.getName());
        streetProperty.setValue("Ruukinkatu");
        assertEquals("Ruukinkatu", oldMill.getStreet());
        postalCodePrimitiveProperty.setValue(0);
        postalCodeObjectProperty.setValue(1);
        assertEquals(0, oldMill.getPostalCodePrimitive());
        assertEquals(Integer.valueOf(1), oldMill.getPostalCodeObject());

        postalCodeObjectProperty.setValue(null);
        assertNull(oldMill.getPostalCodeObject());

        Address address2 = new Address("Other street", 12345);
        addressProperty.setValue(address2);
        assertEquals("Other street", streetProperty.getValue());

        Address address3 = null;
        addressProperty.setValue(address3);
        assertNull(addressProperty.getValue());
        streetProperty.setValue("Ruukinkatu");
        assertNull(streetProperty.getValue());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        @SuppressWarnings("unchecked")
        NestedMethodProperty<String> property2 = (NestedMethodProperty<String>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        assertEquals("Ruukinkatu 2-4", property2.getValue());
    }

    @Test
    public void testSerializationWithIntermediateNull()
            throws IOException, ClassNotFoundException {
        vaadin.setManager(null);
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        @SuppressWarnings("unchecked")
        NestedMethodProperty<String> property2 = (NestedMethodProperty<String>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        assertNull(property2.getValue());
    }

    @Test
    public void testIsReadOnly() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        NestedMethodProperty<Boolean> booleanProperty = new NestedMethodProperty<Boolean>(
                vaadin, "manager.address.boolean");

        assertFalse(streetProperty.isReadOnly());
        assertTrue(booleanProperty.isReadOnly());
    }

    @Test
    public void testChangeInstance() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");

        Address somewhere = new Address("The street", 1234);
        Person someone = new Person("Someone", somewhere);
        Team someteam = new Team("The team", someone);
        streetProperty.setInstance(someteam);

        assertEquals("The street", streetProperty.getValue());
        assertEquals("Ruukinkatu 2-4",
                vaadin.getManager().getAddress().getStreet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeInstanceToIncompatible() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");

        streetProperty.setInstance("bar");
    }

}
