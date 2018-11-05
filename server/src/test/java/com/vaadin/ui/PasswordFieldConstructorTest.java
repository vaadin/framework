package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;

public class PasswordFieldConstructorTest {

    @Test
    public void initiallyEmpty() {
        PasswordField passwordField = new PasswordField();
        assertTrue(passwordField.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        PasswordField passwordField = new PasswordField(null, "foobar");
        assertFalse(passwordField.isEmpty());

        passwordField.clear();
        assertTrue(passwordField.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        PasswordField passwordField = new PasswordField(valueChangeListener);

        passwordField.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        PasswordField passwordField = new PasswordField("Caption",
                "Initial value", valueChangeListener);

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        passwordField.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
