package com.vaadin.tests.components.customfield;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * An example of a custom field for editing a boolean value. The field is
 * composed of multiple components, and could also edit a more complex data
 * structures. Here, the commit etc. logic is not overridden.
 */
public class BooleanField extends CustomField<Boolean> {
    private final Button button = new Button("Off");
    private boolean value;

    @Override
    protected Component initContent() {
        button.addClickListener(event -> {
            setValue(!getValue());
            button.setCaption(getValue() ? "On" : "Off");
        });

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("Please click the button"));
        layout.addComponent(button);
        return layout;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    protected void doSetValue(Boolean value) {
        this.value = value;
        button.setCaption(value ? "On" : "Off");
    }
}
