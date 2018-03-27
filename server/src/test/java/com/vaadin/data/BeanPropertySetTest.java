/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import com.vaadin.data.provider.bov.Person;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.FatherAndSon;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.server.ClassesSerializableTest;

import static com.vaadin.data.PropertyFilterDefinition.getDefaultFilter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;

public class BeanPropertySetTest {
    @Test
    public void testSerializeDeserialize_propertySet() throws Exception {
        PropertySet<Person> originalPropertySet = BeanPropertySet
                .get(Person.class);

        PropertySet<Person> deserializedPropertySet = ClassesSerializableTest
                .serializeAndDeserialize(originalPropertySet);

        comparePropertySet(originalPropertySet, deserializedPropertySet, "Deserialized instance should be the same as the original");
    }

    private void comparePropertySet(PropertySet<?> propertySetA, PropertySet<?> propertySetB, String message) {

        PropertyDefinition<?, ?>[] propertiesA = propertySetA.getProperties()
                .sorted(Comparator.comparing(PropertyDefinition::getName))
                .toArray(PropertyDefinition<?, ?>[]::new);
        PropertyDefinition<?, ?>[] propertiesB = propertySetA.getProperties()
                .sorted(Comparator.comparing(PropertyDefinition::getName))
                .toArray(PropertyDefinition<?, ?>[]::new);

        assertEquals(message, propertiesA.length, propertiesB.length);
        for (int i = 0; i < propertiesB.length; i++) {
            assertSame(message,
                    propertiesA[i], propertiesB[i]);
        }
    }

    @Test
    public void testSerializeDeserialize_propertySet_cacheCleared()
            throws Exception {
        PropertySet<Person> originalPropertySet = BeanPropertySet
                .get(Person.class);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bs);
        out.writeObject(originalPropertySet);
        byte[] data = bs.toByteArray();

        // Simulate deserializing into a different JVM by clearing the instance
        // map
        Field instancesField = BeanPropertySet.class
                .getDeclaredField("INSTANCES");
        instancesField.setAccessible(true);
        Map<?, ?> instances = (Map<?, ?>) instancesField.get(null);
        instances.clear();

        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data));
        PropertySet<Person> deserializedPropertySet = (PropertySet<Person>) in
                .readObject();

        comparePropertySet(BeanPropertySet.get(Person.class), deserializedPropertySet, "Deserialized instance should be the same as in the cache");
        assertNotSame(
                "Deserialized instance should not be the same as the original",
                originalPropertySet, deserializedPropertySet);
    }

    @Test
    public void testSerializeDeserialize_propertyDefinition() throws Exception {
        PropertyDefinition<Person, ?> definition = BeanPropertySet
                .get(Person.class).getProperty("born")
                .orElseThrow(RuntimeException::new);

        PropertyDefinition<Person, ?> deserializedDefinition = ClassesSerializableTest
                .serializeAndDeserialize(definition);

        ValueProvider<Person, ?> getter = deserializedDefinition.getGetter();
        Person person = new Person("Milennial", 2000);
        Integer age = (Integer) getter.apply(person);

        assertEquals("Deserialized definition should be functional",
                Integer.valueOf(2000), age);

        assertSame("Deserialized instance should be the same as in the cache",
                BeanPropertySet.get(Person.class).getProperty("born")
                        .orElseThrow(RuntimeException::new),
                deserializedDefinition);
    }

    @Test
    public void testSerializeDeserialize_nestedPropertyDefinition()
            throws Exception {

        PropertyDefinition<com.vaadin.tests.data.bean.Person, ?> definition = BeanPropertySet
                .get(com.vaadin.tests.data.bean.Person.class,true, getDefaultFilter())
                .getProperty("address.postalCode").orElseThrow(AssertionFailedError::new);


        PropertyDefinition<com.vaadin.tests.data.bean.Person, ?> deserializedDefinition = ClassesSerializableTest
                .serializeAndDeserialize(definition);

        ValueProvider<com.vaadin.tests.data.bean.Person, ?> getter = deserializedDefinition
                .getGetter();
        Address address = new Address("Ruukinkatu 2-4", 20540, "Turku",
                Country.FINLAND);
        com.vaadin.tests.data.bean.Person person = new com.vaadin.tests.data.bean.Person(
                "Jon", "Doe", "jon.doe@vaadin.com", 32, Sex.MALE, address);

        Integer postalCode = (Integer) getter.apply(person);

        assertEquals("Deserialized definition should be functional",
                address.getPostalCode(), postalCode);

    }

    @Test
    public void nestedPropertyDefinition_samePropertyNameOnMultipleLevels() {
        PropertyDefinition<FatherAndSon, ?> definition = BeanPropertySet
                .get(FatherAndSon.class).getProperty("father.father.firstName")
                .orElseThrow(RuntimeException::new);

        ValueProvider<FatherAndSon, ?> getter = definition.getGetter();

        FatherAndSon grandFather = new FatherAndSon("Grand Old Jon", "Doe",
                null, null);
        FatherAndSon father = new FatherAndSon("Old Jon", "Doe", grandFather,
                null);
        FatherAndSon son = new FatherAndSon("Jon", "Doe", father, null);

        String firstName = (String) getter.apply(son);

        assertEquals(grandFather.getFirstName(), firstName);
    }

    @Test(expected = NullPointerException.class)
    public void nestedPropertyDefinition_propertyChainBroken() {
        PropertyDefinition<FatherAndSon, ?> definition = BeanPropertySet
                .get(FatherAndSon.class).getProperty("father.firstName")
                .orElseThrow(RuntimeException::new);
        ValueProvider<FatherAndSon, ?> getter = definition.getGetter();
        getter.apply(new FatherAndSon("Jon", "Doe", null, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nestedPropertyDefinition_invalidPropertyNameInChain() {
        BeanPropertySet.get(FatherAndSon.class)
                .getProperty("grandfather.firstName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nestedPropertyDefinition_invalidPropertyNameAtChainEnd() {
        BeanPropertySet.get(FatherAndSon.class).getProperty("father.age");
    }

    @Test
    public void properties() {
        PropertySet<Person> propertySet = BeanPropertySet.get(Person.class);

        Set<String> propertyNames = propertySet.getProperties()
                .map(PropertyDefinition::getName).collect(Collectors.toSet());

        assertEquals(new HashSet<>(Arrays.asList("name", "born")),
                propertyNames);
    }
}
