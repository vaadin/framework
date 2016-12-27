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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.provider.bov.Person;

public class BeanBinderPropertySetTest {
    @Test
    public void testSerializeDeserialize() throws Exception {
        BinderPropertyDefinition<Person, ?> definition = BeanBinderPropertySet
                .get(Person.class).getProperty("born")
                .orElseThrow(RuntimeException::new);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(definition);
        out.flush();

        ObjectInputStream inputStream = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));

        BinderPropertyDefinition<Person, ?> deserializedDefinition = (BinderPropertyDefinition<Person, ?>) inputStream
                .readObject();

        ValueProvider<Person, ?> getter = deserializedDefinition.getGetter();
        Person person = new Person("Milennial", 2000);
        Integer age = (Integer) getter.apply(person);

        Assert.assertEquals(Integer.valueOf(2000), age);
    }
}
