package com.vaadin.tests.server.component.abstractfield;

import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class AbstractFieldValueConversions extends TestCase {

    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com",
            34, Sex.FEMALE, new Address("Paula street 1", 12345, "P-town",
                    Country.FINLAND));

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

    public void testStringIdentityConversion() {
        TextField tf = new TextField();
        tf.setConverter(new Converter<String, String>() {

            @Override
            public String convertToModel(String value, Locale locale) {
                return value;
            }

            @Override
            public String convertToPresentation(String value, Locale locale) {
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

    public void testBooleanNullConversion() {
        CheckBox cb = new CheckBox();
        cb.setConverter(new Converter<Boolean, Boolean>() {

            @Override
            public Boolean convertToModel(Boolean value, Locale locale) {
                // value from a CheckBox should never be null as long as it is
                // not set to null (handled by conversion below).
                assertNotNull(value);
                return value;
            }

            @Override
            public Boolean convertToPresentation(Boolean value, Locale locale) {
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
                cb.getValue(), new Locale("fi", "FI"));
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

    public static class NumberBean {
        private Number number;

        public Number getNumber() {
            return number;
        }

        public void setNumber(Number number) {
            this.number = number;
        }

    }

    public void testNumberDoubleConverterChange() {
        final VaadinServiceSession a = new VaadinServiceSession(null);
        VaadinServiceSession.setCurrent(a);
        TextField tf = new TextField() {
            @Override
            public VaadinServiceSession getSession() {
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
                "StringToNumber converter is ok for integer types and should stay even though property is changed",
                c1 == c2);
        assertEquals(490, tf.getPropertyDataSource().getValue());
        assertEquals("490", tf.getValue());

    }

}
