/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui.declarative.converters;

import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * Utility class for {@link DesignAttributeHandler} that deals with converting
 * various TimeZones to string.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignTimeZoneConverter implements Converter<String, TimeZone> {

    @Override
    public TimeZone convertToModel(String value,
            Class<? extends TimeZone> targetTimeZone, Locale locale)
            throws Converter.ConversionException {
        if (value == null || value.isEmpty()) {
            return null;
        }

        return TimeZone.getTimeZone(value);
    }

    @Override
    public String convertToPresentation(TimeZone value,
            Class<? extends String> targetTimeZone, Locale locale)
            throws Converter.ConversionException {
        if (value == null) {
            return "";
        } else {
            return value.getID();
        }
    }

    @Override
    public Class<TimeZone> getModelType() {
        return TimeZone.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
