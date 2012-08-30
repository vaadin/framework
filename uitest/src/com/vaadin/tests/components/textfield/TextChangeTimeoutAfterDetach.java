package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class TextChangeTimeoutAfterDetach extends TestBase {

    @Override
    protected void setup() {
        final TextField field = new TextField();
        field.setImmediate(false);
        field.setTextChangeTimeout(2000);
        field.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
        field.addListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                // Need to add a listener for events to occur
            }
        });
        addComponent(field);

        Button detachBtn = new Button("detach field",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        removeComponent(field);
                        getLayout().addComponentAsFirst(
                                new Label("Field detached!"));
                    }
                });
        addComponent(detachBtn);
    }

    @Override
    protected String getDescription() {
        return "The textfield has a TextChangeTimout of 1 second. Edit the field and immidietly detach the field and you will cause an \"Out Of Sync\" error.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6507;
    }

}
