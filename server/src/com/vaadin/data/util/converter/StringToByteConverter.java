/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * A converter that converts from {@link String} to {@link Byte} and back. Uses
 * the given locale and a {@link NumberFormat} instance for formatting and
 * parsing.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.4
 */
public class StringToByteConverter extends
        AbstractStringToNumberConverter<Byte> {

    /**
     * Returns the format used by
     * {@link #convertToPresentation(Byte, Class, Locale)} and
     * {@link #convertToModel(String, Class, Locale)}
     * 
     * @param locale
     *            The locale to use
     * @return A NumberFormat instance
     */
    @Override
    protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return NumberFormat.getIntegerInstance(locale);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object,
     * java.lang.Class, java.util.Locale)
     */
    @Override
    public Byte convertToModel(String value, Class<? extends Byte> targetType,
            Locale locale) throws ConversionException {
        Number n = convertToNumber(value, targetType, locale);

        if (n == null) {
            return null;
        }

        byte byteValue = n.byteValue();
        if (byteValue == n.longValue()) {
            return byteValue;
        }

        throw new ConversionException("Could not convert '" + value + "' to "
                + Byte.class.getName() + ": value out of range");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<Byte> getModelType() {
        return Byte.class;
    }

}
