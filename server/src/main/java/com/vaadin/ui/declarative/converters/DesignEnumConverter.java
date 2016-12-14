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

import java.util.Locale;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * An converter for Enum to/from String for {@link DesignAttributeHandler} to
 * use internally.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
@SuppressWarnings("rawtypes")
public class DesignEnumConverter<T extends Enum>
        implements Converter<String, T> {

    private final Class<T> type;

    /**
     * Creates a converter for the given enum type.
     *
     * @param type
     *            the enum type to convert to/from
     */
    public DesignEnumConverter(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<T> convertToModel(String value, ValueContext context) {
        if (value == null || value.trim().isEmpty()) {
            return Result.ok(null);
        }
        try {
            T result = (T) Enum.valueOf(type,
                    value.toUpperCase(Locale.ENGLISH));
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public String convertToPresentation(T value, ValueContext context) {
        if (value == null) {
            return null;
        }

        return value.name().toLowerCase(Locale.ENGLISH);
    }

}
