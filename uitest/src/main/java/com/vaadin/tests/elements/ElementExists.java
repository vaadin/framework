package com.vaadin.tests.elements;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;

public class ElementExists extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("b");
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "The TestBench method exists() should not throw an exception even when the UI "
                + "has not been initialized.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14808;
    }

}
