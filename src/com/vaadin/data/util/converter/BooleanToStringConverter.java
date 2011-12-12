/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Locale;

/**
 * A converter that converts from {@link Boolean} to {@link String} and back.
 * The String representation is given by Boolean.toString().
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class BooleanToStringConverter implements Converter<Boolean, String> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromTargetToSource(java
     * .lang.Object, java.util.Locale)
     */
    public Boolean convertFromTargetToSource(String value, Locale locale)
            throws ConversionException {
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            throw new ConversionException("Cannot convert " + value
                    + " to Boolean");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromSourceToTarget(java
     * .lang.Object, java.util.Locale)
     */
    public String convertFromSourceToTarget(Boolean value, Locale locale)
            throws ConversionException {
        if (value == null) {
            return "";
        }

        return value.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getSourceType()
     */
    public Class<Boolean> getSourceType() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getTargetType()
     */
    public Class<String> getTargetType() {
        return String.class;
    }

}
