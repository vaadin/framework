package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.TextField;

public class TextFieldEagerRepaint extends TestBase {

    @Override
    protected void setup() {

        final TextField tf1 = new TextField("Updates value");
        tf1.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf1.addListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                String text = event.getText();
                if (!text.matches("[a-z]*")) {
                    String newValue = text.replaceAll("[^a-z]", "");
                    tf1.setValue(newValue);
                }
            }
        });

        final TextField tf2 = new TextField("Updates width");
        tf2.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf2.addListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                String text = event.getText();
                if (!text.matches("[a-z]*")) {
                    tf2.setWidth("100px");
                } else {
                    tf2.setWidth("150px");
                }
            }
        });

        addComponent(tf1);
        addComponent(tf2);
    }

    @Override
    protected String getDescription() {
        return "Updating the value in an EAGER TextChangeListener should send the new value to the client while updating something else (e.g. the width) should preserve the text in the field. Both fields react when the field contains anything else than lower case letters a-z";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6588);
    }

}
