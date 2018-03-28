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

public class InlineDateFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        InlineDateField dateField = new InlineDateField();
        assertTrue(dateField.isEmpty());
        assertEquals(DateResolution.DAY, dateField.getResolution());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        InlineDateField dateField = new InlineDateField(null, LocalDate.now());
        assertEquals(DateResolution.DAY, dateField.getResolution());
        assertFalse(dateField.isEmpty());

        dateField.clear();
        assertTrue(dateField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        InlineDateField dateField = new InlineDateField(valueChangeListener);
        assertEquals(DateResolution.DAY, dateField.getResolution());

        dateField.setValue(LocalDate.now());

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        InlineDateField dateField = new InlineDateField("Caption",
                LocalDate.now(), valueChangeListener);
        assertEquals(DateResolution.DAY, dateField.getResolution());

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        dateField.setValue(LocalDate.now().plusDays(1));

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
