/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Locale;

/**
 * A converter that wraps another {@link Converter} and reverses source and
 * target types.
 * 
 * @param <SOURCE>
 *            The source type
 * @param <TARGET>
 *            The target type
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class ReverseConverter<SOURCE, TARGET> implements
        Converter<SOURCE, TARGET> {

    private Converter<TARGET, SOURCE> realConverter;

    /**
     * Creates a converter from source to target based on a converter that
     * converts from target to source.
     * 
     * @param converter
     *            The converter to use in a reverse fashion
     */
    public ReverseConverter(Converter<TARGET, SOURCE> converter) {
        this.realConverter = converter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromTargetToSource(java
     * .lang.Object, java.util.Locale)
     */
    public SOURCE convertFromTargetToSource(TARGET value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return realConverter.convertFromSourceToTarget(value, locale);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromSourceToTarget(java
     * .lang.Object, java.util.Locale)
     */
    public TARGET convertFromSourceToTarget(SOURCE value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return realConverter.convertFromTargetToSource(value, locale);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getSourceType()
     */
    public Class<SOURCE> getSourceType() {
        return realConverter.getTargetType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getTargetType()
     */
    public Class<TARGET> getTargetType() {
        return realConverter.getSourceType();
    }

}
