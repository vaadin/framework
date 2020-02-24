package com.vaadin.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class DateFieldTestCase {

    private AbstractLocalDateField dateField;
    private LocalDate date;

    @Rule
    public transient ExpectedException exceptionRule = ExpectedException.none();

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
        assertNull(dateField.getComponentError());
    }

    @Test
    public void belowRangeStartIsNotAcceptedAsValue() {
        LocalDate currentDate = dateField.getValue();
        dateField.setRangeStart(date);
        exceptionRule.expect(IllegalArgumentException.class);
        dateField.setValue(date.minusDays(1));
        assertThat(dateField.getValue(), is(currentDate));
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
        assertNull(dateField.getComponentError());
    }

    @Test
    public void aboveRangeEndIsNotAcceptedAsValue() {
        LocalDate currentDate = dateField.getValue();
        dateField.setRangeEnd(date);
        exceptionRule.expect(IllegalArgumentException.class);
        dateField.setValue(date.plusDays(1));
        assertThat(dateField.getValue(), is(currentDate));
    }
}
