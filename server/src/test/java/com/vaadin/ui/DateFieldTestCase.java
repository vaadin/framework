package com.vaadin.ui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;

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

        assertNull(dateField.getRangeStart());
    }

    @Test
    public void rangeStartIsAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date);
        assertNull(dateField.getComponentError());
    }

    @Test
    public void belowRangeStartIsNotAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date.minusDays(1));
        assertNotNull(dateField.getComponentError());
    }

    @Test
    public void rangeEndIsSetToNull() {
        dateField.setRangeEnd(null);

        assertNull(dateField.getRangeEnd());
    }

    @Test
    public void rangeEndIsAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date);
        assertNull(dateField.getComponentError());
    }

    @Test
    public void aboveRangeEndIsNotAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date.plusDays(1));
        assertNotNull(dateField.getComponentError());
    }
}
