/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Locale;

/**
 * A converter that converts from {@link String} to {@link Boolean} and back.
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
public class StringToBooleanConverter implements Converter<String, Boolean> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object,
     * java.util.Locale)
     */
    public Boolean convertToModel(String value, Locale locale)
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
            throw new ConversionException("Cannot convert " + value + " to "
                    + getModelType().getName());
        }
    }

    /**
     * Gets the string representation for true. Default is "true".
     * 
     * @return the string representation for true
     */
    protected String getTrueString() {
        return Boolean.TRUE.toString();
    }

    /**
     * Gets the string representation for false. Default is "false".
     * 
     * @return the string representation for false
     */
    protected String getFalseString() {
        return Boolean.FALSE.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang
     * .Object, java.util.Locale)
     */
    public String convertToPresentation(Boolean value, Locale locale)
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
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    public Class<Boolean> getModelType() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getPresentationType()
     */
    public Class<String> getPresentationType() {
        return String.class;
    }

}
