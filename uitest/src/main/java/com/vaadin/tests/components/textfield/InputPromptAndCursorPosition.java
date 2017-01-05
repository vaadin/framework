package com.vaadin.tests.components.textfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class InputPromptAndCursorPosition extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TextField tf = new TextField();
        tf.setWidth("40em");
        tf.setValue(
                "Delete this text to reveal input prompt and update cursor position.");
        tf.setPlaceholder("This is an input prompt");

        final Label l = new Label("Cursor position: ?");
        Button button = new Button("Update cursor position",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        l.setValue(
                                "Cursor position: " + tf.getCursorPosition());
                    }
                });

        addComponent(tf);
        addComponent(l);
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Cursor position should always be zero when input prompt is displayed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19766;
    }
}
