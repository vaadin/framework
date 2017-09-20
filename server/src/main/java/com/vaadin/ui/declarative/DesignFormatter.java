/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui.declarative;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.parser.Parser;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Resource;
import com.vaadin.ui.declarative.converters.DesignDateConverter;
import com.vaadin.ui.declarative.converters.DesignEnumConverter;
import com.vaadin.ui.declarative.converters.DesignLocalDateConverter;
import com.vaadin.ui.declarative.converters.DesignLocalDateTimeConverter;
import com.vaadin.ui.declarative.converters.DesignObjectConverter;
import com.vaadin.ui.declarative.converters.DesignResourceConverter;
import com.vaadin.ui.declarative.converters.DesignShortcutActionConverter;
import com.vaadin.ui.declarative.converters.DesignTimeZoneConverter;
import com.vaadin.ui.declarative.converters.DesignToStringConverter;

/**
 * Class focused on flexible and consistent formatting and parsing of different
 * values throughout reading and writing {@link Design}. An instance of this
 * class is used by {@link DesignAttributeHandler}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignFormatter implements Serializable {

    private final Map<Class<?>, Converter<String, ?>> converterMap = new ConcurrentHashMap<>();
    private final Converter<String, Object> stringObjectConverter = new DesignObjectConverter();

    /**
     * Creates the formatter with default types already mapped.
     */
    public DesignFormatter() {
        mapDefaultTypes();
    }

    /**
     * Maps default types to their converters.
     *
     */
    protected void mapDefaultTypes() {
        // numbers use standard toString/valueOf approach
        for (Class<?> c : new Class<?>[] { Byte.class, Short.class,
                Integer.class, Long.class }) {
            DesignToStringConverter<?> conv = new DesignToStringConverter(c);
            converterMap.put(c, conv);
            try {
                converterMap.put((Class<?>) c.getField("TYPE").get(null), conv);
            } catch (Exception e) {
                ; // this will never happen
            }
        }
        // booleans use a bit different converter than the standard one
        // "false" is boolean false, everything else is boolean true
        Converter<String, Boolean> booleanConverter = new Converter<String, Boolean>() {

            @Override
            public Result<Boolean> convertToModel(String value,
                    ValueContext context) {
                return Result.ok(!value.equalsIgnoreCase("false"));
            }

            @Override
            public String convertToPresentation(Boolean value,
                    ValueContext context) {
                if (value.booleanValue()) {
                    return "";
                } else {
                    return "false";
                }
            }

        };
        converterMap.put(Boolean.class, booleanConverter);
        converterMap.put(boolean.class, booleanConverter);

        // floats and doubles use formatters
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(
                new Locale("en_US"));
        final DecimalFormat fmt = new DecimalFormat("0.###", symbols);
        fmt.setGroupingUsed(false);

        Converter<String, ?> floatConverter = new StringToFloatConverter(
                "Error converting value") {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                return fmt;
            };
        };
        converterMap.put(Float.class, floatConverter);
        converterMap.put(float.class, floatConverter);

        Converter<String, ?> doubleConverter = new StringToDoubleConverter(
                "Error converting value") {
            @Override
            protected NumberFormat getFormat(Locale locale) {
                return fmt;
            };
        };
        converterMap.put(Double.class, doubleConverter);
        converterMap.put(double.class, doubleConverter);

        final DecimalFormat bigDecimalFmt = new DecimalFormat("0.###", symbols);
        bigDecimalFmt.setGroupingUsed(false);
        bigDecimalFmt.setParseBigDecimal(true);
        converterMap.put(BigDecimal.class,
                new StringToBigDecimalConverter("Error converting value") {
                    @Override
                    protected NumberFormat getFormat(Locale locale) {
                        return bigDecimalFmt;
                    };
                });

        // strings do nothing
        converterMap.put(String.class, new Converter<String, String>() {

            @Override
            public Result<String> convertToModel(String value,
                    ValueContext context) {
                return Result.ok(value);
            }

            @Override
            public String convertToPresentation(String value,
                    ValueContext context) {
                return value;
            }

        });

        // char takes the first character from the string
        Converter<String, Character> charConverter = new DesignToStringConverter<Character>(
                Character.class) {

            @Override
            public Result<Character> convertToModel(String value,
                    ValueContext context) {
                return Result.ok(value.charAt(0));
            }

        };
        converterMap.put(Character.class, charConverter);
        converterMap.put(char.class, charConverter);

        converterMap.put(Date.class, new DesignDateConverter());
        converterMap.put(LocalDate.class, new DesignLocalDateConverter());
        converterMap.put(LocalDateTime.class,
                new DesignLocalDateTimeConverter());
        converterMap.put(ShortcutAction.class,
                new DesignShortcutActionConverter());
        converterMap.put(Resource.class, new DesignResourceConverter());
        converterMap.put(TimeZone.class, new DesignTimeZoneConverter());
    }

    /**
     * Adds a converter for a given type.
     *
     * @param type
     *            Type to convert to/from.
     * @param converter
     *            Converter.
     * @since 8.0
     */
    protected <T> void addConverter(Class<?> type,
            Converter<String, ?> converter) {
        converterMap.put(type, converter);
    }

    /**
     * Removes the converter for given type, if it was present.
     *
     * @param type
     *            Type to remove converter for.
     */
    protected void removeConverter(Class<?> type) {
        converterMap.remove(type);
    }

    /**
     * Returns a set of classes that have a converter registered. This is <b>not
     * the same</b> as the list of supported classes - subclasses of classes in
     * this set are also supported.
     *
     * @return An unmodifiable set of classes that have a converter registered.
     */
    protected Set<Class<?>> getRegisteredClasses() {
        return Collections.unmodifiableSet(converterMap.keySet());
    }

    /**
     * Parses a given string as a value of given type.
     *
     * @param value
     *            String value to convert.
     * @param type
     *            Expected result type.
     * @return String converted to the expected result type using a registered
     *         converter for that type.
     */
    public <T> T parse(String value, Class<? extends T> type) {
        Converter<String, T> converter = findConverterFor(type);
        if (converter != null) {
            Result<T> result = converter.convertToModel(value,
                    new ValueContext());
            return result.getOrThrow(msg -> new IllegalArgumentException(msg));
        } else {
            return null;
        }
    }

    /**
     * Finds a formatter for a given object and attempts to format it.
     *
     * @param object
     *            Object to format.
     * @return String representation of the object, as returned by the
     *         registered converter.
     */
    public String format(Object object) {
        return format(object,
                object == null ? Object.class : object.getClass());
    }

    /**
     * Formats an object according to a converter suitable for a given type.
     *
     * @param object
     *            Object to format.
     * @param type
     *            Type of the object.
     * @return String representation of the object, as returned by the
     *         registered converter.
     */
    public <T> String format(T object, Class<? extends T> type) {
        if (object == null) {
            return null;
        } else {
            Converter<String, Object> converter = findConverterFor(
                    object.getClass());
            return converter.convertToPresentation(object, new ValueContext());
        }
    }

    /**
     * Checks whether or not a value of a given type can be converted. If a
     * converter for a superclass is found, this will return true.
     *
     * @param type
     *            Type to check.
     * @return <b>true</b> when either a given type or its supertype has a
     *         converter, <b>false</b> otherwise.
     */
    public boolean canConvert(Class<?> type) {
        return findConverterFor(type) != null;
    }

    /**
     * Finds a converter for a given type. May return a converter for a
     * superclass instead, if one is found and {@code strict} is false.
     *
     * @param sourceType
     *            Type to find a converter for.
     * @param strict
     *            Whether or not search should be strict. When this is
     *            <b>false</b>, a converter for a superclass of given type may
     *            be returned.
     * @return A valid converter for a given type or its supertype, <b>null</b>
     *         if it was not found.
     * @since 8.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> Converter<String, T> findConverterFor(
            Class<? extends T> sourceType, boolean strict) {
        if (sourceType == Object.class) {
            // Use for propertyIds, itemIds and such. Only string type objects
            // are really supported if no special logic is implemented in the
            // component.
            return (Converter<String, T>) stringObjectConverter;
        }

        if (converterMap.containsKey(sourceType)) {
            return ((Converter<String, T>) converterMap.get(sourceType));
        } else if (!strict) {
            for (Class<?> supported : converterMap.keySet()) {
                if (supported.isAssignableFrom(sourceType)) {
                    return ((Converter<String, T>) converterMap.get(supported));
                }
            }
        }

        if (sourceType.isEnum()) {
            return new DesignEnumConverter(sourceType);
        }
        return null;
    }

    /**
     * Finds a converter for a given type. May return a converter for a
     * superclass instead, if one is found.
     *
     * @param sourceType
     *            Type to find a converter for.
     * @return A valid converter for a given type or its subtype, <b>null</b> if
     *         it was not found.
     * @since 8.0
     */
    protected <T> Converter<String, T> findConverterFor(
            Class<? extends T> sourceType) {
        return findConverterFor(sourceType, false);
    }

    /**
     * <p>
     * Encodes <em>some</em> special characters in a given input String to make
     * it ready to be written as contents of a text node. WARNING: this will
     * e.g. encode "&lt;someTag&gt;" to "&amp;lt;someTag&amp;gt;" as this method
     * doesn't do any parsing and assumes that there are no intended HTML
     * elements in the input. Only some entities are actually encoded:
     * &amp;,&lt;, &gt; It's assumed that other entities are taken care of by
     * Jsoup.
     * </p>
     * <p>
     * Typically, this method will be used by components to encode data (like
     * option items in {@code AbstractSelect}) when dumping to HTML format
     * </p>
     *
     * @since 7.5.7
     * @param input
     *            String to be encoded
     * @return String with &amp;,&lt; and &gt; replaced with their HTML entities
     */
    public static String encodeForTextNode(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;").replace(">", "&gt;").replace("<",
                "&lt;");
    }

    /**
     * <p>
     * Decodes HTML entities in a text from text node and replaces them with
     * actual characters.
     * </p>
     *
     * <p>
     * Typically this method will be used by components to read back data (like
     * option items in {@code AbstractSelect}) from HTML. Note that this method
     * unencodes more characters than {@link #encodeForTextNode(String)} encodes
     * </p>
     *
     * @since 7.6
     * @param input
     * @return
     */
    public static String decodeFromTextNode(String input) {
        return Parser.unescapeEntities(input, false);
    }

}
