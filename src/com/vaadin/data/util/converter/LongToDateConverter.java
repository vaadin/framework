/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Date;
import java.util.Locale;

/**
 * A converter that converts from {@link Long} to {@link Date} and back.
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class LongToDateConverter implements Converter<Long, Date> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromTargetToSource(java
     * .lang.Object, java.util.Locale)
     */
    public Long convertToModel(Date value, Locale locale) {
        if (value == null) {
            return null;
        }

        return value.getTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromSourceToTarget(java
     * .lang.Object, java.util.Locale)
     */
    public Date convertToPresentation(Long value, Locale locale) {
        if (value == null) {
            return null;
        }

        return new Date(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getSourceType()
     */
    public Class<Long> getModelType() {
        return Long.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getTargetType()
     */
    public Class<Date> getPresentationType() {
        return Date.class;
    }

}
