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
package com.vaadin.tests.server.component.grid;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class GridCustomPropertySetTest {

    public static class MyBeanWithoutGetters {
        public String str;
        public int number;

        public MyBeanWithoutGetters(String str, int number) {
            this.str = str;
            this.number = number;
        }
    }

    public static class GridWithCustomPropertySet
            extends Grid<MyBeanWithoutGetters> {

        private final class MyBeanPropertySet
                implements PropertySet<MyBeanWithoutGetters> {

            private PropertyDefinition<MyBeanWithoutGetters, String> strDef = new StrDefinition(
                    this);
            private PropertyDefinition<MyBeanWithoutGetters, Integer> numberDef = new NumberDefinition(
                    this);

            @Override
            public Stream<PropertyDefinition<MyBeanWithoutGetters, ?>> getProperties() {
                return Stream.of(strDef, numberDef);
            }

            @Override
            public Optional<PropertyDefinition<MyBeanWithoutGetters, ?>> getProperty(
                    String name) {
                return getProperties().filter(pd -> pd.getName().equals(name))
                        .findFirst();
            }
        }

        private final class StrDefinition
                implements PropertyDefinition<MyBeanWithoutGetters, String> {
            private PropertySet<MyBeanWithoutGetters> propertySet;

            public StrDefinition(
                    PropertySet<MyBeanWithoutGetters> propertySet) {
                this.propertySet = propertySet;
            }

            @Override
            public ValueProvider<MyBeanWithoutGetters, String> getGetter() {
                return bean -> bean.str;
            }

            @Override
            public Optional<Setter<MyBeanWithoutGetters, String>> getSetter() {
                return Optional.of((bean, value) -> bean.str = value);
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public String getName() {
                return "string";
            }

            @Override
            public String getCaption() {
                return "The String";
            }

            @Override
            public PropertySet<MyBeanWithoutGetters> getPropertySet() {
                return propertySet;
            }

        }

        private final class NumberDefinition
                implements PropertyDefinition<MyBeanWithoutGetters, Integer> {
            private PropertySet<MyBeanWithoutGetters> propertySet;

            public NumberDefinition(
                    PropertySet<MyBeanWithoutGetters> propertySet) {
                this.propertySet = propertySet;
            }

            @Override
            public ValueProvider<MyBeanWithoutGetters, Integer> getGetter() {
                return bean -> bean.number;
            }

            @Override
            public Optional<Setter<MyBeanWithoutGetters, Integer>> getSetter() {
                return Optional.of((bean, value) -> bean.number = value);
            }

            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }

            @Override
            public String getName() {
                return "numbah";
            }

            @Override
            public String getCaption() {
                return "The Number";
            }

            @Override
            public PropertySet<MyBeanWithoutGetters> getPropertySet() {
                return propertySet;
            }

        }

        public GridWithCustomPropertySet() {
            super();
            setPropertySet(new MyBeanPropertySet());
        }

    }

    @Test
    public void customPropertySet() {
        GridWithCustomPropertySet customGrid = new GridWithCustomPropertySet();
        Assert.assertEquals(0, customGrid.getColumns().size());

        Column<MyBeanWithoutGetters, Integer> numberColumn = (Column<MyBeanWithoutGetters, Integer>) customGrid
                .addColumn("numbah");
        Assert.assertEquals(1, customGrid.getColumns().size());
        Assert.assertEquals("The Number", numberColumn.getCaption());
        Assert.assertEquals(24, (int) numberColumn.getValueProvider()
                .apply(new MyBeanWithoutGetters("foo", 24)));

        Column<MyBeanWithoutGetters, String> stringColumn = (Column<MyBeanWithoutGetters, String>) customGrid
                .addColumn("string");
        Assert.assertEquals(2, customGrid.getColumns().size());
        Assert.assertEquals("The String", stringColumn.getCaption());
        Assert.assertEquals("foo", stringColumn.getValueProvider()
                .apply(new MyBeanWithoutGetters("foo", 24)));
    }

}
