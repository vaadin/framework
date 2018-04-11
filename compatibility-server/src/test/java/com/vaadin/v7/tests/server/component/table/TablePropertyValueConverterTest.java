package com.vaadin.v7.tests.server.component.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.Table;

public class TablePropertyValueConverterTest {
    protected TestableTable table;
    protected Collection<?> initialProperties;

    @Test
    public void testRemovePropertyId() {
        Collection<Object> converters = table.getCurrentConverters();
        assertFalse("Set of converters was empty at the start.",
                converters.isEmpty());

        Object firstId = converters.iterator().next();

        table.removeContainerProperty(firstId);

        Collection<Object> converters2 = table.getCurrentConverters();
        assertTrue("FirstId was not removed", !converters2.contains(firstId));

        assertTrue("The number of removed converters was not one.",
                converters.size() - converters2.size() == 1);

        for (Object originalId : converters) {
            if (!originalId.equals(firstId)) {
                assertTrue("The wrong converter was removed.",
                        converters2.contains(originalId));
            }
        }

    }

    @Test
    public void testSetContainer() {
        table.setContainerDataSource(createContainer(
                new String[] { "col1", "col3", "col4", "col5" }));
        Collection<Object> converters = table.getCurrentConverters();
        assertTrue("There should only have been one converter left.",
                converters.size() == 1);
        Object onlyKey = converters.iterator().next();
        assertTrue("The incorrect key was left.", onlyKey.equals("col1"));

    }

    @Test
    public void testSetContainerWithInexactButCompatibleTypes() {
        TestableTable customTable = new TestableTable("Test table",
                createContainer(new String[] { "col1", "col2", "col3" },
                        new Class[] { String.class, BaseClass.class,
                                DerivedClass.class }));
        customTable.setConverter("col1", new Converter<String, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String convertToModel(String value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "model";
            }

            @Override
            public String convertToPresentation(String value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "presentation";
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

        });
        customTable.setConverter("col2", new Converter<String, BaseClass>() {
            private static final long serialVersionUID = 1L;

            @Override
            public BaseClass convertToModel(String value,
                    Class<? extends BaseClass> targetType, Locale locale)
                    throws ConversionException {
                return new BaseClass("model");
            }

            @Override
            public Class<BaseClass> getModelType() {
                return BaseClass.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

            @Override
            public String convertToPresentation(BaseClass value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return null;
            }
        });
        customTable.setConverter("col3", new Converter<String, DerivedClass>() {
            private static final long serialVersionUID = 1L;

            @Override
            public DerivedClass convertToModel(String value,
                    Class<? extends DerivedClass> targetType, Locale locale)
                    throws ConversionException {
                return new DerivedClass("derived" + 1001);
            }

            @Override
            public Class<DerivedClass> getModelType() {
                return DerivedClass.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

            @Override
            public String convertToPresentation(DerivedClass value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return null;
            }
        });
        customTable.setContainerDataSource(
                createContainer(new String[] { "col1", "col2", "col3" },
                        new Class[] { DerivedClass.class, DerivedClass.class,
                                BaseClass.class }));
        Set<Object> converters = customTable.getCurrentConverters();
        // TODO Test temporarily disabled as this feature
        // is not yet implemented in Table
        /*
         * assertTrue("Incompatible types were not removed.", converters.size()
         * <= 1); assertTrue("Even compatible types were removed",
         * converters.size() == 1); assertTrue("Compatible type was missing.",
         * converters.contains("col2"));
         */
    }

    @Test
    public void testPrimitiveTypeConverters() {
        TestableTable customTable = new TestableTable("Test table",
                createContainer(new String[] { "col1", "col2", "col3" },
                        new Class[] { int.class, BaseClass.class,
                                DerivedClass.class }));
        customTable.setConverter("col1", new Converter<String, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer convertToModel(String value,
                    Class<? extends Integer> targetType, Locale locale)
                    throws ConversionException {
                return 11;
            }

            @Override
            public String convertToPresentation(Integer value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "presentation";
            }

            @Override
            public Class<Integer> getModelType() {
                return Integer.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        Set<Object> converters = customTable.getCurrentConverters();
        assertFalse("Converter was not set.", converters.isEmpty());
    }

    @Test
    public void testInheritance() {
        assertTrue("BaseClass isn't assignable from DerivedClass",
                BaseClass.class.isAssignableFrom(DerivedClass.class));
        assertFalse("DerivedClass is assignable from BaseClass",
                DerivedClass.class.isAssignableFrom(BaseClass.class));
    }

    @Before
    public void setUp() {
        table = new TestableTable("Test table",
                createContainer(new String[] { "col1", "col2", "col3" }));
        table.setConverter("col1", new Converter<String, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String convertToModel(String value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "model";
            }

            @Override
            public String convertToPresentation(String value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "presentation";
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

        });

        table.setConverter("col2", new Converter<String, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String convertToModel(String value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "model2";
            }

            @Override
            public String convertToPresentation(String value,
                    Class<? extends String> targetType, Locale locale)
                    throws ConversionException {
                return "presentation2";
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

        });

        initialProperties = table.getContainerPropertyIds();
    }

    private static Container createContainer(Object[] ids) {
        Class[] types = new Class[ids.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = String.class;
        }
        return createContainer(ids, types);
    }

    private static Container createContainer(Object[] ids, Class[] types) {
        IndexedContainer container = new IndexedContainer();
        if (ids.length > types.length) {
            throw new IllegalArgumentException("Too few defined types");
        }
        for (int i = 0; i < ids.length; ++i) {
            container.addContainerProperty(ids[i], types[i], "");
        }

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("item " + i);
            for (int j = 0; j < ids.length; ++j) {
                Property itemProperty = item.getItemProperty(ids[j]);
                if (types[j] == String.class) {
                    itemProperty.setValue(ids[j].toString() + i);
                } else if (types[j] == BaseClass.class) {
                    itemProperty.setValue(new BaseClass("base" + i));
                } else if (types[j] == DerivedClass.class) {
                    itemProperty.setValue(new DerivedClass("derived" + i));
                } else if (types[j] == int.class) {
                    // FIXME can't set values because the int is autoboxed into
                    // an Integer and not unboxed prior to set

                    // itemProperty.setValue(i);
                } else {
                    throw new IllegalArgumentException(
                            "Unhandled type in createContainer: " + types[j]);
                }
            }
        }

        return container;
    }

    private class TestableTable extends Table {
        /**
         * @param string
         * @param createContainer
         */
        public TestableTable(String string, Container container) {
            super(string, container);
        }

        Set<Object> getCurrentConverters() {
            try {
                Field f = Table.class
                        .getDeclaredField("propertyValueConverters");
                f.setAccessible(true);
                Map<Object, Converter<String, Object>> pvc = (Map<Object, Converter<String, Object>>) f
                        .get(this);
                Set<Object> currentConverters = new HashSet<Object>();
                for (Entry<Object, Converter<String, Object>> entry : pvc
                        .entrySet()) {
                    currentConverters.add(entry.getKey());
                }
                return currentConverters;

            } catch (Exception e) {
                fail("Unable to retrieve propertyValueConverters");
                return null;
            }
        }
    }

    private static class BaseClass {
        private String title;

        public BaseClass(String title) {
            this.title = title;
        }
    }

    private static class DerivedClass extends BaseClass {
        public DerivedClass(String title) {
            super(title);
        }
    }
}
