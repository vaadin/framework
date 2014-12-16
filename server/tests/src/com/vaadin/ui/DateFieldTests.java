package com.vaadin.ui;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class DateFieldTests {

    private DateField dateField;
    private Date date;

    @Before
    public void setup() {
        dateField = new DateField();
        date = new Date();
    }

    @Test
    public void rangeStartIsSetToNull() {
        dateField.setRangeStart(null);

        assertThat(dateField.getRangeStart(), is(nullValue()));
    }

    @Test
    public void rangeStartIsImmutable() {
        long expectedTime = date.getTime();

        dateField.setRangeStart(date);
        date.setTime(expectedTime + 1);

        assertThat(dateField.getRangeStart().getTime(), is(expectedTime));
    }

    @Test
    public void rangeEndIsSetToNull() {
        dateField.setRangeEnd(null);

        assertThat(dateField.getRangeEnd(), is(nullValue()));
    }

    @Test
    public void rangeEndIsImmutable() {
        long expectedTime = date.getTime();

        dateField.setRangeEnd(date);
        date.setTime(expectedTime + 1);

        assertThat(dateField.getRangeEnd().getTime(), is(expectedTime));
    }
}
