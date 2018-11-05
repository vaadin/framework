package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;

public class TextFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        TextField textField = new TextField();
        assertTrue(textField.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        TextField textField = new TextField(null, "foobar");
        assertFalse(textField.isEmpty());

        textField.clear();
        assertTrue(textField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        TextField textField = new TextField(valueChangeListener);

        textField.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));
    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        TextField textField = new TextField("Caption", "Initial value",
                valueChangeListener);

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        textField.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));
    }
}
