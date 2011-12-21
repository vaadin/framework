package com.vaadin.tests.server.component.abstractfield;

import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.IntegerToStringConverter;
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

            public String convertFromTargetToSource(String value, Locale locale) {
                return value;
            }

            public String convertFromSourceToTarget(String value, Locale locale) {
                return value;
            }

            public Class<String> getSourceType() {
                return String.class;
            }

            public Class<String> getTargetType() {
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

    public void testFailingConversion() {
        TextField tf = new TextField();
        tf.setConverter(new Converter<Integer, String>() {

            public Integer convertFromTargetToSource(String value, Locale locale) {
                throw new ConversionException("Failed");
            }

            public String convertFromSourceToTarget(Integer value, Locale locale) {
                throw new ConversionException("Failed");
            }

            public Class<Integer> getSourceType() {
                // TODO Auto-generated method stub
                return null;
            }

            public Class<String> getTargetType() {
                // TODO Auto-generated method stub
                return null;
            }
        });
        try {
            tf.setValue(1);
            fail("setValue(Integer) should throw an exception");
        } catch (Converter.ConversionException e) {
            // OK, expected
        }
    }

    public void testIntegerStringConversion() {
        TextField tf = new TextField();

        tf.setConverter(new IntegerToStringConverter());
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

            public Boolean convertFromTargetToSource(Boolean value,
                    Locale locale) {
                // value from a CheckBox should never be null as long as it is
                // not set to null (handled by conversion below).
                assertNotNull(value);
                return value;
            }

            public Boolean convertFromSourceToTarget(Boolean value,
                    Locale locale) {
                // Datamodel -> field
                if (value == null) {
                    return false;
                }

                return value;
            }

            public Class<Boolean> getSourceType() {
                return Boolean.class;
            }

            public Class<Boolean> getTargetType() {
                return Boolean.class;
            }

        });
        MethodProperty<Boolean> property = new MethodProperty<Boolean>(
                paulaBean, "deceased");
        cb.setPropertyDataSource(property);
        assertEquals(Boolean.FALSE, property.getValue());
        assertEquals(Boolean.FALSE, cb.getValue());
        Boolean newDmValue = cb.getConverter().convertFromSourceToTarget(
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

}
