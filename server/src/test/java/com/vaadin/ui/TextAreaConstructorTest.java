package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;

public class TextAreaConstructorTest {

    @Test
    public void initiallyEmpty() {
        TextArea textArea = new TextArea();
        assertTrue(textArea.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        TextArea textArea = new TextArea(null, "foobar");
        assertFalse(textArea.isEmpty());

        textArea.clear();
        assertTrue(textArea.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        TextArea textArea = new TextArea(valueChangeListener);

        textArea.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        TextArea textArea = new TextArea("Caption", "Initial value",
                valueChangeListener);

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        textArea.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
