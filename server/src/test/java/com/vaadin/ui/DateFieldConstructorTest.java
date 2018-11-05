package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.datefield.DateResolution;

public class DateFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        DateField dateField = new DateField();
        assertTrue(dateField.isEmpty());
        assertEquals(DateResolution.DAY, dateField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        DateField dateField = new DateField(null, LocalDate.now());
        assertEquals(DateResolution.DAY, dateField.getResolution());
        assertFalse(dateField.isEmpty());

        dateField.clear();
        assertTrue(dateField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        DateField dateField = new DateField(valueChangeListener);
        assertEquals(DateResolution.DAY, dateField.getResolution());

        dateField.setValue(LocalDate.now());

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        DateField dateField = new DateField("Caption", LocalDate.now(),
                valueChangeListener);
        assertEquals(DateResolution.DAY, dateField.getResolution());

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        dateField.setValue(LocalDate.now().plusDays(1));

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
