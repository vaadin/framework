/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.data.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
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
        Assert.assertEquals("John", property.getValue());
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
        Assert.assertEquals("John", property.getValue());
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
        Assert.assertNull(property.getValue());
    }

    @Test
    public void testMethodPropertyDescriptorWithPrimitivePropertyType()
            throws Exception {
        MethodPropertyDescriptor<Person> pd = new MethodPropertyDescriptor<Person>(
                "age", int.class, Person.class.getMethod("getAge"),
                Person.class.getMethod("setAge", int.class));

        Assert.assertEquals(Integer.class, pd.getPropertyType());
    }
}
