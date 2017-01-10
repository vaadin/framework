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
package com.vaadin.ui;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import com.vaadin.data.validator.DateTimeRangeValidator;
import com.vaadin.data.validator.RangeValidator;
import com.vaadin.shared.ui.datefield.AbstractTextualDateFieldState;
import com.vaadin.shared.ui.datefield.DateTimeResolution;

/**
 * Abstract DateField class for {@link LocalDateTime} type.
 * 
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
public abstract class AbstractLocalDateTimeField
        extends AbstractDateField<LocalDateTime, DateTimeResolution> {

    /**
     * Constructs an empty <code>AbstractLocalDateTimeField</code> with no
     * caption.
     */
    public AbstractLocalDateTimeField() {
        super(DateTimeResolution.MINUTE);
    }

    /**
     * Constructs an empty <code>AbstractLocalDateTimeField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     */
    public AbstractLocalDateTimeField(String caption) {
        super(caption, DateTimeResolution.MINUTE);
    }

    /**
     * Constructs a new <code>AbstractLocalDateTimeField</code> with the given
     * caption and initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the LocalDateTime value.
     */
    public AbstractLocalDateTimeField(String caption, LocalDateTime value) {
        super(caption, value, DateTimeResolution.MINUTE);
    }

    @Override
    protected AbstractTextualDateFieldState getState() {
        return (AbstractTextualDateFieldState) super.getState();
    }

    @Override
    protected AbstractTextualDateFieldState getState(boolean markAsDirty) {
        return (AbstractTextualDateFieldState) super.getState(markAsDirty);
    }

    @Override
    protected int getDatePart(LocalDateTime date,
            DateTimeResolution resolution) {
        LocalDateTime value = date;
        if (value == null) {
            value = LocalDateTime.of(1, 1, 1, 0, 0);
        }
        switch (resolution) {
        case DAY:
            return value.getDayOfMonth();
        case MONTH:
            return value.getMonthValue();
        case YEAR:
            return value.getYear();
        case HOUR:
            return value.getHour();
        case MINUTE:
            return value.getMinute();
        case SECOND:
            return value.getSecond();
        default:
            assert false : "Unexpected resolution argument " + resolution;
            return -1;
        }
    }

    @Override
    protected RangeValidator<LocalDateTime> getRangeValidator() {
        return new DateTimeRangeValidator(getDateOutOfRangeMessage(),
                getDate(getRangeStart(), getResolution()),
                getDate(getRangeEnd(), getResolution()));
    }

    @Override
    protected LocalDateTime buildDate(
            Map<DateTimeResolution, Integer> resolutionValues) {
        return LocalDateTime.of(resolutionValues.get(DateTimeResolution.YEAR),
                resolutionValues.getOrDefault(DateTimeResolution.MONTH, 1),
                resolutionValues.getOrDefault(DateTimeResolution.DAY, 1),
                resolutionValues.getOrDefault(DateTimeResolution.HOUR, 0),
                resolutionValues.getOrDefault(DateTimeResolution.MINUTE, 0),
                resolutionValues.getOrDefault(DateTimeResolution.SECOND, 0));
    }

    @Override
    protected LocalDateTime convertFromDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    @Override
    protected Date convertToDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.toInstant(ZoneOffset.UTC));
    }

    private LocalDateTime getDate(LocalDateTime date,
            DateTimeResolution forResolution) {
        if (date == null) {
            return null;
        }
        switch (forResolution) {
        case YEAR:
            return date.withDayOfYear(1).toLocalDate().atStartOfDay();
        case MONTH:
            return date.withDayOfMonth(1).toLocalDate().atStartOfDay();
        case DAY:
            return date.toLocalDate().atStartOfDay();
        case HOUR:
            return date.truncatedTo(ChronoUnit.HOURS);
        case MINUTE:
            return date.truncatedTo(ChronoUnit.MINUTES);
        case SECOND:
            return date.truncatedTo(ChronoUnit.SECONDS);
        default:
            assert false : "Unexpected resolution argument " + forResolution;
            return null;
        }
    }

}
