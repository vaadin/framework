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
package com.vaadin.ui.declarative.converters;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Converter based on Java Formats rather than static methods.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @param <TYPE>
 *            Type of the object to format.
 */
public class DesignFormatConverter<TYPE> implements Converter<String, TYPE> {

    private final Format format;
    private final Class<? extends TYPE> type;

    /**
     * Constructs an instance of the converter.
     */
    public DesignFormatConverter(Class<? extends TYPE> type, Format format) {
        this.type = type;
        this.format = format;
    }

    @Override
    public TYPE convertToModel(String value, Class<? extends TYPE> targetType,
            Locale locale) throws Converter.ConversionException {
        try {
            return targetType.cast(this.format.parseObject(value));
        } catch (ParseException e) {
            throw new Converter.ConversionException(e);
        }
    }

    @Override
    public String convertToPresentation(TYPE value,
            Class<? extends String> targetType, Locale locale)
            throws Converter.ConversionException {
        return this.format.format(value);
    }

    @Override
    public Class<TYPE> getModelType() {
        return (Class<TYPE>) this.type;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
