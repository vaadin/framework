/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.util.Date;
import java.util.Locale;

public class LongToDateConverter implements Converter<Long, Date> {

    public Long convertFromTargetToSource(Date value, Locale locale) {
        if (value == null) {
            return null;
        }

        return value.getTime();
    }

    public Date convertFromSourceToTarget(Long value, Locale locale) {
        if (value == null) {
            return null;
        }

        return new Date(value);
    }

    public Class<Long> getSourceType() {
        return Long.class;
    }

    public Class<Date> getTargetType() {
        return Date.class;
    }

}
