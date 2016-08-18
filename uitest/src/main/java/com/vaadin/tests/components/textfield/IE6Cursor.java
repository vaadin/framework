package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.LegacyTextField;

public class IE6Cursor extends TestBase {

    @Override
    protected void setup() {
        LegacyTextField tf1 = new LegacyTextField("First");
        LegacyTextField tf2 = new LegacyTextField("Second");
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
