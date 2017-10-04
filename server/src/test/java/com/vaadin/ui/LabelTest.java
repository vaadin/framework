package com.vaadin.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LabelTest {

    @Test
    public void emptyLabelValue() {
        assertEquals("", new Label().getValue());
    }

    @Test
    public void labelInitialValue() {
        assertEquals("initial", new Label("initial").getValue());
    }

    @Test
    public void labelSetValue() {
        Label label = new Label();
        label.setValue("foo");
        assertEquals("foo", label.getValue());
    }

}
