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
package com.vaadin.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.provider.bov.Person;
import com.vaadin.tests.server.ClassesSerializableTest;

public class BeanBinderPropertySetTest {
    @Test
    public void testSerializeDeserialize_propertySet() throws Exception {
        BinderPropertySet<Person> originalPropertySet = BeanBinderPropertySet
                .get(Person.class);

        BinderPropertySet<Person> deserializedPropertySet = ClassesSerializableTest
                .serializeAndDeserialize(originalPropertySet);

        Assert.assertSame(
                "Deserialized instance should be the same as the original",
                originalPropertySet, deserializedPropertySet);
    }

    @Test
    public void testSerializeDeserialize_propertySet_cacheCleared()
            throws Exception {
        BinderPropertySet<Person> originalPropertySet = BeanBinderPropertySet
                .get(Person.class);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bs);
        out.writeObject(originalPropertySet);
        byte[] data = bs.toByteArray();

        // Simulate deserializing into a different JVM by clearing the instance
        // map
        Field instancesField = BeanBinderPropertySet.class
                .getDeclaredField("instances");
        instancesField.setAccessible(true);
        Map<?, ?> instances = (Map<?, ?>) instancesField.get(null);
        instances.clear();

        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data));
        BinderPropertySet<Person> deserializedPropertySet = (BinderPropertySet<Person>) in
                .readObject();

        Assert.assertSame(
                "Deserialized instance should be the same as in the cache",
                BeanBinderPropertySet.get(Person.class),
                deserializedPropertySet);
        Assert.assertNotSame(
                "Deserialized instance should not be the same as the original",
                originalPropertySet, deserializedPropertySet);
    }

    @Test
    public void testSerializeDeserialize_propertyDefinition() throws Exception {
        BinderPropertyDefinition<Person, ?> definition = BeanBinderPropertySet
                .get(Person.class).getProperty("born")
                .orElseThrow(RuntimeException::new);

        BinderPropertyDefinition<Person, ?> deserializedDefinition = ClassesSerializableTest
                .serializeAndDeserialize(definition);

        ValueProvider<Person, ?> getter = deserializedDefinition.getGetter();
        Person person = new Person("Milennial", 2000);
        Integer age = (Integer) getter.apply(person);

        Assert.assertEquals("Deserialized definition should be functional",
                Integer.valueOf(2000), age);

        Assert.assertSame(
                "Deserialized instance should be the same as in the cache",
                BeanBinderPropertySet.get(Person.class).getProperty("born")
                        .orElseThrow(RuntimeException::new),
                deserializedDefinition);
    }

    @Test
    public void properties() {
        BinderPropertySet<Person> propertySet = BeanBinderPropertySet
                .get(Person.class);

        Set<String> propertyNames = propertySet.getProperties()
                .map(BinderPropertyDefinition::getName)
                .collect(Collectors.toSet());

        Assert.assertEquals(new HashSet<>(Arrays.asList("name", "born")),
                propertyNames);
    }
}
