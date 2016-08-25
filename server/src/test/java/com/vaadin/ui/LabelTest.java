package com.vaadin.ui;

import org.junit.Assert;
import org.junit.Test;

public class LabelTest {

    @Test
    public void emptyLabelValue() {
        Assert.assertEquals("", new Label().getValue());
    }

    @Test
    public void labelInitialValue() {
        Assert.assertEquals("initial", new Label("initial").getValue());
    }

    @Test
    public void labelSetValue() {
        Label label = new Label();
        label.setValue("foo");
        Assert.assertEquals("foo", label.getValue());
    }

}
