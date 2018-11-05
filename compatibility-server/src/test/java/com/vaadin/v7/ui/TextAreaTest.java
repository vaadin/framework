package com.vaadin.v7.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.util.ObjectProperty;

public class TextAreaTest {
    @Test
    public void initiallyEmpty() {
        TextArea tf = new TextArea();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        TextArea tf = new TextArea(new ObjectProperty<String>("foo"));
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextArea tf = new TextArea();
        tf.setValue("foobar");
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

}
