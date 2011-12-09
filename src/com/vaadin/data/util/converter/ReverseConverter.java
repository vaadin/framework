/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Locale;

public class ReverseConverter<SOURCE, TARGET> implements
        Converter<SOURCE, TARGET> {

    private Converter<TARGET, SOURCE> realConverter;

    public ReverseConverter(Converter<TARGET, SOURCE> realConverter) {
        this.realConverter = realConverter;
    }

    public SOURCE convertFromTargetToSource(TARGET value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return realConverter.convertFromSourceToTarget(value, locale);
    }

    public TARGET convertFromSourceToTarget(SOURCE value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return realConverter.convertFromTargetToSource(value, locale);
    }

    public Class<SOURCE> getSourceType() {
        return realConverter.getTargetType();
    }

    public Class<TARGET> getTargetType() {
        return realConverter.getSourceType();
    }

}
