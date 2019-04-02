/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToBigIntegerConverter;
import com.vaadin.data.converter.StringToBooleanConverter;
import com.vaadin.data.converter.StringToDateConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Page;
import com.vaadin.server.SerializableFunction;

/**
 * TODO
 */
public class DefaultBindingConverterFactory implements BindingConverterFactory {

    private static final Map<Class<?>, Set<Class<?>>> SUPPORTED_PRESENTATION_TO_MODEL_CONVERTERS_MAP = new HashMap<>();
    static {
        SUPPORTED_PRESENTATION_TO_MODEL_CONVERTERS_MAP.put(String.class,
                new HashSet<>(
                        Arrays.asList(Double.class, Integer.class, Float.class,
                                Integer.class, Long.class, BigDecimal.class,
                                Boolean.class, Date.class, BigInteger.class)));
        SUPPORTED_PRESENTATION_TO_MODEL_CONVERTERS_MAP.put(LocalDateTime.class,
                new HashSet<>(Collections.singletonList(Date.class)));
        SUPPORTED_PRESENTATION_TO_MODEL_CONVERTERS_MAP.put(LocalDate.class,
                new HashSet<>(Collections.singletonList(Date.class)));
    }

    private static final SerializableFunction<Class, ErrorMessageProvider> TYPE_BASED_ERROR_MESSAGE_PROVIDER = clazz -> context -> String
            .format("Invalid value, expected ${0}", clazz.getSimpleName());

    public ErrorMessageProvider getErrorMessageProvider(Class<?> modelType) {
        return TYPE_BASED_ERROR_MESSAGE_PROVIDER.apply(modelType);
    }

    @Override
    public Binder.BindingBuilder buildBindingConverter(
            Binder.BindingBuilder builder, Class<?> presentationType,
            Class<?> modelType) {
        if (isSupported(presentationType, modelType)) {
            Supplier<?> nullRepresentationProvider = getNullRepresentationProvider(
                    presentationType, modelType);
            if (nullRepresentationProvider != null) {
                builder = builder.withNullRepresentation(
                        nullRepresentationProvider.get());
            }
            if (presentationType == String.class) {
                builder = builder
                        .withConverter(createStringConverter(modelType));
            } else if (presentationType == LocalDateTime.class) {
                if (modelType == Date.class) {
                    builder = builder
                            .withConverter(new LocalDateTimeToDateConverter(
                                    getZoneIdForDateConverters()));
                }
            } else if (presentationType == LocalDate.class) {
                if (modelType == Date.class) {
                    builder = builder
                            .withConverter(new LocalDateToDateConverter(
                                    getZoneIdForDateConverters()));
                }
            }
        }
        return builder;
    }

    /**
     * Creates converter between string and the given model type.
     * 
     * @param modelType
     *            the model type
     * @return a converter between string and the model type
     */
    protected Converter<String, ?> createStringConverter(Class<?> modelType) {
        ErrorMessageProvider errorMessageProvider = getErrorMessageProvider(
                modelType);
        if (Double.class.isAssignableFrom(modelType)) {
            return new StringToDoubleConverter(errorMessageProvider);
        } else if (Float.class.isAssignableFrom(modelType)) {
            return new StringToFloatConverter(errorMessageProvider);
        } else if (Integer.class.isAssignableFrom(modelType)) {
            return new StringToIntegerConverter(errorMessageProvider);
        } else if (Long.class.isAssignableFrom(modelType)) {
            return new StringToLongConverter(errorMessageProvider);
        } else if (BigDecimal.class.isAssignableFrom(modelType)) {
            return new StringToBigDecimalConverter(errorMessageProvider);
        } else if (Boolean.class.isAssignableFrom(modelType)) {
            return new StringToBooleanConverter(errorMessageProvider);
        } else if (Date.class.isAssignableFrom(modelType)) {
            return new StringToDateConverter();
        } else if (BigInteger.class.isAssignableFrom(modelType)) {
            return new StringToBigIntegerConverter(errorMessageProvider);
        } else {
            return null;
        }
    }

    /**
     * Gets the provider of
     * {@link com.vaadin.data.Binder.BindingBuilder#withNullRepresentation(Object)}
     * for the given type. This can be used for customizing the conversion from
     * {@code null} value to something else.
     * <p>
     * By default this only supports converting to empty string for fields that
     * have String as presentation type. Override this method to support
     * converting null to something additional, or return {@code null} to
     * prevent null conversion from model -> presentation.
     * 
     * @param presentationType
     *            the presentation type
     * @param modelType
     *            the model type
     * @return the null representation provider, or {@code null} to prevent
     *         conversion of {@code null} model value
     */
    protected Supplier<?> getNullRepresentationProvider(
            Class<?> presentationType, Class<?> modelType) {
        if (presentationType.equals(String.class)) {
            return () -> "";
        }
        return null;
    }

    /**
     * Gets the time zone id for {@link LocalDateTimeToDateConverter} and
     * {@link LocalDateToDateConverter}.
     * <p>
     * Tries to access browser provided zone id with {@link Page#getCurrent()},
     * defaults to system default if not available.
     * 
     * @return the zone id to use, not {@code null}
     */
    protected ZoneId getZoneIdForDateConverters() {
        Page page = Page.getCurrent();
        if (page != null && page.getWebBrowser().getTimeZoneId() != null) {
            return ZoneId.of(page.getWebBrowser().getTimeZoneId());
        }
        return ZoneId.systemDefault();
    }

    @Override
    public boolean isSupported(Class<?> presentationType, Class<?> modelType) {
        Set<Class<?>> supported = SUPPORTED_PRESENTATION_TO_MODEL_CONVERTERS_MAP
                .get(presentationType);
        return supported != null && supported.size() > 0
                && supported.contains(modelType);
    }
}
