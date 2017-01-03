package com.vaadin.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateFieldTestCase {

    private AbstractLocalDateField dateField;
    private LocalDate date;

    @Before
    public void setup() {
        dateField = new AbstractLocalDateField() {
        };
        date = LocalDate.now();
    }

    @Test
    public void rangeStartIsSetToNull() {
        dateField.setRangeStart(null);

        assertThat(dateField.getRangeStart(), is(nullValue()));
    }

    @Test
    public void rangeStartIsAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date);
        Assert.assertNull(dateField.getComponentError());
    }

    @Test
    public void belowRangeStartIsNotAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date.minusDays(1));
        Assert.assertNotNull(dateField.getComponentError());
    }

    @Test
    public void rangeEndIsSetToNull() {
        dateField.setRangeEnd(null);

        assertThat(dateField.getRangeEnd(), is(nullValue()));
    }

    @Test
    public void rangeEndIsAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date);
        Assert.assertNull(dateField.getComponentError());
    }

    @Test
    public void aboveRangeEndIsNotAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date.plusDays(1));
        Assert.assertNotNull(dateField.getComponentError());
    }
}
