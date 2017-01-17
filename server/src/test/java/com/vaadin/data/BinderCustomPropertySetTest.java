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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.server.Setter;
import com.vaadin.ui.TextField;

public class BinderCustomPropertySetTest {
    public static class MapPropertyDefinition
            implements BinderPropertyDefinition<Map<String, String>, String> {

        private MapPropertySet propertySet;
        private String name;

        public MapPropertyDefinition(MapPropertySet propertySet, String name) {
            this.propertySet = propertySet;
            this.name = name;
        }

        @Override
        public ValueProvider<Map<String, String>, String> getGetter() {
            return map -> map.get(name);
        }

        @Override
        public Optional<Setter<Map<String, String>, String>> getSetter() {
            return Optional.of((map, value) -> {
                if (value == null) {
                    map.remove(name);
                } else {
                    map.put(name, value);
                }
            });
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }

        @Override
        public BindingBuilder<Map<String, String>, String> beforeBind(
                BindingBuilder<Map<String, String>, String> originalBuilder) {
            return originalBuilder;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public BinderPropertySet<Map<String, String>> getPropertySet() {
            return propertySet;
        }

    }

    public static class MapPropertySet
            implements BinderPropertySet<Map<String, String>> {
        @Override
        public Stream<BinderPropertyDefinition<Map<String, String>, ?>> getProperties() {
            return Stream.of("one", "two", "three").map(this::createProperty);
        }

        @Override
        public Optional<BinderPropertyDefinition<Map<String, String>, ?>> getProperty(
                String name) {
            return Optional.of(createProperty(name));
        }

        private BinderPropertyDefinition<Map<String, String>, ?> createProperty(
                String name) {
            return new MapPropertyDefinition(this, name);
        }
    }

    public static class InstanceFields {
        private TextField one;
        private TextField another;
    }

    @Test
    public void testBindByString() {
        TextField field = new TextField();
        Map<String, String> map = new HashMap<>();
        Binder<Map<String, String>> binder = Binder
                .withPropertySet(new MapPropertySet());

        binder.bind(field, "key");
        binder.setBean(map);

        field.setValue("value");
        Assert.assertEquals(
                "Field value should propagate to the corresponding key in the map",
                "value", map.get("key"));
    }

    @Test
    public void testBindInstanceFields() {
        Map<String, String> map = new HashMap<>();
        Binder<Map<String, String>> binder = Binder
                .withPropertySet(new MapPropertySet());
        InstanceFields instanceFields = new InstanceFields();

        binder.bindInstanceFields(instanceFields);

        Assert.assertNotNull(
                "Field corresponding to supported property name should be bound",
                instanceFields.one);
        Assert.assertNull(
                "Field corresponding to unsupported property name should be ignored",
                instanceFields.another);

        binder.setBean(map);

        instanceFields.one.setValue("value");
        Assert.assertEquals(
                "Field value should propagate to the corresponding key in the map",
                "value", map.get("one"));
    }
}
