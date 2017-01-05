package com.vaadin.v7.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.TextField;

public class TextChangeEventsEternalLoop extends TestBase {
    @Override
    protected void setup() {

        final TextField tf = new TextField("Debug");
        getLayout().addComponent(tf);

        tf.addValueChangeListener(listener -> tf.setValue(listener.getValue()));
    }

    @Override
    protected String getDescription() {
        return "Moving current text to value in text change listener should not cause eternal loop.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6376;
    }

}
