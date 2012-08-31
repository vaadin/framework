package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.TextField;

public class IE6Cursor extends TestBase {

    @Override
    protected void setup() {
        TextField tf1 = new TextField("First");
        TextField tf2 = new TextField("Second");
        tf2.setInputPrompt("prompt");

        addComponent(tf1);
        addComponent(tf2);
    }

    @Override
    protected String getDescription() {
        return "Tabbing from the first field to the second should clear the second textfield and show the normal, blinking cursor in the field";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3343;
    }

}
