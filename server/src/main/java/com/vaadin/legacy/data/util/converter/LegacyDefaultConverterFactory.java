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

package com.vaadin.legacy.data.util.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Logger;

import com.vaadin.server.VaadinSession;

/**
 * Default implementation of {@link LegacyConverterFactory}. Provides converters
 * for standard types like {@link String}, {@link Double} and {@link Date}.
 * </p>
 * <p>
 * Custom converters can be provided by extending this class and using
 * {@link VaadinSession#setConverterFactory(LegacyConverterFactory)}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public class LegacyDefaultConverterFactory implements LegacyConverterFactory {

    private final static Logger log = Logger
            .getLogger(LegacyDefaultConverterFactory.class.getName());

    @Override
    public <PRESENTATION, MODEL> LegacyConverter<PRESENTATION, MODEL> createConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        LegacyConverter<PRESENTATION, MODEL> converter = findConverter(
                presentationType, modelType);
        if (converter != null) {
            log.finest(getClass().getName() + " created a "
                    + converter.getClass());
            return converter;
        }

        // Try to find a reverse converter
        LegacyConverter<MODEL, PRESENTATION> reverseConverter = findConverter(
                modelType, presentationType);
        if (reverseConverter != null) {
            log.finest(getClass().getName() + " created a reverse "
                    + reverseConverter.getClass());
            return new LegacyReverseConverter<PRESENTATION, MODEL>(
                    reverseConverter);
        }

        log.finest(getClass().getName() + " could not find a converter for "
                + presentationType.getName() + " to " + modelType.getName()
                + " conversion");
        return null;

    }

    protected <PRESENTATION, MODEL> LegacyConverter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        if (presentationType == String.class) {
            // TextField converters and more
            LegacyConverter<PRESENTATION, MODEL> converter = (LegacyConverter<PRESENTATION, MODEL>) createStringConverter(
                    modelType);
            if (converter != null) {
                return converter;
            }
        } else if (presentationType == Date.class) {
            // DateField converters and more
            LegacyConverter<PRESENTATION, MODEL> converter = (LegacyConverter<PRESENTATION, MODEL>) createDateConverter(
                    modelType);
            if (converter != null) {
                return converter;
            }
        }

        return null;

    }

    protected LegacyConverter<Date, ?> createDateConverter(
            Class<?> sourceType) {
        if (Long.class.isAssignableFrom(sourceType)) {
            return new LegacyDateToLongConverter();
        } else if (java.sql.Date.class.isAssignableFrom(sourceType)) {
            return new LegacyDateToSqlDateConverter();
        } else {
            return null;
        }
    }

    protected LegacyConverter<String, ?> createStringConverter(
            Class<?> sourceType) {
        if (Double.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToDoubleConverter();
        } else if (Float.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToFloatConverter();
        } else if (Integer.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToIntegerConverter();
        } else if (Long.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToLongConverter();
        } else if (BigDecimal.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToBigDecimalConverter();
        } else if (Boolean.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToBooleanConverter();
        } else if (Date.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToDateConverter();
        } else if (Enum.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToEnumConverter();
        } else if (BigInteger.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToBigIntegerConverter();
        } else if (Short.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToShortConverter();
        } else if (Byte.class.isAssignableFrom(sourceType)) {
            return new LegacyStringToByteConverter();
        } else {
            return null;
        }
    }

}
