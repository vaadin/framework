package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.DefaultConverterFactory;

public class DefaultConverterFactoryTest {

    private DefaultConverterFactory factory = new DefaultConverterFactory();

    @Test
    public void stringToBigDecimal() {
        assertConverter("14", new BigDecimal("14"));
    }

    @Test
    public void stringToBigInteger() {
        assertConverter("14", new BigInteger("14"));
    }

    @Test
    public void stringToDouble() {
        assertConverter("14", new Double("14"));
    }

    @Test
    public void stringToFloat() {
        assertConverter("14", new Float("14"));
    }

    @Test
    public void stringToInteger() {
        assertConverter("14", new Integer("14"));
    }

    @Test
    public void stringToLong() {
        assertConverter("14", new Long("14"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void stringToDate() {
        assertConverter("Oct 12, 2014 12:00:00 AM",
                new Date(2014 - 1900, 10 - 1, 12));
    }

    @Test
    public void sqlDateToDate() {
        long l = 1413071210000L;
        assertConverter(new java.sql.Date(l), new java.util.Date(l));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void longToDate() {
        Date d = new Date(2014 - 1900, 10 - 1, 12);
        assertConverter(
                1413061200000L + (d.getTimezoneOffset() + 180) * 60 * 1000L, d);
    }

    public enum Foo {
        BAR, BAZ;
    }

    @Test
    public void stringToEnum() {
        assertConverter("Bar", Foo.BAR);
    }

    @Test
    public void stringToShort() {
        assertConverter("14", new Short("14"));
    }

    @Test
    public void stringToByte() {
        assertConverter("14", new Byte("14"));
    }

    private <T, U> void assertConverter(T t, U u) {
        Class<T> tClass = (Class<T>) t.getClass();
        Class<U> uClass = (Class<U>) u.getClass();

        U tConvertedToU = factory.createConverter(tClass, uClass)
                .convertToModel(t, uClass, Locale.ENGLISH);
        assertEquals("Incorrect type of value converted from "
                + tClass.getSimpleName() + " to " + uClass.getSimpleName(),
                uClass, tConvertedToU.getClass());
        assertEquals("Incorrect conversion of " + t + " to "
                + uClass.getSimpleName(), u, tConvertedToU);

        T uConvertedToT = factory.createConverter(uClass, tClass)
                .convertToModel(u, tClass, Locale.ENGLISH);
        assertEquals("Incorrect type of value converted from "
                + uClass.getSimpleName() + " to " + tClass.getSimpleName(),
                tClass, uConvertedToT.getClass());
        assertEquals("Incorrect conversion of " + u + " to "
                + tClass.getSimpleName(), t, uConvertedToT);

    }

}
