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
package com.vaadin.ui.declarative.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * A {@link LocalDate} converter to be used by {@link DesignAttributeHandler}.
 * Provides ISO-compliant way of storing date and time.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class DesignLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public Result<LocalDate> convertToModel(String value,
            ValueContext context) {
        for (String pattern : new String[] { "yyyy-MM-dd", "yyyy-MM",
                "yyyy" }) {
            try {
                Locale effectiveLocale = context.getLocale()
                        .orElse(Locale.ENGLISH);
                LocalDate date = DateTimeFormatter
                        .ofPattern(pattern, effectiveLocale)
                        .parse(value, LocalDate::from);
                return Result.ok(date);
            } catch (DateTimeParseException e) {
                // not parseable, ignore and try another format
            }
        }
        return Result.error("Could not parse date value: " + value);
    }

    @Override
    public String convertToPresentation(LocalDate value, ValueContext context) {
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd",
                        context.getLocale().orElse(Locale.ENGLISH))
                .format(value);
    }

}
