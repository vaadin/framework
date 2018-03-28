package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

public class ResynchronizeUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Resynchronize", event -> {
            // Theme change is currently the only operation which always
            // does resynchronize
            setTheme("runo");
            log("Set theme to runo");
        });
        addComponent(b);
    }
}
