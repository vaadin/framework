package com.vaadin.tests.server.container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.vaadin.data.util.MethodProperty.MethodException;
import com.vaadin.data.util.NestedMethodProperty;

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
        NestedMethodProperty<String> nameProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "name");

        Assert.assertEquals(String.class, nameProperty.getType());
        Assert.assertEquals("Vaadin", nameProperty.getValue());
    }

    public void testSingleLevelNestedObjectProperty() {
        NestedMethodProperty<Person> managerProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager");

        Assert.assertEquals(Person.class, managerProperty.getType());
        Assert.assertEquals(joonas, managerProperty.getValue());
    }

    public void testMultiLevelNestedProperty() {
        NestedMethodProperty<String> managerNameProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.name");
        NestedMethodProperty<Address> addressProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address");
        NestedMethodProperty<String> streetProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.street");
        NestedMethodProperty<String> postalCodePrimitiveProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin,
                        "manager.address.postalCodePrimitive");
        NestedMethodProperty<String> postalCodeObjectProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin,
                        "manager.address.postalCodeObject");
        NestedMethodProperty<String> booleanProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.boolean");

        Assert.assertEquals(String.class, managerNameProperty.getType());
        Assert.assertEquals("Joonas", managerNameProperty.getValue());

        Assert.assertEquals(Address.class, addressProperty.getType());
        Assert.assertEquals(oldMill, addressProperty.getValue());

        Assert.assertEquals(String.class, streetProperty.getType());
        Assert.assertEquals("Ruukinkatu 2-4", streetProperty.getValue());

        Assert.assertEquals(Integer.class,
                postalCodePrimitiveProperty.getType());
        Assert.assertEquals(20540, postalCodePrimitiveProperty.getValue());

        Assert.assertEquals(Integer.class, postalCodeObjectProperty.getType());
        Assert.assertEquals(20540, postalCodeObjectProperty.getValue());

        Assert.assertEquals(Boolean.class, booleanProperty.getType());
        Assert.assertEquals(true, booleanProperty.getValue());
    }

    public void testEmptyPropertyName() {
        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin, "");
            fail();
        } catch (MethodException e) {
            // should get exception
        }

        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin, " ");
            fail();
        } catch (MethodException e) {
            // should get exception
        }
    }

    public void testInvalidPropertyName() {
        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin, ".");
            fail();
        } catch (MethodException e) {
            // should get exception
        }
        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin, ".manager");
            fail();
        } catch (MethodException e) {
            // should get exception
        }
        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin, "manager.");
            fail();
        } catch (MethodException e) {
            // should get exception
        }
        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin,
                    "manager..name");
            fail();
        } catch (MethodException e) {
            // should get exception
        }
    }

    public void testInvalidNestedPropertyName() {
        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin, "member");
            fail();
        } catch (MethodException e) {
            // should get exception
        }

        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin,
                    "manager.pet");
            fail();
        } catch (MethodException e) {
            // should get exception
        }

        try {
            NestedMethodProperty.buildNestedMethodProperty(vaadin,
                    "manager.address.city");
            fail();
        } catch (MethodException e) {
            // should get exception
        }
    }

    public void testNullNestedProperty() {
        NestedMethodProperty<String> managerNameProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.name");
        NestedMethodProperty<String> streetProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.street");

        joonas.setAddress(null);
        try {
            streetProperty.getValue();
            fail();
        } catch (MethodException e) {
            // should get exception
        }

        vaadin.setManager(null);
        try {
            managerNameProperty.getValue();
            fail();
        } catch (MethodException e) {
            // should get exception
        }
        try {
            streetProperty.getValue();
            fail();
        } catch (MethodException e) {
            // should get exception
        }

        vaadin.setManager(joonas);
        Assert.assertEquals("Joonas", managerNameProperty.getValue());
    }

    public void testMultiLevelNestedPropertySetValue() {
        NestedMethodProperty<String> managerNameProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.name");
        NestedMethodProperty<Address> addressProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address");
        NestedMethodProperty<String> streetProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.street");
        NestedMethodProperty<String> postalCodePrimitiveProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin,
                        "manager.address.postalCodePrimitive");
        NestedMethodProperty<String> postalCodeObjectProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin,
                        "manager.address.postalCodeObject");

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
        NestedMethodProperty<String> streetProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        NestedMethodProperty<String> property2 = (NestedMethodProperty<String>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Assert.assertEquals("Ruukinkatu 2-4", property2.getValue());
    }

    public void testIsReadOnly() {
        NestedMethodProperty<String> streetProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.street");
        NestedMethodProperty<String> booleanProperty = NestedMethodProperty
                .buildNestedMethodProperty(vaadin, "manager.address.boolean");

        Assert.assertFalse(streetProperty.isReadOnly());
        Assert.assertTrue(booleanProperty.isReadOnly());
    }

}
