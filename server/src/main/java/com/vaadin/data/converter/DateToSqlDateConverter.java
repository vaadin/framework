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

/**
 *
 */
package com.vaadin.data.converter;

import java.util.Date;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Converter for handling conversion between {@link java.util.Date} and
 * {@link java.sql.Date}. This is used when a PopupDateField or InlineDateField
 * is connected to a java.sql.Date property. Note that information (time
 * information) is lost when converting from {@link java.util.Date} to
 * {@link java.sql.Date}.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public class DateToSqlDateConverter implements Converter<Date, java.sql.Date> {

    @Override
    public Result<java.sql.Date> convertToModel(Date value,
            ValueContext context) {
        if (value == null) {
            return Result.ok(null);
        }

        return Result.ok(new java.sql.Date(value.getTime()));
    }

    @Override
    public Date convertToPresentation(java.sql.Date value,
            ValueContext context) {
        if (value == null) {
            return null;
        }

        return new Date(value.getTime());
    }

}
