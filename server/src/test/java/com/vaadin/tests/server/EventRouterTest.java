package com.vaadin.tests.server;

import org.junit.Test;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.TextField;

public class EventRouterTest {

    int innerListenerCalls = 0;

    @Test
    public void testAddInEventListener() {
        final TextField tf = new TextField();

        final ValueChangeListener outer = new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                ValueChangeListener inner = new ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        innerListenerCalls++;
                        System.out.println("The inner listener was called");
                    }
                };

                tf.addListener(inner);
            }
        };

        tf.addListener(outer);
        tf.setValue("abc"); // No inner listener calls, adds one inner
        tf.setValue("def"); // One inner listener call, adds one inner
        tf.setValue("ghi"); // Two inner listener calls, adds one inner
        assert (innerListenerCalls == 3);
    }
}
