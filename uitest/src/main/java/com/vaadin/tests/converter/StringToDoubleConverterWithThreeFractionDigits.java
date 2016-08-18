package com.vaadin.tests.converter;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.legacy.data.util.converter.LegacyStringToDoubleConverter;

public class StringToDoubleConverterWithThreeFractionDigits
        extends LegacyStringToDoubleConverter {

    @Override
    protected NumberFormat getFormat(Locale locale) {
        NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(3);
        format.setMinimumFractionDigits(3);
        return format;
    }
}
