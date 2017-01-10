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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.data.validator.RangeValidator;
import com.vaadin.shared.ui.datefield.AbstractTextualDateFieldState;
import com.vaadin.shared.ui.datefield.DateResolution;

/**
 * Abstract DateField class for {@link LocalDate} type.
 * 
 * @author Vaadin Ltd
 * 
 * @since 8.0
 *
 */
public abstract class AbstractLocalDateField
        extends AbstractDateField<LocalDate, DateResolution> {

    /**
     * Constructs an empty <code>AbstractLocalDateField</code> with no caption.
     */
    public AbstractLocalDateField() {
        super(DateResolution.DAY);
    }

    /**
     * Constructs an empty <code>AbstractLocalDateField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     */
    public AbstractLocalDateField(String caption) {
        super(caption, DateResolution.DAY);
    }

    /**
     * Constructs a new <code>AbstractLocalDateField</code> with the given
     * caption and initial text contents.
     *
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the LocalDate value.
     */
    public AbstractLocalDateField(String caption, LocalDate value) {
        super(caption, value, DateResolution.DAY);
    }

    @Override
    protected int getDatePart(LocalDate date, DateResolution resolution) {
        LocalDate value = date;
        if (value == null) {
            value = LocalDate.of(1, 1, 1);
        }
        switch (resolution) {
        case DAY:
            return value.getDayOfMonth();
        case MONTH:
            return value.getMonthValue();
        case YEAR:
            return value.getYear();
        default:
            assert false : "Unexpected resolution argument " + resolution;
            return -1;
        }
    }

    @Override
    protected LocalDate buildDate(
            Map<DateResolution, Integer> resolutionValues) {
        return LocalDate.of(resolutionValues.get(DateResolution.YEAR),
                resolutionValues.getOrDefault(DateResolution.MONTH, 1),
                resolutionValues.getOrDefault(DateResolution.DAY, 1));
    }

    @Override
    protected RangeValidator<LocalDate> getRangeValidator() {
        return new DateRangeValidator(getDateOutOfRangeMessage(),
                getDate(getRangeStart(), getResolution()),
                getDate(getRangeEnd(), getResolution()));
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
    protected LocalDate convertFromDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneOffset.UTC)
                .toLocalDate();
    }

    @Override
    protected Date convertToDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    private LocalDate getDate(LocalDate date, DateResolution forResolution) {
        if (date == null) {
            return null;
        }
        if (forResolution == DateResolution.YEAR) {
            return date.withDayOfYear(1);
        } else if (forResolution == DateResolution.MONTH) {
            return date.withDayOfMonth(1);
        } else {
            return date;
        }
    }
}
