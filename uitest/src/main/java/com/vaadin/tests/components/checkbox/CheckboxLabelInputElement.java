package com.vaadin.tests.components.checkbox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;

public class CheckboxLabelInputElement extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox cb = new CheckBox("Test custom style names for inner elements", true);
        cb.getInputElement().addStyleName("my-input-class");
        cb.getLabelElement().addStyleName("my-label-class");

        addComponent(cb);

        addButton("add-style", e -> {
            cb.getInputElement().addStyleName("later-applied-input-class");
            cb.getLabelElement().addStyleName("later-applied-label-class");
        });

        addButton("remove-style", e -> {
            cb.getInputElement().removeStyleName("my-input-class");
            cb.getLabelElement().removeStyleName("my-label-class");
        });

        addButton("remove-style-2", e -> {
            cb.getInputElement().removeStyleName("later-applied-input-class");
            cb.getLabelElement().removeStyleName("later-applied-label-class");
        });
    }

}
