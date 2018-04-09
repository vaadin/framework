package com.vaadin.v7.tests.server.component.textfield;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

public class TextFieldTest {

    @Test
    public void initiallyEmpty() {
        TextField tf = new TextField();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        TextField tf = new TextField(new ObjectProperty<String>("foo"));
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextField tf = new TextField();
        tf.setValue("foobar");
        assertFalse(tf.isEmpty());
        tf.clear();
        assertTrue(tf.isEmpty());
    }

}
