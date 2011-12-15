/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Locale;

/**
 * A converter that converts from {@link Boolean} to {@link String} and back.
 * The String representation is given by Boolean.toString().
 * <p>
 * Leading and trailing white spaces are ignored when converting from a String.
 * </p>
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
        if (value == null) {
            return null;
        }

        // Remove leading and trailing white space
        value = value.trim();

        if (getTrueString().equals(value)) {
            return true;
        } else if (getFalseString().equals(value)) {
            return false;
        } else {
            throw new ConversionException("Cannot convert " + value
                    + " to Boolean");
        }
    }

    protected String getTrueString() {
        return Boolean.TRUE.toString();
    }

    protected String getFalseString() {
        return Boolean.FALSE.toString();
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
            return null;
        }
        if (value) {
            return getTrueString();
        } else {
            return getFalseString();
        }
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
