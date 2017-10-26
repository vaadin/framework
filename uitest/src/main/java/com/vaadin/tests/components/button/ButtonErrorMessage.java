package com.vaadin.tests.components.button;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;

public class ButtonErrorMessage extends TestBase {

    @Override
    protected void setup() {
        Button b = new Button("Click for error");
        b.addClickListener(event -> {
            throw new NullPointerException();
        });
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Click the button for an exception. The exception should not contain any extra ',' characters";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3303;
    }

}
