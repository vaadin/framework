package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class TooltipStyling extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label defaultLabel = new Label(
                "I have a tooltip with default settings");
        defaultLabel.setDescription(
                "This long description should be shown with the application's default font and wrap to several lines as needed."
                        + "\n\nThis part should be on a separate line");
        defaultLabel.setId("default");
        addComponent(defaultLabel);

        Label htmlLabel = new Label("I have a tooltip with HTML contents");
        htmlLabel.setDescription("This is regular text in a tooltip."
                + "<pre>This is a pre tag inside a HTML tooltip. It should use a monospace font and by default not break to multiple lines.</pre>",
                ContentMode.HTML);
        htmlLabel.setId("html");
        addComponent(htmlLabel);
    }

    @Override
    protected String getTestDescription() {
        return "Tooltips should be shown with the regular application font and automatically wrap to multiple lines for long contents.<br />"
                + "&lt;pre> tag contents in a HTML tooltip should still behave according to browser defaults.";
    }

}
