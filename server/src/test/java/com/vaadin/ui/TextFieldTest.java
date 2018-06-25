package com.vaadin.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;

public class TextFieldTest {

    @Test
    public void initiallyEmpty() {
        TextField tf = new TextField();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        TextField tf = new TextField(new ObjectProperty<String>("foo"));
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextField tf = new TextField();
        tf.setValue("foobar");
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

}
