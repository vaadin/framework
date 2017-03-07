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

package com.vaadin.v7.tests.server.component.datefield;

import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.DateField;

public class DateFieldConverterTest {

    private Property<Long> date;
    private DateField datefield;

    @Before
    public void setUp() {
        date = new ObjectProperty<Long>(0L);
        datefield = new DateField();
        datefield.setBuffered(false);
        datefield.setConverter(new Converter<Date, Long>() {

            @Override
            public Long convertToModel(Date value,
                    Class<? extends Long> targetType, Locale locale)
                    throws ConversionException {
                return value.getTime();
            }

            @Override
            public Date convertToPresentation(Long value,
                    Class<? extends Date> targetType, Locale locale)
                    throws ConversionException {
                return new Date(value);
            }

            @Override
            public Class<Long> getModelType() {
                return Long.class;
            }

            @Override
            public Class<Date> getPresentationType() {
                return Date.class;
            }
        });
        datefield.setPropertyDataSource(date);
    }

    /*
     * See #12193.
     */
    @Test
    public void testResolution() {
        datefield.setValue(new Date(110, 0, 1));
        datefield.setResolution(Resolution.MINUTE);
        datefield.validate();
    }
}
