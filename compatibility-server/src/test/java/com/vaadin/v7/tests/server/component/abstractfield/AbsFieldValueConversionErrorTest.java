package com.vaadin.v7.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.TextField;

public class AbsFieldValueConversionErrorTest {

    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com",
            34, Sex.FEMALE,
            new Address("Paula street 1", 12345, "P-town", Country.FINLAND));

    @Test
    public void testValidateConversionErrorParameters() {
        TextField tf = new TextField();
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean, "age"));
        tf.setConversionError("(Type: {0}) Converter exception message: {1}");
        tf.setValue("abc");
        try {
            tf.validate();
            fail();
        } catch (InvalidValueException e) {
            assertEquals(
                    "(Type: Integer) Converter exception message: Could not convert 'abc' to java.lang.Integer",
                    e.getMessage());
        }

    }

    @Test
    public void testConvertToModelConversionErrorParameters() {
        TextField tf = new TextField();
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean, "age"));
        tf.setConversionError("(Type: {0}) Converter exception message: {1}");
        tf.setValue("abc");
        try {
            tf.getConvertedValue();
            fail();
        } catch (ConversionException e) {
            assertEquals(
                    "(Type: Integer) Converter exception message: Could not convert 'abc' to java.lang.Integer",
                    e.getMessage());
        }

    }

    @Test
    public void testNullConversionMessages() {
        TextField tf = new TextField();
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean, "age"));
        tf.setConversionError(null);
        tf.setValue("abc");
        try {
            tf.validate();
            fail();
        } catch (InvalidValueException e) {
            assertEquals(null, e.getMessage());
        }

    }

    @Test
    public void testDefaultConversionErrorMessage() {
        TextField tf = new TextField();
        tf.setConverter(new StringToIntegerConverter());
        tf.setPropertyDataSource(new MethodProperty<String>(paulaBean, "age"));
        tf.setValue("abc");

        try {
            tf.validate();
            fail();
        } catch (InvalidValueException e) {
            assertEquals("Could not convert value to Integer", e.getMessage());
        }

    }
}
