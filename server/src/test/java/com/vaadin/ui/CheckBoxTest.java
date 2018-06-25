package com.vaadin.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;

public class CheckBoxTest {
    @Test
    public void initiallyEmpty() {
        CheckBox tf = new CheckBox();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClearUsingPDS() {
        CheckBox tf = new CheckBox();
        tf.setPropertyDataSource(new ObjectProperty<Boolean>(Boolean.TRUE));
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        CheckBox tf = new CheckBox();
        tf.setValue(true);
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }

}
