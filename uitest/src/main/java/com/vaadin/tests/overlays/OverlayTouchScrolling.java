/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.overlays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class OverlayTouchScrolling extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final CssLayout green = new CssLayout();
        green.setSizeFull();
        final CssLayout layout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "background:green;";
            }
        };
        layout.setSizeFull();
        layout.addComponent(green);
        setContent(layout);

        Button button = new Button("Tap me with a touch device");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                Notification
                        .show("Now close this and you can scroll in mad places.");
                green.addComponent(new Label(
                        "Thank you for clicking, now scroll (with touch device) to area without green background, which shouldn't be possible."));
            }
        });
        green.addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Using overlays breaks top level scrolling on touch devices";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10860;
    }
}
