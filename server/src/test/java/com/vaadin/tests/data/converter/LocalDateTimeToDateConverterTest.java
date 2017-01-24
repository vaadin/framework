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
package com.vaadin.tests.data.converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.ui.DateTimeField;

public class LocalDateTimeToDateConverterTest extends AbstractConverterTest {

    private static final LocalDateTime LOCAL_DATE = LocalDateTime.of(2017, 1, 1,
            1, 1, 1);
    private static final Date DATE = createDate();

    @Override
    protected LocalDateTimeToDateConverter getConverter() {
        return new LocalDateTimeToDateConverter(ZoneOffset.UTC);
    }

    @Test
    public void testToModel() {
        assertValue(DATE,
                getConverter().convertToModel(LOCAL_DATE, new ValueContext()));
    }

    @Test
    public void testToModelFromSqlDate() {
        // Check that SQL dates also work (e.g. java.sql.Date.toInstant throws)
        assertValue(new java.sql.Date(DATE.getTime()),
                getConverter().convertToModel(LOCAL_DATE, new ValueContext()));
    }

    @Test
    public void testToPresentation() {
        Assert.assertEquals(LOCAL_DATE,
                getConverter().convertToPresentation(DATE, new ValueContext()));
    }

    @Test
    public void useWithBinder() throws ValidationException {
        Binder<BeanWithDate> binder = new Binder<>();
        DateTimeField dateField = new DateTimeField();

        binder.forField(dateField).withConverter(getConverter())
                .bind(BeanWithDate::getDate, BeanWithDate::setDate);

        dateField.setValue(LOCAL_DATE);

        BeanWithDate bean = new BeanWithDate();
        binder.writeBean(bean);

        Assert.assertEquals(DATE, bean.getDate());
    }

    public static class BeanWithDate {
        private Date date;

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }
    }

    private static Date createDate() {
        Calendar calendar = Calendar
                .getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
        calendar.clear();
        calendar.set(2017, Calendar.JANUARY, 1, 1, 1, 1);
        return calendar.getTime();
    }

}
