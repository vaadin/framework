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

            @Override
            public void textChange(TextChangeEvent event) {
                l.log("Text change event, text content currently:'"
                        + event.getText() + "' Cursor at index:"
                        + event.getCursorPosition());
            }
        };

        tf.addListener(inputEventListener);

        // tf.setImmediate(true); // works when this is set

        tf.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                l.log("Value change: '" + event.getProperty().getValue() + "'");
            }
        });

        getLayout().addComponent(tf);

        getLayout().addComponent(l);
    }

    @Override
    protected String getDescription() {
        return "Type a, pause for a second, type ENTER,  type a. Text field should not forget the last textchange event right after valuechange (enter)."
                + "<br />Then press backspace. The text field should send a text change event even though the text in the field is the same as the field's value";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6866;
    }

}
