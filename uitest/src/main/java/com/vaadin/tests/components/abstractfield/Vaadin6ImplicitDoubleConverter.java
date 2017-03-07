/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.abstractfield;

import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

public class Vaadin6ImplicitDoubleConverter
        implements Converter<String, Double> {

    @Override
    public Double convertToModel(String value,
            Class<? extends Double> targetType, Locale locale)
            throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
        if (null == value) {
            return null;
        }
        return new Double(value.toString());
    }

    @Override
    public String convertToPresentation(Double value,
            Class<? extends String> targetType, Locale locale)
            throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }
        return value.toString();

    }

    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
