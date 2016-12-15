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

import java.lang.reflect.InvocationTargetException;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.declarative.DesignAttributeHandler;

/**
 * Utility class for {@link DesignAttributeHandler} that deals with converting
 * various types to string.
 *
 * @since 7.4
 * @author Vaadin Ltd
 * @param <TYPE>
 *            Type of the data being converted.
 */
public class DesignToStringConverter<TYPE> implements Converter<String, TYPE> {

    private final Class<? extends TYPE> type;

    private final String staticMethodName;

    /**
     * A string that corresponds to how a null value is stored.
     */
    public static final String NULL_VALUE_REPRESENTATION = "";

    /**
     * Constructs the converter for a given type. Implicitly requires that a
     * static method {@code valueOf(String)} is present in the type to do the
     * conversion.
     *
     * @param type
     *            Type of values to convert.
     */
    public DesignToStringConverter(Class<? extends TYPE> type) {
        this(type, "valueOf");
    }

    /**
     * Constructs the converter for a given type, giving the name of the public
     * static method that does the conversion from String.
     *
     * @param type
     *            Type to convert.
     * @param staticMethodName
     *            Method to call when converting from String to this type. This
     *            must be public and static method that returns an object of
     *            passed type.
     */
    public DesignToStringConverter(Class<? extends TYPE> type,
            String staticMethodName) {
        this.type = type;
        this.staticMethodName = staticMethodName;
    }

    @Override
    public Result<TYPE> convertToModel(String value, ValueContext context) {
        try {
            return Result.ok(type
                    .cast(type.getMethod(this.staticMethodName, String.class)
                            .invoke(null, value)));
        } catch (InvocationTargetException e) {
            return Result.error(e.getCause().getMessage());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public String convertToPresentation(TYPE value, ValueContext context) {
        if (value == null) {
            return NULL_VALUE_REPRESENTATION;
        } else {
            return value.toString();
        }
    }

}
