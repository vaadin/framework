package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextAreaTest {
    @Test
    public void initiallyEmpty() {
        TextArea textArea = new TextArea();
        assertTrue(textArea.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextArea textArea = new TextArea();
        textArea.setValue("foobar");
        assertFalse(textArea.isEmpty());
        textArea.clear();
        assertTrue(textArea.isEmpty());
    }

}
