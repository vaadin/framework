package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.data.util.converter.StringToEnumConverter;

public class StringToEnumConverterTest extends TestCase {

    public static enum FooEnum {
        VALUE1, SOME_VALUE, FOO_BAR_BAZ, Bar, nonStandardCase, _HUGH;
    }

    StringToEnumConverter converter = new StringToEnumConverter();
    Converter<Enum, String> reverseConverter = new ReverseConverter<Enum, String>(
            converter);

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", Enum.class, null));
    }

    public void testInvalidEnumClassConversion() {
        try {
            converter.convertToModel("Foo", Enum.class, null);
            fail("No exception thrown");
        } catch (ConversionException e) {
            // OK
        }
    }

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Enum.class, null));
    }

    public void testReverseNullConversion() {
        assertEquals(null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    public void testValueConversion() {
        assertEquals(FooEnum.VALUE1,
                converter.convertToModel("Value1", FooEnum.class, null));
        assertEquals(FooEnum.SOME_VALUE,
                converter.convertToModel("Some value", FooEnum.class, null));
        assertEquals(FooEnum.FOO_BAR_BAZ,
                converter.convertToModel("Foo bar baz", FooEnum.class, null));
        assertEquals(FooEnum.Bar,
                converter.convertToModel("Bar", FooEnum.class, null));
        assertEquals(FooEnum.nonStandardCase, converter.convertToModel(
                "Nonstandardcase", FooEnum.class, null));
        assertEquals(FooEnum._HUGH,
                converter.convertToModel("_hugh", FooEnum.class, null));
    }

    public void testReverseValueConversion() {
        assertEquals("Value1", reverseConverter.convertToModel(FooEnum.VALUE1,
                String.class, null));
        assertEquals("Some value", reverseConverter.convertToModel(
                FooEnum.SOME_VALUE, String.class, null));
        assertEquals("Foo bar baz", reverseConverter.convertToModel(
                FooEnum.FOO_BAR_BAZ, String.class, null));
        assertEquals("Bar", reverseConverter.convertToModel(FooEnum.Bar,
                String.class, null));
        assertEquals("Nonstandardcase", reverseConverter.convertToModel(
                FooEnum.nonStandardCase, String.class, null));
        assertEquals("_hugh", reverseConverter.convertToModel(FooEnum._HUGH,
                String.class, null));

    }

}
