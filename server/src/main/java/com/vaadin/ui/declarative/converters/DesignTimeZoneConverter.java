/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.TimeZone;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
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
    public Result<TimeZone> convertToModel(String value, ValueContext context) {
        if (value == null || value.isEmpty()) {
            return Result.ok(null);
        }

        return Result.ok(TimeZone.getTimeZone(value));
    }

    @Override
    public String convertToPresentation(TimeZone value, ValueContext context) {
        if (value == null) {
            return "";
        } else {
            return value.getID();
        }
    }

}
