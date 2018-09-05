package com.vaadin.v7.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

public class TextChangeListenerLosesFocus extends TestBase {

    private final TextChangeListener listener = new TextChangeListener() {
        @Override
        public void textChange(TextChangeEvent event) {
            final String value = event.getText();
            if (value.length() > 2) {
                ((Field) event.getComponent())
                        .setValue("Updated by TextChangeListener");
            }
        }
    };

    @Override
    protected void setup() {
        TestUtils.injectCSS(getMainWindow(),
                ".v-textfield-focus, .v-textarea-focus { "
                        + " background: #E8F0FF !important }");

        AbstractTextField field = new TextField();
        field.setDebugId("test-textfield");
        field.setInputPrompt("Enter at least 3 characters");
        field.addTextChangeListener(listener);
        addComponent(field);

        field = new TextArea();
        field.setDebugId("test-textarea");
        field.setInputPrompt("Enter at least 3 characters");
        field.addTextChangeListener(listener);
        addComponent(field);

    }

    @Override
    protected String getDescription() {
        return "Updating a focused TextField overwrites the focus stylename";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11623;
    }
}
