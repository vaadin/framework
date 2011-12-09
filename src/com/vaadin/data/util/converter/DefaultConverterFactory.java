/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Date;
import java.util.logging.Logger;

public class DefaultConverterFactory implements ConverterFactory {

    private final static Logger log = Logger
            .getLogger(DefaultConverterFactory.class.getName());

    public <SOURCE, TARGET> Converter<SOURCE, TARGET> createConverter(
            Class<SOURCE> sourceType, Class<TARGET> targetType) {
        Converter<SOURCE, TARGET> converter = findConverter(sourceType,
                targetType);
        if (converter != null) {
            log.finest(getClass().getName() + " created a "
                    + converter.getClass());
            return converter;
        }

        // Try to find a reverse converter
        Converter<TARGET, SOURCE> reverseConverter = findConverter(targetType,
                sourceType);
        if (reverseConverter != null) {
            log.finest(getClass().getName() + " created a reverse "
                    + reverseConverter.getClass());
            return new ReverseConverter<SOURCE, TARGET>(reverseConverter);
        }

        log.finest(getClass().getName() + " could not find a converter for "
                + sourceType.getName() + " to " + targetType.getName()
                + " conversion");
        return null;

    }

    protected <SOURCE, TARGET> Converter<SOURCE, TARGET> findConverter(
            Class<SOURCE> sourceType, Class<TARGET> targetType) {
        if (targetType == String.class) {
            // TextField converters and more
            Converter<SOURCE, TARGET> converter = (Converter<SOURCE, TARGET>) createStringConverter(sourceType);
            if (converter != null) {
                return converter;
            }
        } else if (targetType == Date.class) {
            // DateField converters and more
            Converter<SOURCE, TARGET> converter = (Converter<SOURCE, TARGET>) createDateConverter(sourceType);
            if (converter != null) {
                return converter;
            }
        }

        return null;

    }

    protected Converter<?, Date> createDateConverter(Class<?> sourceType) {
        if (Long.class.isAssignableFrom(sourceType)) {
            return new LongToDateConverter();
        } else {
            return null;
        }
    }

    protected Converter<?, String> createStringConverter(Class<?> sourceType) {
        if (Double.class.isAssignableFrom(sourceType)) {
            return new DoubleToStringConverter();
        } else if (Integer.class.isAssignableFrom(sourceType)) {
            return new IntegerToStringConverter();
        } else if (Boolean.class.isAssignableFrom(sourceType)) {
            return new BooleanToStringConverter();
        } else if (Number.class.isAssignableFrom(sourceType)) {
            return new NumberToStringConverter();
        } else if (Date.class.isAssignableFrom(sourceType)) {
            return new DateToStringConverter();
        } else {
            return null;
        }
    }

}
