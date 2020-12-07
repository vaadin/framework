package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class MobileHtml5DndEnablingError extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final MobileHtml5DndEnablingError ui = MobileHtml5DndEnablingError.this;
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                ui.access(() -> {
                    Label label;
                    try {
                        ui.setMobileHtml5DndEnabled(true);
                        label = new Label(
                                "If you see this, there was no error.");
                    } catch (Exception e) {
                        label = new Label("Error message: " + e.getMessage());
                    }
                    label.setId("error");
                    label.setSizeFull();
                    ui.addComponent(label);
                });
            }
        }.start();
    }

    @Override
    protected String getTestDescription() {
        return "Attempting to set HTML5 DnD enabled for mobile devices from "
                + "a thread should give an informative error message.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12152;
    }
}
