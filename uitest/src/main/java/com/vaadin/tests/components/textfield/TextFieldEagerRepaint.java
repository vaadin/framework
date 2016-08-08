package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.legacy.ui.LegacyAbstractTextField.TextChangeEventMode;
import com.vaadin.tests.components.TestBase;

public class TextFieldEagerRepaint extends TestBase {

    @Override
    protected void setup() {

        final LegacyTextField tf1 = new LegacyTextField("Updates value");
        tf1.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf1.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                String text = event.getText();
                if (!text.matches("[a-z]*")) {
                    String newValue = text.replaceAll("[^a-z]", "");
                    tf1.setValue(newValue);
                }
            }
        });

        final LegacyTextField tf2 = new LegacyTextField("Updates width");
        tf2.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf2.addTextChangeListener(new TextChangeListener() {
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
