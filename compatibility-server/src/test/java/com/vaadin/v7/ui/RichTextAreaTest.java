package com.vaadin.v7.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.util.ObjectProperty;

public class RichTextAreaTest {
    @Test
    public void initiallyEmpty() {
        RichTextArea tf = new RichTextArea();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        RichTextArea tf = new RichTextArea(new ObjectProperty<String>("foo"));
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        RichTextArea tf = new RichTextArea();
        tf.setValue("foobar");
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

}
