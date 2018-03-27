package com.vaadin.tests.server.component.datefield;

import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

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
