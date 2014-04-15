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
 * A converter that converts from {@link Number} to {@link String} and back.
 * Uses the given locale and {@link NumberFormat} for formatting and parsing.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public class StringToNumberConverter extends
        AbstractStringToNumberConverter<Number> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object,
     * java.lang.Class, java.util.Locale)
     */
    @Override
    public Number convertToModel(String value,
            Class<? extends Number> targetType, Locale locale)
            throws ConversionException {
        if (targetType != getModelType()) {
            throw new ConversionException("Converter only supports "
                    + getModelType().getName() + " (targetType was "
                    + targetType.getName() + ")");
        }

        return convertToNumber(value, targetType, locale);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<Number> getModelType() {
        return Number.class;
    }

}
