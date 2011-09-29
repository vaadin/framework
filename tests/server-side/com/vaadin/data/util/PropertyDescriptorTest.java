package com.vaadin.data.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.vaadin.data.Property;
import com.vaadin.data.util.MethodPropertyDescriptor;
import com.vaadin.data.util.NestedPropertyDescriptor;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.data.util.NestedMethodPropertyTest.Person;

public class PropertyDescriptorTest extends TestCase {
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
        VaadinPropertyDescriptor<Person> descriptor2 = (VaadinPropertyDescriptor<Person>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Property property = descriptor2
                .createProperty(new Person("John", null));
        Assert.assertEquals("John", property.getValue());
    }

    public void testNestedPropertyDescriptorSerialization() throws Exception {
        NestedPropertyDescriptor<Person> pd = new NestedPropertyDescriptor<Person>(
                "name", Person.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(pd);
        VaadinPropertyDescriptor<Person> pd2 = (VaadinPropertyDescriptor<Person>) new ObjectInputStream(
                new ByteArrayInputStream(baos.toByteArray())).readObject();

        Property property = pd2.createProperty(new Person("John", null));
        Assert.assertEquals("John", property.getValue());
    }
}
