package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.ReverseConverter;
import com.vaadin.v7.data.util.converter.StringToEnumConverter;

public class StringToEnumConverterTest {

    public static enum FooEnum {
        VALUE1, SOME_VALUE, FOO_BAR_BAZ, Bar, nonStandardCase, _HUGH;
    }

    public static enum EnumWithCustomToString {
        ONE, TWO, THREE;

        @Override
        public String toString() {
            return "case " + (ordinal() + 1);
        }
    }

    public static enum EnumWithAmbigousToString {
        FOO, FOOBAR, FOO_BAR;

        @Override
        public String toString() {
            return name().replaceAll("_", "");
        }
    }

    StringToEnumConverter converter = new StringToEnumConverter();
    Converter<Enum, String> reverseConverter = new ReverseConverter<Enum, String>(
            converter);

    private String convertToString(Enum value) {
        return converter.convertToPresentation(value, String.class, null);
    }

    public Enum convertToEnum(String string, Class<? extends Enum> type) {
        return converter.convertToModel(string, type, null);
    }

    @Test
    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", Enum.class, null));
    }

    @Test
    public void testInvalidEnumClassConversion() {
        try {
            converter.convertToModel("Foo", Enum.class, null);
            fail("No exception thrown");
        } catch (ConversionException e) {
            // OK
        }
    }

    @Test
    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Enum.class, null));
    }

    @Test
    public void testReverseNullConversion() {
        assertEquals(null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    @Test
    public void testValueConversion() {
        assertEquals(FooEnum.VALUE1,
                converter.convertToModel("Value1", FooEnum.class, null));
        assertEquals(FooEnum.SOME_VALUE,
                converter.convertToModel("Some value", FooEnum.class, null));
        assertEquals(FooEnum.FOO_BAR_BAZ,
                converter.convertToModel("Foo bar baz", FooEnum.class, null));
        assertEquals(FooEnum.Bar,
                converter.convertToModel("Bar", FooEnum.class, null));
        assertEquals(FooEnum.nonStandardCase, converter
                .convertToModel("Nonstandardcase", FooEnum.class, null));
        assertEquals(FooEnum._HUGH,
                converter.convertToModel("_hugh", FooEnum.class, null));
    }

    @Test
    public void testReverseValueConversion() {
        assertEquals("Value1", reverseConverter.convertToModel(FooEnum.VALUE1,
                String.class, null));
        assertEquals("Some value", reverseConverter
                .convertToModel(FooEnum.SOME_VALUE, String.class, null));
        assertEquals("Foo bar baz", reverseConverter
                .convertToModel(FooEnum.FOO_BAR_BAZ, String.class, null));
        assertEquals("Bar", reverseConverter.convertToModel(FooEnum.Bar,
                String.class, null));
        assertEquals("Nonstandardcase", reverseConverter
                .convertToModel(FooEnum.nonStandardCase, String.class, null));
        assertEquals("_hugh", reverseConverter.convertToModel(FooEnum._HUGH,
                String.class, null));

    }

    @Test
    public void preserveFormattingWithCustomToString() {
        for (EnumWithCustomToString e : EnumWithCustomToString.values()) {
            assertEquals(e.toString(), convertToString(e));
        }
    }

    @Test
    public void findEnumWithCustomToString() {
        for (EnumWithCustomToString e : EnumWithCustomToString.values()) {
            assertSame(e,
                    convertToEnum(e.toString(), EnumWithCustomToString.class));
            assertSame(e,
                    convertToEnum(e.name(), EnumWithCustomToString.class));
        }
    }

    @Test
    public void unambigousValueInEnumWithAmbigous_succeed() {
        assertSame(EnumWithAmbigousToString.FOO,
                convertToEnum("foo", EnumWithAmbigousToString.class));
    }

    @Test(expected = ConversionException.class)
    public void ambigousValue_throws() {
        convertToEnum("foobar", EnumWithAmbigousToString.class);
    }
}
