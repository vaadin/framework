package com.vaadin.tests.minitutorials.v7a1;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToDoubleConverter;

public class MyStringToDoubleConverter extends StringToDoubleConverter {

    @Override
    protected NumberFormat getFormat(Locale locale) {
        NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(3);
        format.setMinimumFractionDigits(3);
        return format;
    }
}
