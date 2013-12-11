package com.vaadin.data.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.TestCase;

import org.junit.Assert;

public class NestedMethodPropertyTest extends TestCase {

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

        public Person(String name, Address address) {
            this.name = name;
            this.address = address;
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

    @Override
    public void setUp() {
        oldMill = new Address("Ruukinkatu 2-4", 20540);
        joonas = new Person("Joonas", oldMill);
        vaadin = new Team("Vaadin", joonas);
    }

    @Override
    public void tearDown() {
        vaadin = null;
        joonas = null;
        oldMill = null;
    }

    public void testSingleLevelNestedSimpleProperty() {
        NestedMethodProperty<String> nameProperty = new NestedMethodProperty<String>(
                vaadin, "name");

        Assert.assertEquals(String.class, nameProperty.getType());
        Assert.assertEquals("Vaadin", nameProperty.getValue());
    }

    public void testSingleLevelNestedObjectProperty() {
        NestedMethodProperty<Person> managerProperty = new NestedMethodProperty<Person>(
                vaadin, "manager");

        Assert.assertEquals(Person.class, managerProperty.getType());
        Assert.assertEquals(joonas, managerProperty.getValue());
    }

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

        Assert.assertEquals(String.class, managerNameProperty.getType());
        Assert.assertEquals("Joonas", managerNameProperty.getValue());

        Assert.assertEquals(Address.class, addressProperty.getType());
        Assert.assertEquals(oldMill, addressProperty.getValue());

        Assert.assertEquals(String.class, streetProperty.getType());
        Assert.assertEquals("Ruukinkatu 2-4", streetProperty.getValue());

        Assert.assertEquals(Integer.class,
                postalCodePrimitiveProperty.getType());
        Assert.assertEquals(Integer.valueOf(20540),
                postalCodePrimitiveProperty.getValue());

        Assert.assertEquals(Integer.class, postalCodeObjectProperty.getType());
        Assert.assertEquals(Integer.valueOf(20540),
                postalCodeObjectProperty.getValue());

        Assert.assertEquals(Boolean.class, booleanProperty.getType());
        Assert.assertEquals(Boolean.TRUE, booleanProperty.getValue());
    }

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
        Assert.assertEquals("Joonas", managerNameProperty.getValue());
        Assert.assertNull(streetProperty.getValue());

    }

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
        Assert.assertEquals("Joonas L", joonas.getName());
        streetProperty.setValue("Ruukinkatu");
        Assert.assertEquals("Ruukinkatu", oldMill.getStreet());
        postalCodePrimitiveProperty.setValue(0);
        postalCodeObjectProperty.setValue(1);
        Assert.assertEquals(0, oldMill.getPostalCodePrimitive());
        Assert.assertEquals(Integer.valueOf(1), oldMill.getPostalCodeObject());

        postalCodeObjectProperty.setValue(null);
        Assert.assertNull(oldMill.getPostalCodeObject());

        Address address2 = new Address("Other street", 12345);
        addressProperty.setValue(address2);
        Assert.assertEquals("Other street", streetProperty.getValue());
    }

    public void testSerialization() throws IOException, ClassNotFoundException {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        @SuppressWarnings("unchecked")
        NestedMethodProperty<String> property2 = (NestedMethodProperty<String>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Assert.assertEquals("Ruukinkatu 2-4", property2.getValue());
    }

    public void testSerializationWithIntermediateNull() throws IOException,
            ClassNotFoundException {
        vaadin.setManager(null);
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        @SuppressWarnings("unchecked")
        NestedMethodProperty<String> property2 = (NestedMethodProperty<String>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Assert.assertNull(property2.getValue());
    }

    public void testIsReadOnly() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(
                vaadin, "manager.address.street");
        NestedMethodProperty<Boolean> booleanProperty = new NestedMethodProperty<Boolean>(
                vaadin, "manager.address.boolean");

        Assert.assertFalse(streetProperty.isReadOnly());
        Assert.assertTrue(booleanProperty.isReadOnly());
    }

}
