package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;

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
            public Class<?> getPropertyHolderType() {
                return MyBeanWithoutGetters.class;
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
            public Class<?> getPropertyHolderType() {
                return MyBeanWithoutGetters.class;
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
        assertEquals(0, customGrid.getColumns().size());

        Column<MyBeanWithoutGetters, Integer> numberColumn = (Column<MyBeanWithoutGetters, Integer>) customGrid
                .addColumn("numbah");
        assertEquals(1, customGrid.getColumns().size());
        assertEquals("The Number", numberColumn.getCaption());
        assertEquals(24, (int) numberColumn.getValueProvider()
                .apply(new MyBeanWithoutGetters("foo", 24)));

        Column<MyBeanWithoutGetters, String> stringColumn = (Column<MyBeanWithoutGetters, String>) customGrid
                .addColumn("string");
        assertEquals(2, customGrid.getColumns().size());
        assertEquals("The String", stringColumn.getCaption());
        assertEquals("foo", stringColumn.getValueProvider()
                .apply(new MyBeanWithoutGetters("foo", 24)));
    }

}
