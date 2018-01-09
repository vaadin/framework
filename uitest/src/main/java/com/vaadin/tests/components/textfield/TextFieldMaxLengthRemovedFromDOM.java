package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.TextField;

public class TextFieldMaxLengthRemovedFromDOM extends TestBase {

    @Override
    protected void setup() {
        final TextField tf = new TextField();
        tf.setMaxLength(11);
        tf.setRequired(true);
        tf.setImmediate(true);
        addComponent(tf);

        tf.addFocusListener(event -> {
            // Resetting Max length should not remove maxlength attribute
            tf.setMaxLength(11);
        });
    }

    @Override
    protected String getDescription() {
        return "Maxlength attribute should not dissappear from the DOM when I focus the text field.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9940;
    }

}
