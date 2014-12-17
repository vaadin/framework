/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util.converter;

import java.util.EnumSet;
import java.util.Locale;

/**
 * A converter that converts from {@link String} to an {@link Enum} and back.
 * <p>
 * Designed to provide nice human readable strings for {@link Enum} classes
 * where the constants are named SOME_UPPERCASE_WORDS. Will not necessarily work
 * correctly for other cases.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.4
 */
public class StringToEnumConverter implements Converter<String, Enum> {

    @Override
    public Enum convertToModel(String value, Class<? extends Enum> targetType,
            Locale locale) throws ConversionException {
        if (value == null || value.trim().equals("")) {
            return null;
        }

        return stringToEnum(value, targetType, locale);
    }

    /**
     * Converts the given string to the given enum type using the given locale
     * <p>
     * Compatible with {@link #enumToString(Enum, Locale)}
     * 
     * @param value
     *            The string value to convert
     * @param enumType
     *            The type of enum to create
     * @param locale
     *            The locale to use for conversion. If null, the JVM default
     *            locale will be used
     * @return The enum which matches the given string
     * @throws ConversionException
     *             if the conversion fails
     */
    public static <T extends Enum<T>> T stringToEnum(String value,
            Class<T> enumType, Locale locale) throws ConversionException {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        // Foo -> FOO
        // Foo bar -> FOO_BAR
        String result = value.replace(" ", "_").toUpperCase(locale);
        try {
            return Enum.valueOf(enumType, result);
        } catch (Exception ee) {
            // There was no match. Try to compare the available values to see if
            // the constant is using something else than all upper case
            try {
                EnumSet<T> set = EnumSet.allOf(enumType);
                for (T e : set) {
                    if (e.name().toUpperCase(locale).equals(result)) {
                        return e;
                    }
                }
            } catch (Exception e) {
            }

            // Fallback did not work either, re-throw original exception so
            // user knows what went wrong
            throw new ConversionException(ee);
        }
    }

    /**
     * Converts the given enum to a human readable string using the given locale
     * <p>
     * Compatible with {@link #stringToEnum(String, Class, Locale)}
     * 
     * @param value
     *            The enum value to convert
     * @param locale
     *            The locale to use for conversion. If null, the JVM default
     *            locale will be used
     * @return A human readable string based on the enum
     * @throws ConversionException
     *             if the conversion fails
     */
    public static String enumToString(Enum<?> value, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        String enumString = value.toString();
        // FOO -> Foo
        // FOO_BAR -> Foo bar
        // _FOO -> _foo
        String result = enumString.substring(0, 1).toUpperCase(locale);
        result += enumString.substring(1).toLowerCase(locale).replace('_', ' ');
        return result;
    }

    @Override
    public String convertToPresentation(Enum value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }

        return enumToString(value, locale);
    }

    @Override
    public Class<Enum> getModelType() {
        return Enum.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
