package com.vaadin.tests.components.textfield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.TextField;

public class TextChangeEventsWithNonImmediateValueChange extends TestBase {
    Log l = new Log(5);

    @Override
    protected void setup() {

        TextField tf = new TextField("Default");

        TextChangeListener inputEventListener = new TextChangeListener() {

            public void textChange(TextChangeEvent event) {
                l.log("Text change event for  "
                        + event.getComponent().getCaption()
                        + ", text content currently:'" + event.getText()
                        + "' Cursor at index:" + event.getCursorPosition());
            }
        };

        tf.addListener(inputEventListener);

        // tf.setImmediate(true); // works when this is set

        tf.addListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                // TODO should not use Property.toString()
                l.log("Value change:" + event.getProperty().toString());
            }
        });

        getLayout().addComponent(tf);

        getLayout().addComponent(l);
    }

    @Override
    protected String getDescription() {
        return "Type a, pause for a second, type ENTER,  type a. Text field should not forget the last textchange event right after valuechange (enter).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6866;
    }

}