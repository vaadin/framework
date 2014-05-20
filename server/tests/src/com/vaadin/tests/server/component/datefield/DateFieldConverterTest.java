/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.tests.server.component.datefield;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

public class DateFieldConverterTest extends TestCase {

    private Property<Long> date;
    private DateField datefield;

    @Override
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
    public void testResolution() {
        datefield.setValue(new Date(110, 0, 1));
        datefield.setResolution(Resolution.MINUTE);
        datefield.validate();
    }
}
