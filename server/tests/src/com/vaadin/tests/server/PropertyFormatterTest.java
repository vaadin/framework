package com.vaadin.tests.server;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyFormatter;

@SuppressWarnings("unchecked")
public class PropertyFormatterTest extends TestCase {

    class TestFormatter extends PropertyFormatter {

        @Override
        public String format(Object value) {
            boolean isCorrectType = getExpectedClass().isAssignableFrom(
                    value.getClass());
            assertTrue(isCorrectType);
            return "FOO";
        }

        @Override
        public Object parse(String formattedValue) throws Exception {
            return getExpectedClass().newInstance();
        }
    }

    @SuppressWarnings("rawtypes")
    private Class expectedClass;

    @SuppressWarnings("rawtypes")
    private Class getExpectedClass() {
        return expectedClass;
    }

    /**
     * The object passed to format should be same as property's type.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Test
    @SuppressWarnings({ "rawtypes" })
    public void testCorrectTypeForFormat() throws InstantiationException,
            IllegalAccessException {
        Class[] testedTypes = new Class[] { Integer.class, Boolean.class,
                Double.class, String.class, Date.class };
        Object[] testValues = new Object[] { new Integer(3), Boolean.FALSE,
                new Double(3.3), "bar", new Date() };

        int i = 0;
        for (Class class1 : testedTypes) {
            expectedClass = class1;

            TestFormatter formatter = new TestFormatter();

            // Should just return null, without formatting
            Object value = formatter.getValue();

            // test with property which value is null
            formatter.setPropertyDataSource(new ObjectProperty(null,
                    expectedClass));
            formatter.getValue(); // calls format

            // test with a value
            formatter.setPropertyDataSource(new ObjectProperty(testValues[i++],
                    expectedClass));
            formatter.getValue(); // calls format
        }

    }
}
