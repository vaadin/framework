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

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * An converter for Object to/from String for {@link DesignAttributeHandler} to
 * use internally.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignObjectConverter implements Converter<String, Object> {

    @Override
    public Object convertToModel(String value,
            Class<? extends Object> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value;
    }

    @Override
    public String convertToPresentation(Object value,
            Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    @Override
    public Class<Object> getModelType() {
        return Object.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
