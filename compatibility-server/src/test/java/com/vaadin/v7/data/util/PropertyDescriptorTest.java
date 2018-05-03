package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.NestedMethodPropertyTest.Person;

public class PropertyDescriptorTest {

    @Test
    public void testMethodPropertyDescriptorSerialization() throws Exception {
        PropertyDescriptor[] pds = Introspector.getBeanInfo(Person.class)
                .getPropertyDescriptors();

        MethodPropertyDescriptor<Person> descriptor = null;

        for (PropertyDescriptor pd : pds) {
            if ("name".equals(pd.getName())) {
                descriptor = new MethodPropertyDescriptor<Person>(pd.getName(),
                        String.class, pd.getReadMethod(), pd.getWriteMethod());
                break;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(descriptor);
        @SuppressWarnings("unchecked")
        VaadinPropertyDescriptor<Person> descriptor2 = (VaadinPropertyDescriptor<Person>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Property<?> property = descriptor2
                .createProperty(new Person("John", null));
        assertEquals("John", property.getValue());
    }

    @Test
    public void testSimpleNestedPropertyDescriptorSerialization()
            throws Exception {
        NestedPropertyDescriptor<Person> pd = new NestedPropertyDescriptor<Person>(
                "name", Person.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(pd);
        @SuppressWarnings("unchecked")
        VaadinPropertyDescriptor<Person> pd2 = (VaadinPropertyDescriptor<Person>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Property<?> property = pd2.createProperty(new Person("John", null));
        assertEquals("John", property.getValue());
    }

    @Test
    public void testNestedPropertyDescriptorSerialization() throws Exception {
        NestedPropertyDescriptor<Person> pd = new NestedPropertyDescriptor<Person>(
                "address.street", Person.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(pd);
        @SuppressWarnings("unchecked")
        VaadinPropertyDescriptor<Person> pd2 = (VaadinPropertyDescriptor<Person>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Property<?> property = pd2.createProperty(new Person("John", null));
        assertNull(property.getValue());
    }

    @Test
    public void testMethodPropertyDescriptorWithPrimitivePropertyType()
            throws Exception {
        MethodPropertyDescriptor<Person> pd = new MethodPropertyDescriptor<Person>(
                "age", int.class, Person.class.getMethod("getAge"),
                Person.class.getMethod("setAge", int.class));

        assertEquals(Integer.class, pd.getPropertyType());
    }
}
