package com.vaadin.demo.sampler.features.commons;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ErrorsExample extends VerticalLayout {

    public ErrorsExample() {
        setSpacing(true);

        Panel panel = new Panel("Configure this");
        panel.setComponentError(new UserError("'Input' contains an error"));
        addComponent(panel);

        TextField input = new TextField("Input");
        input
                .setComponentError(new UserError(
                        "This field is never satisfied."));
        panel.addComponent(input);

    }
}
