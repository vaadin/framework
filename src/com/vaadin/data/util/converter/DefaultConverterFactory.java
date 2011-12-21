/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Date;
import java.util.logging.Logger;

import com.vaadin.Application;

/**
 * Default implementation of {@link ConverterFactory}. Provides converters for
 * standard types like {@link String}, {@link Double} and {@link Date}. </p>
 * <p>
 * Custom converters can be provided by extending this class and using
 * {@link Application#setConverterFactory(ConverterFactory)}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class DefaultConverterFactory implements ConverterFactory {

    private final static Logger log = Logger
            .getLogger(DefaultConverterFactory.class.getName());

    public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        Converter<PRESENTATION, MODEL> converter = findConverter(
                presentationType, modelType);
        if (converter != null) {
            log.finest(getClass().getName() + " created a "
                    + converter.getClass());
            return converter;
        }

        // Try to find a reverse converter
        Converter<MODEL, PRESENTATION> reverseConverter = findConverter(
                modelType, presentationType);
        if (reverseConverter != null) {
            log.finest(getClass().getName() + " created a reverse "
                    + reverseConverter.getClass());
            return new ReverseConverter<PRESENTATION, MODEL>(reverseConverter);
        }

        log.finest(getClass().getName() + " could not find a converter for "
                + presentationType.getName() + " to " + modelType.getName()
                + " conversion");
        return null;

    }

    protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        if (presentationType == String.class) {
            // TextField converters and more
            Converter<PRESENTATION, MODEL> converter = (Converter<PRESENTATION, MODEL>) createStringConverter(modelType);
            if (converter != null) {
                return converter;
            }
        } else if (presentationType == Date.class) {
            // DateField converters and more
            Converter<PRESENTATION, MODEL> converter = (Converter<PRESENTATION, MODEL>) createDateConverter(modelType);
            if (converter != null) {
                return converter;
            }
        }

        return null;

    }

    protected Converter<Date, ?> createDateConverter(Class<?> sourceType) {
        if (Long.class.isAssignableFrom(sourceType)) {
            return new DateToLongConverter();
        } else {
            return null;
        }
    }

    protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
        if (Double.class.isAssignableFrom(sourceType)) {
            return new StringToDoubleConverter();
        } else if (Integer.class.isAssignableFrom(sourceType)) {
            return new StringToIntegerConverter();
        } else if (Boolean.class.isAssignableFrom(sourceType)) {
            return new StringToBooleanConverter();
        } else if (Number.class.isAssignableFrom(sourceType)) {
            return new StringToNumberConverter();
        } else if (Date.class.isAssignableFrom(sourceType)) {
            return new StringToDateConverter();
        } else {
            return null;
        }
    }

}
