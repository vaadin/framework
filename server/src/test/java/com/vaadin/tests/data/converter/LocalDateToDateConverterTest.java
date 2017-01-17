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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.ui.DateField;

public class LocalDateToDateConverterTest extends AbstractConverterTest {

    private static final String TIME_ZONE = "UTC";

    private static final LocalDate LOCAL_DATE = LocalDate.of(2017, 1, 1);
    private static final Date DATE = createDate();

    @Override
    protected LocalDateToDateConverter getConverter() {
        return new LocalDateToDateConverter(ZoneId.of(TIME_ZONE));
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
        DateField dateField = new DateField();

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
                .getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.clear();
        calendar.set(2017, Calendar.JANUARY, 1);
        return calendar.getTime();
    }

}
