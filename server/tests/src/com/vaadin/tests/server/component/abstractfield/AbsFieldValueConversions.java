package com.vaadin.tests.server.component.abstractfield;

import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class AbsFieldValueConversions extends TestCase {

    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com",
            34, Sex.FEMALE, new Address("Paula street 1", 12345, "P-town",
                    Country.FINLAND));

    /**
     * Java uses a non-breaking space (ascii 160) instead of space when
     * formatting
     */
    private static final char FORMATTED_SPACE = 160;

    public void testWithoutConversion() {
        TextField tf = new TextField();
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean,
                "firstName"));
        assertEquals("Paula", tf.getValue());
        assertEquals("Paula", tf.getPropertyDataSource().getValue());
        tf.setValue("abc");
        assertEquals("abc", tf.getValue());
        assertEquals("abc", tf.getPropertyDataSource().getValue());
        assertEquals("abc", paulaBean.getFirstName());
    }

    public void testNonmodifiedBufferedFieldConversion() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        TextField tf = new TextField("salary");
        tf.setBuffered(true);
        tf.setLocale(new Locale("en", "US"));
        ObjectProperty<Integer> ds = new ObjectProperty<Integer>(123456789);
        tf.setPropertyDataSource(ds);
        assertEquals((Integer) 123456789, ds.getValue());
        assertEquals("123,456,789", tf.getValue());
        tf.setLocale(new Locale("fi", "FI"));
        assertEquals((Integer) 123456789, ds.getValue());
        assertEquals("123" + FORMATTED_SPACE + "456" + FORMATTED_SPACE + "789",
                tf.getValue());

    }

    public void testModifiedBufferedFieldConversion() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        TextField tf = new TextField("salary");
        tf.setBuffered(true);
        tf.setLocale(new Locale("en", "US"));
        ObjectProperty<Integer> ds = new ObjectProperty<Integer>(123456789);
        tf.setPropertyDataSource(ds);
        assertEquals((Integer) 123456789, ds.getValue());
        assertEquals("123,456,789", tf.getValue());
        tf.setValue("123,123");
        assertEquals((Integer) 123456789, ds.getValue());
        assertEquals("123,123", tf.getValue());

        tf.setLocale(new Locale("fi", "FI"));
        assertEquals((Integer) 123456789, ds.getValue());
        // Value should not be updated when field is buffered
        assertEquals("123,123", tf.getValue());
    }

    public void testStringIdentityConversion() {
        TextField tf = new TextField();
        tf.setConverter(new Converter<String, String>() {

            @Override
            public String convertToModel(String value,
                    Class<? extends String> targetType, Locale locale) {
                return value;
            }

            @Override
            public String convertToPresentation(String value,
                    Class<? extends String> targetType, Locale locale) {
                return value;
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
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean,
                "firstName"));
        assertEquals("Paula", tf.getValue());
        assertEquals("Paula", tf.getPropertyDataSource().getValue());
        tf.setValue("abc");
        assertEquals("abc", tf.getValue());
        assertEquals("abc", tf.getPropertyDataSource().getValue());
        assertEquals("abc", paulaBean.getFirstName());
    }

    public void testIntegerStringConversion() {
        TextField tf = new TextField();

        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new MethodProperty<Integer>(paulaBean, "age"));
        assertEquals(34, tf.getPropertyDataSource().getValue());
        assertEquals("34", tf.getValue());
        tf.setValue("12");
        assertEquals(12, tf.getPropertyDataSource().getValue());
        assertEquals("12", tf.getValue());
        tf.getPropertyDataSource().setValue(42);
        assertEquals(42, tf.getPropertyDataSource().getValue());
        assertEquals("42", tf.getValue());
    }

    public void testChangeReadOnlyFieldLocale() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));

        TextField tf = new TextField("salary");
        tf.setLocale(new Locale("en", "US"));
        ObjectProperty<Integer> ds = new ObjectProperty<Integer>(123456789);
        ds.setReadOnly(true);
        tf.setPropertyDataSource(ds);
        assertEquals((Integer) 123456789, ds.getValue());
        assertEquals("123,456,789", tf.getValue());
        tf.setLocale(new Locale("fi", "FI"));
        assertEquals((Integer) 123456789, ds.getValue());
        assertEquals("123" + FORMATTED_SPACE + "456" + FORMATTED_SPACE + "789",
                tf.getValue());
    }

    public void testBooleanNullConversion() {
        CheckBox cb = new CheckBox();
        cb.setConverter(new Converter<Boolean, Boolean>() {

            @Override
            public Boolean convertToModel(Boolean value,
                    Class<? extends Boolean> targetType, Locale locale) {
                // value from a CheckBox should never be null as long as it is
                // not set to null (handled by conversion below).
                assertNotNull(value);
                return value;
            }

            @Override
            public Boolean convertToPresentation(Boolean value,
                    Class<? extends Boolean> targetType, Locale locale) {
                // Datamodel -> field
                if (value == null) {
                    return false;
                }

                return value;
            }

            @Override
            public Class<Boolean> getModelType() {
                return Boolean.class;
            }

            @Override
            public Class<Boolean> getPresentationType() {
                return Boolean.class;
            }

        });
        MethodProperty<Boolean> property = new MethodProperty<Boolean>(
                paulaBean, "deceased");
        cb.setPropertyDataSource(property);
        assertEquals(Boolean.FALSE, property.getValue());
        assertEquals(Boolean.FALSE, cb.getValue());
        Boolean newDmValue = cb.getConverter().convertToPresentation(
                cb.getValue(), Boolean.class, new Locale("fi", "FI"));
        assertEquals(Boolean.FALSE, newDmValue);

        // FIXME: Should be able to set to false here to cause datamodel to be
        // set to false but the change will not be propagated to the Property
        // (field value is already false)

        cb.setValue(true);
        assertEquals(Boolean.TRUE, cb.getValue());
        assertEquals(Boolean.TRUE, property.getValue());

        cb.setValue(false);
        assertEquals(Boolean.FALSE, cb.getValue());
        assertEquals(Boolean.FALSE, property.getValue());

    }

    // Now specific to Integer because StringToNumberConverter has been removed
    public static class NumberBean {
        private Integer number;

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

    }

    public void testNumberDoubleConverterChange() {
        final VaadinSession a = new AlwaysLockedVaadinSession(null);
        VaadinSession.setCurrent(a);
        TextField tf = new TextField() {
            @Override
            public VaadinSession getSession() {
                return a;
            }
        };
        NumberBean nb = new NumberBean();
        nb.setNumber(490);

        tf.setPropertyDataSource(new MethodProperty<Number>(nb, "number"));
        assertEquals(490, tf.getPropertyDataSource().getValue());
        assertEquals("490", tf.getValue());

        Converter c1 = tf.getConverter();

        tf.setPropertyDataSource(new MethodProperty<Number>(nb, "number"));
        Converter c2 = tf.getConverter();
        assertTrue(
                "StringToInteger converter is ok for integer types and should stay even though property is changed",
                c1 == c2);
        assertEquals(490, tf.getPropertyDataSource().getValue());
        assertEquals("490", tf.getValue());

    }

    @Test
    public void testNullConverter() {
        TextField tf = new TextField("foo");
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new ObjectProperty<Integer>(12));
        tf.setConverter((Converter) null);
        try {
            Object v = tf.getConvertedValue();
            System.out.println(v);
            Assert.fail("Trying to convert String -> Integer should fail when there is no converter");
        } catch (ConversionException e) {
            // ok, should happen when there is no converter but conversion is
            // needed
        }
    }

}
