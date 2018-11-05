package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;

public class RichTextAreaConstructorTest {

    @Test
    public void initiallyEmpty() {
        RichTextArea richTextArea = new RichTextArea();
        assertTrue(richTextArea.isEmpty());
    }

    @Test
    public void testValueConstructor_emptyAfterClear() {
        RichTextArea richTextArea = new RichTextArea(null, "foobar");
        assertFalse(richTextArea.isEmpty());

        richTextArea.clear();
        assertTrue(richTextArea.isEmpty());
    }

    @Test
    public void testValueChangeListener_eventOnValueChange() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        RichTextArea richTextArea = new RichTextArea(valueChangeListener);

        richTextArea.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }

    @Test
    public void testCaptionValueListener() {
        HasValue.ValueChangeListener valueChangeListener = Mockito
                .mock(HasValue.ValueChangeListener.class);
        RichTextArea richTextArea = new RichTextArea("Caption", "Initial value",
                valueChangeListener);

        verify(valueChangeListener, never())
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

        richTextArea.setValue("value change");

        verify(valueChangeListener)
                .valueChange(Mockito.any(HasValue.ValueChangeEvent.class));

    }
}
