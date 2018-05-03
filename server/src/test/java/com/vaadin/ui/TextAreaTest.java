package com.vaadin.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;

public class TextAreaTest {
    @Test
    public void initiallyEmpty() {
        TextArea tf = new TextArea();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        TextArea tf = new TextArea(new ObjectProperty<String>("foo"));
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextArea tf = new TextArea();
        tf.setValue("foobar");
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

}
