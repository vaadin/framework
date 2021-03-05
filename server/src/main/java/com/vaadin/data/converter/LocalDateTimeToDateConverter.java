/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.data.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.InlineDateTimeField;

/**
 * A converter that converts between <code>LocalDateTime</code> and
 * <code>Date</code>. This is used when a {@link DateTimeField} or
 * {@link InlineDateTimeField} is bound to a {@link Date} property.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class LocalDateTimeToDateConverter
        implements Converter<LocalDateTime, Date> {

    private ZoneId zoneId;

    /**
     * Creates a new converter using the given time zone.
     *
     * @param zoneId
     *            the time zone to use, not <code>null</code>
     */
    public LocalDateTimeToDateConverter(ZoneId zoneId) {
        this.zoneId = Objects.requireNonNull(zoneId,
                "Zone identifier cannot be null");
    }

    @Override
    public Result<Date> convertToModel(LocalDateTime localDate,
            ValueContext context) {
        if (localDate == null) {
            return Result.ok(null);
        }

        return Result.ok(Date.from(localDate.atZone(zoneId).toInstant()));
    }

    @Override
    public LocalDateTime convertToPresentation(Date date,
            ValueContext context) {
        if (date == null) {
            return null;
        }

        return Instant.ofEpochMilli(date.getTime()).atZone(zoneId)
                .toLocalDateTime();
    }

}
